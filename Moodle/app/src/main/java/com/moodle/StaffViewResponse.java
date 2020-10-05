package com.moodle;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StaffViewResponse extends AppCompatActivity {
    String stud_username, quiz_solution_url;
    String key;
    ListView list;
    Button btn_save_marks;
    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<String> solutions = new ArrayList<String>();
    ArrayList<String> marks_obtained = new ArrayList<String>();
    ArrayList<String> marks_out_of = new ArrayList<String>();
    CustomListViewResponse adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_view_response);

        //Get bundles
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        stud_username = bundle.getString("stud_username");
        quiz_solution_url = bundle.getString("quiz_solution_url");

        getSupportActionBar().setTitle("Response: " + stud_username);

        btn_save_marks = (Button) findViewById(R.id.save_marks);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference quizDR = database.getReference(quiz_solution_url);
        quizDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                questions.clear();
                solutions.clear();
                marks_obtained.clear();
                marks_out_of.clear();

                for (DataSnapshot child : children) {
                    QuizSolution q = child.getValue(QuizSolution.class);

                    System.out.println("-------"+q.getRoll_no());
                    if (stud_username.equals(q.getRoll_no())) {
                        key = child.getKey();

                        for (int i = 0; i < q.getSolutions().size(); i++) {
                            questions.add(q.getSolutions().get(i).getQues());
                            solutions.add(q.getSolutions().get(i).getSolution());
                            marks_obtained.add(Double.toString(q.getSolutions().get(i).getMarks_obtained()));
                            marks_out_of.add(Double.toString(q.getSolutions().get(i).getOut_of_marks()));
                        }
                        break;
                    }
                }

                adapter = new CustomListViewResponse(StaffViewResponse.this, questions, solutions, marks_obtained, marks_out_of);
                list = (ListView) findViewById(R.id.response_ques_list);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            ;
        });

        btn_save_marks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference quizDR = database.getReference(quiz_solution_url+"/"+key+"/solutions/");
                quizDR.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        boolean flag = false;
                        String link = "";
                        for (DataSnapshot child : children) {
                            Solution s = child.getValue(Solution.class);

                            String child_key = child.getKey();
                            link = quiz_solution_url + "/" + key + "/solutions/" + child_key;
                            System.out.println(link);

                            Double marks_obt, marks_out_of;
                            String value1, value2;
                            value1 = adapter.marks_obtained.get(Integer.parseInt(child_key));
                            value2 = adapter.marks_out_of.get(Integer.parseInt(child_key));
                            System.out.println("value:" + value1);
                            marks_obt = Double.parseDouble(value1);
                            marks_out_of = Double.parseDouble(value2);
                            System.out.println("marks_obt:" + marks_obt);

                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference dr = db.getReference(link);
                            dr.child("marks_obtained").setValue(marks_obt);

                            Toast.makeText(getApplicationContext(), "Marks Updated", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                            /*if (marks_obt > marks_out_of) {
                                flag = true;
                                break;
                                //Toast.makeText(getApplicationContext(), "Please enter valid marks", Toast.LENGTH_SHORT).show();
                            }*/
                        }

                        /*if (!flag) {
                            ArrayList<String> marks_obt = adapter.marks_obtained;
                            for (int i = 0; i < marks_obt.size(); i++) {
                                FirebaseDatabase db = FirebaseDatabase.getInstance();
                                DatabaseReference dr = db.getReference(link);
                                dr.child("marks_obtained").setValue(Double.parseDouble(marks_obt.get(i)));
                            }

                            Toast.makeText(getApplicationContext(), "Marks Updated", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter valid marks", Toast.LENGTH_SHORT).show();
                        }*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        btn_save_marks.performClick();
        finish();
    }
}

class CustomListViewResponse extends ArrayAdapter<String> {

    public final Activity context;
    public final ArrayList<String> questions;
    public final ArrayList<String> solutions;
    public final ArrayList<String> marks_obtained;
    public final ArrayList<String> marks_out_of;
    ListViewHolder2 listViewHolder;
    View rowView;


    public CustomListViewResponse(Activity context,
                      ArrayList<String> questions, ArrayList<String> solutions, ArrayList<String> marks_obtained, ArrayList<String> marks_out_of) {
        super(context, R.layout.custom_view_response_item, questions);
        this.context = context;
        this.questions = questions;
        this.solutions = solutions;
        this.marks_obtained = marks_obtained;
        this.marks_out_of = marks_out_of;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView= inflater.inflate(R.layout.custom_view_response_item, null, true);

            listViewHolder = new ListViewHolder2();
            listViewHolder.txtQuestion = (TextView) rowView.findViewById(R.id.question);
            listViewHolder.txtSolution = (TextView) rowView.findViewById(R.id.solution);
            listViewHolder.txtOutOfMarks = (TextView) rowView.findViewById(R.id.out_of_marks);
            listViewHolder.etMarksObtained = (EditText) rowView.findViewById(R.id.marks_obtained);

            rowView.setTag(listViewHolder);
        } else {
            rowView = view;
            listViewHolder = (ListViewHolder2) rowView.getTag();
        }

        listViewHolder.etMarksObtained.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText marks_et =(EditText)v.findViewById(R.id.marks_obtained);
                    marks_obtained.set(position, marks_et.getText().toString().trim());
                    //notifyDataSetChanged();
                }
            }
        });

        listViewHolder.txtQuestion.setText("Q. "+questions.get(position));
        listViewHolder.txtSolution.setText(solutions.get(position));
        listViewHolder.etMarksObtained.setText(marks_obtained.get(position));
        listViewHolder.etMarksObtained.setFocusable(true);
        listViewHolder.txtOutOfMarks.setText("/"+marks_out_of.get(position));

        return rowView;
    }
}

class ListViewHolder2 {

    TextView txtQuestion;
    TextView txtSolution;
    TextView txtOutOfMarks;
    EditText etMarksObtained;
}