package com.moodle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Staff_Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference, storageUrl;
    //private FirebaseAnalytics mFirebaseAnalytics;

    String username, upload;
    String storage_profile_url = "gs://moodle-9546f.appspot.com/profiles/staff/";
    String storage_uploads_url = "uploads/";
    String docs_url, notices_url, quiz_url;
    ArrayList<String> sub;
    private static final int PICKFILE_RESULT_CODE = 1;
    DocsData docs_data;
    NoticesData notices_data;

    DocsFragment d = new DocsFragment();
    NoticesFragment n = new NoticesFragment();
    StaffQuizFragment q = new StaffQuizFragment();

    TextView full_name;
    TextView email;
    NavigationView navigationView;
    View headerLayout;
    FloatingActionButton docs_btn, notices_btn, quiz_btn;
    LinearLayout docs_layout, notices_layout, quiz_layout;
    AlertDialog dialog;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_staff_home);

        //Get username
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");

        /*// Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Track Screen View
        mFirebaseAnalytics.setCurrentScreen(this, username+" - Staff Home", null);

        bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, username);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Staff Home");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Bundle params = new Bundle();
        params.putString("image_name", "Staff Home");
        params.putString("full_text", "ABCD");
        mFirebaseAnalytics.logEvent("share_image", params);*/

        progressDialog = new ProgressDialog(this);

        //Get navigation bar and header
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        headerLayout = LayoutInflater.from(this).inflate(R.layout.nav_header_staff_home, null);
        navigationView.addHeaderView(headerLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //For tabbed activity
        ViewPager vp_pages= (ViewPager) findViewById(R.id.vp_pages);
        PagerAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        vp_pages.setAdapter(pagerAdapter);

        TabLayout tbl_pages= (TabLayout) findViewById(R.id.tbl_pages);
        tbl_pages.setupWithViewPager(vp_pages);

        //Storage Reference
        storageReference = FirebaseStorage.getInstance().getReference();

        //Staff data
        DatabaseReference databaseReference = database.getReference("staff"); //users is a node in your Firebase Database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Staff staff = child.getValue(Staff.class);
                    if(username.equals(staff.getUsername())) {
                        //Set profile image
                        final ImageView profile_image = (ImageView) headerLayout.findViewById(R.id.profile_image);
                        storageUrl = storage.getReferenceFromUrl(storage_profile_url).child(username+".png");
                        Glide.with(getApplicationContext())
                                .using(new FirebaseImageLoader())
                                .load(storageUrl)
                                .override(200, 200)
                                .centerCrop()
                                .into(profile_image);

                        //Set full_name
                        full_name = (TextView) headerLayout.findViewById(R.id.full_name);
                        full_name.setText(staff.getFull_name());

                        //Set email
                        email = (TextView) headerLayout.findViewById(R.id.email);
                        email.setText(staff.getEmail());
                        
                        //Set subjects
                        Menu nav_menu = navigationView.getMenu();
                        sub = new ArrayList<String>(staff.getSubjects());
                        for(int i=0; i<sub.size(); i++) {
                            nav_menu.add(R.id.subjects, i, i, sub.get(i));
                        }

                        //By default 1st subject selected
                        docs_url = storage_uploads_url + sub.get(0) + "/" + username + "/Docs/";
                        notices_url = storage_uploads_url + sub.get(0) + "/" + username + "/Notices/";
                        quiz_url = storage_uploads_url + sub.get(0) + "/" + username + "/Quiz/";

                        d.displayList(docs_url);
                        n.displayList(notices_url);
                        q.displayList(username, quiz_url, getApplicationContext());

                        getSupportActionBar().setTitle(sub.get(0));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //FloatingActionButton add --> menu
        docs_btn = (FloatingActionButton) findViewById(R.id.docs_btn);
        notices_btn = (FloatingActionButton) findViewById(R.id.notices_btn);
        quiz_btn = (FloatingActionButton) findViewById(R.id.quiz_btn);

        docs_layout = (LinearLayout) findViewById(R.id.docs_layout);
        notices_layout = (LinearLayout) findViewById(R.id.notices_layout);
        quiz_layout = (LinearLayout) findViewById(R.id.quiz_layout);

        //Initially keep menu closed
        docs_layout.setVisibility(View.GONE);
        notices_layout.setVisibility(View.GONE);
        quiz_layout.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(docs_layout.getVisibility()==View.VISIBLE
                        && notices_layout.getVisibility()==View.VISIBLE
                        && quiz_layout.getVisibility()==View.VISIBLE ) {
                    docs_layout.setVisibility(View.GONE);
                    notices_layout.setVisibility(View.GONE);
                    quiz_layout.setVisibility(View.GONE);
                } else {
                    docs_layout.setVisibility(View.VISIBLE);
                    notices_layout.setVisibility(View.VISIBLE);
                    quiz_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        //onClickListener of menu items
        docs_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Upload doc
                upload = "docs";
                Intent docs_intent = new Intent(Intent.ACTION_PICK);
                docs_intent.setType("*/*");
                startActivityForResult(docs_intent, PICKFILE_RESULT_CODE);

                //Close menu
                docs_layout.setVisibility(View.GONE);
                notices_layout.setVisibility(View.GONE);
                quiz_layout.setVisibility(View.GONE);
            }
        });

        notices_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Upload notice
                upload = "notices";
                Intent notices_intent = new Intent(Intent.ACTION_PICK);
                notices_intent.setType("*/*");
                startActivityForResult(notices_intent, PICKFILE_RESULT_CODE);

                //Close menu
                docs_layout.setVisibility(View.GONE);
                notices_layout.setVisibility(View.GONE);
                quiz_layout.setVisibility(View.GONE);
            }
        });

        quiz_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Upload quiz
                Intent intent = new Intent(Staff_Home.this, UploadQuiz.class);
                Bundle bundle = new Bundle();
                bundle.putString("quiz_url", quiz_url);
                intent.putExtras(bundle);
                startActivity(intent);

                q.displayList(username, quiz_url, getApplicationContext());

                //Close menu
                docs_layout.setVisibility(View.GONE);
                notices_layout.setVisibility(View.GONE);
                quiz_layout.setVisibility(View.GONE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Staff_Home.this);
            View mView = getLayoutInflater().inflate(R.layout.upload_docs_notices, null);

            final EditText file_name = (EditText) mView.findViewById(R.id.file_name);
            final Button upload_docs_notices = (Button) mView.findViewById(R.id.upload_docs_notices);

            upload_docs_notices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setTitle("Uploading..");
                    progressDialog.show();
                    String filename = file_name.getText().toString();

                    if(filename.isEmpty()) {
                        Toast.makeText(Staff_Home.this, "Please enter a file name", Toast.LENGTH_SHORT).show();
                    } else if(!isAlphaNumeric(filename)) {
                        Toast.makeText(Staff_Home.this, "Filename should contain only alphanumeric characters and underscore allowed", Toast.LENGTH_LONG).show();
                    } else {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

                        if(upload.equals("docs")) {
                            docs_data = new DocsData();
                            docs_data.setName(filename);
                            docs_data.setDatetime(format1.format(c.getTime()));
                        } else if(upload.equals("notices")) {
                            notices_data = new NoticesData();
                            notices_data.setName(filename);
                            notices_data.setDatetime(format1.format(c.getTime()));
                        }

                        filename += "_"+Long.toString(c.getTimeInMillis());

                        Uri uri = data.getData();

                        StorageReference filepath = null;

                        if(upload.equals("docs")) {
                            filepath = storageReference.child(docs_url).child(filename);
                            docs_data.setUrl(filepath.toString().replace("%20"," "));
                        } else if(upload.equals("notices")) {
                            filepath = storageReference.child(notices_url).child(filename);
                            notices_data.setUrl(filepath.toString().replace("%20"," "));
                        }

                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Store doc uploaded info in database
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://moodle-9546f.firebaseio.com/");
                                DatabaseReference databaseReference;
                                if(upload.equals("docs")) {
                                    databaseReference = database.getReference(docs_url);
                                    databaseReference.push().setValue(docs_data);
                                } else if(upload.equals("notices")) {
                                    databaseReference = database.getReference(notices_url);
                                    databaseReference.push().setValue(notices_data);
                                }

                                dialog.dismiss();
                                progressDialog.dismiss();

                                d.displayList(docs_url);
                                n.displayList(notices_url);

                                Toast.makeText(Staff_Home.this, "Upload Complete!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                progressDialog.dismiss();
                                Toast.makeText(Staff_Home.this, "Failed to upload.. Please try again!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });

            builder.setView(mView);
            dialog = builder.create();
            dialog.show();
        }
    }

    public boolean isAlphaNumeric(String s){
        String pattern= "^[a-zA-Z0-9_\\x20]*$";
        return s.matches(pattern);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();

            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Are you sure you want to exit??");
            alertBuilder.setCancelable(false);

            alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Staff_Home.this, Login.class);
                    finishAffinity();
                    startActivity(intent);
                }
            });

            alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alertBuilder.create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.staff_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(Staff_Home.this, Login.class);
            finishAffinity();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        docs_url = storage_uploads_url + sub.get(id) + "/" + username + "/Docs/";
        notices_url = storage_uploads_url + sub.get(id) + "/" + username + "/Notices/";
        quiz_url = storage_uploads_url + sub.get(id) + "/" + username + "/Quiz/";

        d.displayList(docs_url);
        n.displayList(notices_url);
        q.displayList(username, quiz_url, getApplicationContext());

        getSupportActionBar().setTitle(sub.get(id));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return(d);
                case 1:
                    return(n);
                case 2:
                    return(q);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                //
                //Your tab titles
                //
                case 0:return "Documents";
                case 1:return "Notices";
                case 2: return "Quiz";
                default:return null;
            }
        }
    }
}