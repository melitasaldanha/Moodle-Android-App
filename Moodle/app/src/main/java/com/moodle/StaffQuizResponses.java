package com.moodle;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StaffQuizResponses extends AppCompatActivity {
    String username, url, quiz_title, quiz_datetime;
    String child_key, quiz_solution_url;
    ArrayList<String> rollno = new ArrayList<String>();
    ArrayList<String> datetime = new ArrayList<String>();
    ArrayList<String> marks = new ArrayList<String>();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_quiz_responses);

        //Get bundles
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");
        url = bundle.getString("quiz_url");
        quiz_title = bundle.getString("quiz_title");
        quiz_datetime = bundle.getString("quiz_datetime");

        String[] url_split = url.split("/");
        getSupportActionBar().setTitle(url_split[url_split.length-3]+": "+quiz_title);

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
                        quiz_solution_url = "quiz_solutions/"+url+child_key;
                        System.out.println("Quiz solution url: "+ quiz_solution_url);

                        //Get all responses in list
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference quizDR = database.getReference(quiz_solution_url);
                        quizDR.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                rollno.clear();
                                datetime.clear();
                                marks.clear();
                                for (DataSnapshot child : children) {
                                    QuizSolution q = child.getValue(QuizSolution.class);
                                    rollno.add(0, q.getRoll_no());
                                    datetime.add(0, q.getDate_time());
                                    Double marks_obt = 0.0;
                                    Double out_of = 0.0;

                                    System.out.println(rollno.get(0));

                                    for(int i=0; i<q.getSolutions().size(); i++) {
                                        marks_obt += q.getSolutions().get(i).getMarks_obtained();
                                        out_of += q.getSolutions().get(i).getOut_of_marks();
                                    }

                                    marks.add(0, Double.toString(marks_obt)+"/"+Double.toString(out_of));
                                }

                                CustomList adapter = new CustomList(StaffQuizResponses.this, rollno, datetime, marks);
                                list = (ListView)findViewById(R.id.response_list);
                                list.setAdapter(adapter);
                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String stud_username = rollno.get(position);

                                        Intent intent = new Intent(getApplicationContext(), StaffViewResponse.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("stud_username", stud_username);
                                        bundle.putString("quiz_solution_url", quiz_solution_url);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
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
    }
}

class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> rollno;
    private final ArrayList<String> datetime;
    private final ArrayList<String> marks;
    public CustomList(Activity context,
                      ArrayList<String> rollno, ArrayList<String> datetime, ArrayList<String> marks) {
        super(context, R.layout.custom_response_list_item, rollno);
        this.context = context;
        this.rollno = rollno;
        this.datetime = datetime;
        this.marks = marks;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.custom_response_list_item, null, true);
        TextView txtRollNo = (TextView) rowView.findViewById(R.id.rollno);
        TextView txtDatetime = (TextView) rowView.findViewById(R.id.datetime);
        TextView txtMarks = (TextView) rowView.findViewById(R.id.marks);

        //ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtRollNo.setText(rollno.get(position));
        txtDatetime.setText(datetime.get(position));
        txtMarks.setText(marks.get(position));
        return rowView;
    }
}