package com.moodle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StudentQuizQuestionsFragment extends Fragment {
    String username, url, quiz_title, title_url;
    View view;
    Context context;
    ArrayList<Question> alQuestion = new ArrayList<Question>();
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    ListView listView;

    private OnFragmentInteractionListener mListener;

    public StudentQuizQuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_student_quiz_questions, container, false);
        listView = (ListView) view.findViewById(R.id.questions_list);
        return view;
    }

    void displayList(String student_username, String title, String quiz_title_url, Context ctx) {
        //Get url
        username = student_username;
        quiz_title = title;
        title_url = quiz_title_url;
        context = ctx;

        //Get all items in list
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference quizDR = database.getReference(title_url);
        quizDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                alQuestion.clear();
                for (DataSnapshot child : children) {
                    Question q = child.getValue(Question.class);
                    alQuestion.add(q);
                }

                //HashMap
                data.clear();
                for (int i = 0; i < alQuestion.size(); i++) {
                    map = new HashMap<String, String>();
                    map.put("Question", alQuestion.get(i).getQues());
                    map.put("Marks", "/"+Double.toString(alQuestion.get(i).getMarks()));
                    data.add(map);
                }

                //Keys in Map
                String[] from = {"Question", "Marks"};

                //Id's of Views
                int[] to = {R.id.question, R.id.marks};

                //Adapter
                SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.custom_student_question, from, to);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            };

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long id) {
                //Call Student Quiz Questions Activity
                String question = alQuestion.get(pos).getQues();

                Intent intent = new Intent(getContext(), StudentAnswerQuestion.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("quiz_title", quiz_title);
                bundle.putString("question_url", title_url+"/"+pos);
                bundle.putString("question", question);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
