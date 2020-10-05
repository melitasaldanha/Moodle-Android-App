package com.moodle;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class UploadQuizListAdapter extends BaseAdapter {
    public ArrayList<String> ques_list;
    public ArrayList<String> marks_list;
    private Context context;

    public UploadQuizListAdapter(Context context, ArrayList<String> ques_list, ArrayList<String> marks_list) {
        this.context = context;
        this.ques_list = ques_list;
        this.marks_list = marks_list;
    }

    @Override
    public int getCount() {
        return ques_list.size();
    }

    @Override
    public String getItem(int position) {
        return ques_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView
            , ViewGroup parent) {
        View row;
        final ListViewHolder listViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.quiz_ques, parent, false);
            listViewHolder = new ListViewHolder();
            listViewHolder.ques_et = row.findViewById(R.id.ques);
            listViewHolder.marks_et = row.findViewById(R.id.marks);
            listViewHolder.delete_ib = row.findViewById(R.id.delete_ques);
            row.setTag(listViewHolder);
        } else {
            row = convertView;
            listViewHolder = (ListViewHolder) row.getTag();
        }

        listViewHolder.ques_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText ques_et =(EditText)v.findViewById(R.id.ques);
                    ques_list.set(position, ques_et.getText().toString().trim());
                }
            }
        });

        listViewHolder.marks_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText marks_et =(EditText)v.findViewById(R.id.marks);
                    marks_list.set(position, marks_et.getText().toString().trim());
                }
            }
        });


        final String ques = getItem(position);
        final String marks = marks_list.get(position);

        listViewHolder.ques_et.setText(ques);
        listViewHolder.marks_et.setText(marks);
        listViewHolder.delete_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update List
                if (ques_list.size() > 1) {
                    ques_list.remove(position);
                    marks_list.remove(position);
                    notifyDataSetChanged();
                } else
                    Toast.makeText(context, "Quiz needs to have atleast 1 question", Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }
}

class ListViewHolder {

    EditText ques_et;
    EditText marks_et;
    ImageButton delete_ib;
    //LinearLayout llMainRow;
}
