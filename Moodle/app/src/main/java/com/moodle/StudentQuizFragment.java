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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentQuizFragment extends ListFragment {
    String url, username;
    ArrayList<QuizData> alQuizData = new ArrayList<QuizData>();
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    View view;
    Context context;

    private OnFragmentInteractionListener mListener;

    public StudentQuizFragment() {
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
        view = inflater.inflate(R.layout.fragment_quiz_folder, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    void displayList(String student_username, String quiz_url, Context ctx) {
        //Get url
        username = student_username;
        url = quiz_url;
        context = ctx;

        //Get all items in list
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference docsDR = database.getReference(url);
        docsDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                alQuizData.clear();
                for (DataSnapshot child : children) {
                    QuizData qd = child.getValue(QuizData.class);
                    alQuizData.add(0, qd);
                }

                //HashMap
                data.clear();
                for (int i = 0; i < alQuizData.size(); i++) {
                    map = new HashMap<String, String>();
                    map.put("Name", alQuizData.get(i).getName());
                    map.put("Datetime", alQuizData.get(i).getDatetime());
                    data.add(map);
                }

                //Keys in Map
                String[] from = {"Name", "Datetime"};

                //Id's of Views
                int[] to = {R.id.data_name, R.id.data_datetime};

                //Adapter
                SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.custom_quiz_folder, from, to);
                adapter.notifyDataSetChanged();
                setListAdapter(adapter);
            };

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long id) {
                //Call Student Quiz Questions Activity
                String quiz_title = alQuizData.get(pos).getName();
                String quiz_datetime = alQuizData.get(pos).getDatetime();

                Intent intent = new Intent(getContext(), StudentQuizQuestions.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("quiz_url", url);
                bundle.putString("quiz_title", quiz_title);
                bundle.putString("quiz_datetime", quiz_datetime);
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
