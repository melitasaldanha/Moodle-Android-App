package com.moodle;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StudentQuizQuestions extends AppCompatActivity {
    String username, url, quiz_title, quiz_datetime;
    String quiz_title_url;
    String solution_url = "quiz_solutions/";
    ArrayList<Question> ques_list = new ArrayList<>();
    Button btn_submit_quiz;
    String child_key = "";
    ProgressDialog progressDialog;
    StudentQuizQuestionsFragment q = new StudentQuizQuestionsFragment();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz_questions);

        //Get bundles
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");
        url = bundle.getString("quiz_url");
        quiz_title = bundle.getString("quiz_title");
        quiz_datetime = bundle.getString("quiz_datetime");

        String[] url_split = url.split("/");
        getSupportActionBar().setTitle(url_split[url_split.length-3]+": "+quiz_title);

        btn_submit_quiz = (Button) findViewById(R.id.submit_quiz);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(url);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    QuizData qd = child.getValue(QuizData.class);
                    if(quiz_title.equals(qd.getName()) && quiz_datetime.equals(qd.getDatetime())) {
                        child_key = child.getKey();
                        quiz_title_url = url+"/"+child_key+"/questions";
                        solution_url += url + child_key + "/";

                        //If quiz_title_url exists in firebase disable submit button

                        // Begin the transaction
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        ft.add(R.id.placeholder, q);
                        // Complete the changes added above
                        ft.commit();
                        q.displayList(username, quiz_title, quiz_title_url, getApplicationContext());

                        //Get all items in list
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference quizDR = database.getReference(quiz_title_url);
                        quizDR.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                ques_list.clear();
                                for (DataSnapshot child : children) {
                                    Question q = child.getValue(Question.class);
                                    ques_list.add(q);
                                }
                            };

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_submit_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //submitQuiz();

                /*AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getApplicationContext());
                alertBuilder.setMessage("Any changes cannot be made after quiz submission!! \nAre you sure you want to continue??");
                alertBuilder.setCancelable(false);

                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int ipos) {*/
                        MyDBHandler dbHandler = new MyDBHandler(getApplicationContext());

                        //Retrieve data for all questions
                        ArrayList<Solution> alSolution = new ArrayList<Solution>();
                        for(int i=0; i<ques_list.size(); i++) {
                            Solution solution = new Solution();
                            solution.setQues(ques_list.get(i).getQues());
                            solution.setSolution(dbHandler.retrieve(dbHandler, username+"@"+quiz_title_url+"/"+i));
                            solution.setMarks_obtained(0.0);
                            solution.setOut_of_marks(ques_list.get(i).getMarks());
                            alSolution.add(solution);
                        }

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

                        QuizSolution quizSolution = new QuizSolution();
                        quizSolution.setRoll_no(username);
                        quizSolution.setDate_time(format1.format(c.getTime()));
                        quizSolution.setSolutions(alSolution);

                        //Store in firebase
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://moodle-9546f.firebaseio.com/");
                        DatabaseReference databaseReference;
                        databaseReference = database.getReference("quiz_solutions/"+url+"/"+child_key);
                        databaseReference.push().setValue(quizSolution);

                        //Delete from local db
                        //for(int i=0; i<ques_list.size(); i++)
                        //    dbHandler.delete(dbHandler, username+"@"+quiz_title_url+"/"+i);

                        Toast.makeText(getApplicationContext(), "Quiz Submitted!!", Toast.LENGTH_SHORT).show();
                    /*}
                });

                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertBuilder.create().show();*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
