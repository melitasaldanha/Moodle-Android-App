package com.moodle;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Student_Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference, storageUrl, docsDownloadUrl;

    String username, upload;
    String storage_profile_url = "gs://moodle-9546f.appspot.com/profiles/students/";
    String storage_uploads_url = "uploads/";
    String docs_url, notices_url, quiz_url;
    ArrayList<HashMap<String, String>> sub;

    DocsFragment d = new DocsFragment();
    NoticesFragment n = new NoticesFragment();
    StudentQuizFragment q = new StudentQuizFragment();

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
        setContentView(R.layout.activity_student_home);

        progressDialog = new ProgressDialog(this);

        //Get navigation bar and header
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        headerLayout = LayoutInflater.from(this).inflate(R.layout.nav_header_staff_home, null);
        navigationView.addHeaderView(headerLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get username
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");

        //For tabbed activity
        ViewPager vp_pages= (ViewPager) findViewById(R.id.vp_pages);
        PagerAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        vp_pages.setAdapter(pagerAdapter);

        TabLayout tbl_pages= (TabLayout) findViewById(R.id.tbl_pages);
        tbl_pages.setupWithViewPager(vp_pages);

        //Storage Reference
        storageReference = FirebaseStorage.getInstance().getReference();

        //Student data
        DatabaseReference databaseReference = database.getReference("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Student student = child.getValue(Student.class);
                    if(username.equals(student.getUsername())) {
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
                        full_name.setText(student.getFull_name());

                        //Set email
                        email = (TextView) headerLayout.findViewById(R.id.email);
                        email.setText(student.getEmail());

                        //Set subjects
                        Menu nav_menu = navigationView.getMenu();
                        sub = new ArrayList<HashMap<String, String>>(student.getSubjects());
                        for(int i=0; i<sub.size(); i++) {
                            nav_menu.add(R.id.subjects, i, i, sub.get(i).get("Name"));
                        }

                        //By default 1st subject selected
                        docs_url = storage_uploads_url + sub.get(0).get("Name") + "/" + sub.get(0).get("Faculty") + "/Docs/";
                        notices_url = storage_uploads_url + sub.get(0).get("Name") + "/" + sub.get(0).get("Faculty") + "/Notices/";
                        quiz_url = storage_uploads_url + sub.get(0).get("Name") + "/" + sub.get(0).get("Faculty") + "/Quiz/";

                        d.displayList(docs_url);
                        n.displayList(notices_url);
                        q.displayList(username, quiz_url, getApplicationContext());

                        getSupportActionBar().setTitle(sub.get(0).get("Name"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                    Intent intent = new Intent(Student_Home.this, Login.class);
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
        getMenuInflater().inflate(R.menu.student_home, menu);
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
            Intent intent = new Intent(Student_Home.this, Login.class);
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

        docs_url = storage_uploads_url + sub.get(id).get("Name") + "/" + sub.get(id).get("Faculty") + "/Docs/";
        notices_url = storage_uploads_url + sub.get(id).get("Name") + "/" + sub.get(id).get("Faculty") + "/Notices/";
        quiz_url = storage_uploads_url + sub.get(id).get("Name") + "/" + sub.get(id).get("Faculty") + "/Quiz/";

        d.displayList(docs_url);
        n.displayList(notices_url);
        q.displayList(username, quiz_url, getApplicationContext());

        getSupportActionBar().setTitle(sub.get(id).get("Name"));

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