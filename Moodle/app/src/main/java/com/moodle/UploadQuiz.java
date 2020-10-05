package com.moodle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;

public class UploadQuiz extends AppCompatActivity {

    private ListView listView;
    private UploadQuizListAdapter listAdapter;
    ArrayList<String> ques_list = new ArrayList<>();
    ArrayList<String> marks_list = new ArrayList<>();
    Button btn_upload_quiz;
    EditText quiz_title;
    ProgressDialog progressDialog;
    String quiz_url;
    String quiz_name, quiz_datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_quiz);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //Get quiz_url
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        quiz_url = bundle.getString("quiz_url");

        final String[] url_split = quiz_url.split("/");
        getSupportActionBar().setTitle(url_split[url_split.length-3]+": Upload Quiz");

        //By default 1 ques
        ques_list.add("");
        marks_list.add("");

        listView = (ListView) findViewById(R.id.quiz_ques_list);
        listAdapter = new UploadQuizListAdapter(this, ques_list, marks_list);
        listView.setAdapter(listAdapter);
        btn_upload_quiz = (Button) findViewById(R.id.upload_quiz);
        quiz_title = (EditText) findViewById(R.id.quiz_title);
        progressDialog = new ProgressDialog(this);

        btn_upload_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uploaded
                boolean empty_ques = false;
                for(int i=0; i<ques_list.size(); i++) {
                    if(ques_list.get(i).equals("") || marks_list.get(i).equals("")) {
                        empty_ques = true;
                        break;
                    }
                }

                if(empty_ques)
                    Toast.makeText(getApplicationContext(), "Any question/marks cannot be left blank..", Toast.LENGTH_SHORT).show();
                else if(quiz_title.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter a title for the quiz..", Toast.LENGTH_SHORT).show();
                else {
                    progressDialog.setTitle("Uploading..");
                    progressDialog.show();
                    String filename = quiz_title.getText().toString();

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

                    ArrayList<Question> final_list = new ArrayList<Question>();
                    for(int i=0; i<ques_list.size(); i++) {
                        Question q = new Question();
                        q.setQues(ques_list.get(i));
                        q.setMarks(Double.parseDouble(marks_list.get(i)));
                        final_list.add(q);
                    }

                    QuizData quiz_data = new QuizData();
                    quiz_data.setName(filename);
                    quiz_data.setDatetime(format1.format(c.getTime()));
                    quiz_data.setQuestions(final_list);

                    quiz_name = filename;
                    quiz_datetime = format1.format(c.getTime());

                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://moodle-9546f.firebaseio.com/");
                    DatabaseReference databaseReference;
                    databaseReference = database.getReference(quiz_url);        //Upload quiz
                    databaseReference.push().setValue(quiz_data);

                    /*FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = database.getReference(quiz_url);
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for (DataSnapshot child : children) {
                            QuizData qd = child.getValue(QuizData.class);
                                if (quiz_name.equals(qd.getName()) && quiz_datetime.equals(qd.getDatetime())) {
                                    String child_key = child.getKey();

                                    FirebaseDatabase db2 = FirebaseDatabase.getInstance("https://moodle-9546f.firebaseio.com/");
                                    DatabaseReference dbRef2 = db2.getReference("quiz_solutions/"
                                            + quiz_url + "/"
                                            + child_key);        //Make solutions directory
                                    dbRef2.push().setValue(qd.getName());
                                    dbRef2.push().setValue(qd.getDatetime());
                                    dbRef2.push().setValue("responses");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Quiz Uploaded!!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        FloatingActionButton add_ques = (FloatingActionButton) findViewById(R.id.add_ques);
        add_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ques_list.add("");
                marks_list.add("");
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("All your changes will be lost!! \nAre you sure you want to exit the quiz??");
        alertBuilder.setCancelable(false);

        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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
