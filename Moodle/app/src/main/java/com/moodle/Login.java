package com.moodle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.widget.Toast.makeText;

/**
 * Created by Melita Saldanha on 10-03-2018.
 */

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private ProgressDialog progressDialog;
    private String username, password;
    //private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /*// Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Track Screen View
        mFirebaseAnalytics.setCurrentScreen(this, "Login", null);*/

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mLoginButton) {
            attemptLogin();
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        username = mUsernameView.getText().toString();
        EncryptPassword ep = new EncryptPassword();
        password = ep.encryptPassword(mPasswordView.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError("Invalid Username");
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //Authenticate user
            try {
                int no = Integer.parseInt(username);
                authenticateStudent();
            } catch(NumberFormatException nfe) {
                authenticateStaff();
            }
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 3;
    }
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    public void authenticateStudent()
    {
        /*Student user = new Student(); //ObjectClass for Users
        user.setUsername(username);
        user.setPassword(password);
        user.setFull_name("Shriya Shet");
        user.setEmail("shriyashet@gmail.com");
        user.setBranch("Computer");
        user.setSem("6");
        ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hm;

        hm = new HashMap<String, String>();
        hm.put("Name", "MCC");
        hm.put("Faculty", "staff1");
        al.add(hm);

        hm = new HashMap<String, String>();
        hm.put("Name", "NPL");
        hm.put("Faculty", "staff1");
        al.add(hm);

        hm = new HashMap<String, String>();
        hm.put("Name", "SPCC");
        hm.put("Faculty", "staff2");
        al.add(hm);

        user.setSubjects(al);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://moodle-9546f.firebaseio.com/");
        DatabaseReference databaseReference = database.getReference("students"); //users is a node in your Firebase Database.
        databaseReference.push().setValue(user);*/
        //final List<Student> studentList = new ArrayList<Student>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Student student = child.getValue(Student.class);
                    if(username.equals(student.getUsername()) && password.equals(student.getPassword())) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Login.this, Student_Home.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return;
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(Login.this, "Invalid User", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void authenticateStaff()
    {
        //final List<Staff> staffList = new ArrayList<Staff>();

        progressDialog.setMessage("Login..");
        progressDialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("staff"); //users is a node in your Firebase Database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Staff staff = child.getValue(Staff.class);
                    if(username.equals(staff.getUsername()) && password.equals(staff.getPassword())) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Login.this, Staff_Home.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return;
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(Login.this, "Invalid User", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
