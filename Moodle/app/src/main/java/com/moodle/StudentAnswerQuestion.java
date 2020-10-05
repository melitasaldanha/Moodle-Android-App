package com.moodle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StudentAnswerQuestion extends AppCompatActivity {
    String username, quiz_title, question_url, question;
    TextView tv_question;
    EditText et_solution;
    Button btn_save;
    boolean save = false, exists = false;
    MyDBHandler dbHandler;
    SavedSolution savedSolution;
    String solution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_answer_question);

        //Get bundles
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("username");
        quiz_title = bundle.getString("quiz_title");
        question_url = bundle.getString("question_url");
        question = bundle.getString("question");

        dbHandler = new MyDBHandler(this);
        savedSolution = new SavedSolution();

        String[] url_split = question_url.split("/");
        getSupportActionBar().setTitle(url_split[url_split.length-7]+": "+quiz_title);

        tv_question = (TextView) findViewById(R.id.question);
        et_solution = (EditText) findViewById(R.id.solution);
        btn_save = (Button) findViewById(R.id.save_answer);

        tv_question.setText("Q. "+question);

        //dbHandler.delete(dbHandler, username+"@"+question_url);

        solution = dbHandler.retrieve(dbHandler, username+"@"+question_url);
        if(!solution.equals("")) {     //Check if exists - Update
            et_solution.setText(solution);
            exists = true;
        }
        et_solution.setSelection(et_solution.getText().length()); // End point Cursor

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Save

                if(exists) {                                    //If exists - Update
                    savedSolution.setQuestion(username+"@"+question_url);
                    savedSolution.setSolution(et_solution.getText().toString().trim());
                    dbHandler.update(dbHandler, savedSolution);
                } else {                                        //Add
                    savedSolution.setQuestion(username+"@"+question_url);
                    savedSolution.setSolution(et_solution.getText().toString());
                    dbHandler.add(dbHandler, savedSolution);

                    exists = true;
                    save = true;
                }

                solution = et_solution.getText().toString().trim();

                Toast.makeText(getApplicationContext(), "Solution Saved!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!(et_solution.getText().toString().trim()).equals(solution)) {
            save = false;
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("All your changes will be lost!! \nAre you sure you want to exit the quiz without saving it??");
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
        } else {
            finish();
        }
    }
}