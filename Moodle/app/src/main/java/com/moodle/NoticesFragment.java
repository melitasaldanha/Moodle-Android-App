package com.moodle;

import android.app.AlertDialog;
import android.app.DownloadManager;
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
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

public class NoticesFragment extends ListFragment {
    String url;
    ArrayList<NoticesData> alNoticesData = new ArrayList<NoticesData>();
    HashMap<String, String> map;
    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    long queueId;
    DownloadManager dm;
    Uri download_url;
    View view;

    private OnFragmentInteractionListener mListener;

    public NoticesFragment() {
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
        view = inflater.inflate(R.layout.fragment_notices, container, false);
        return inflater.inflate(R.layout.fragment_notices, container, false);
    }

    void displayList(String notices_url) {
        //Get url
        url = notices_url;

        //Get all items in list
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference noticesDR = database.getReference(url);
        noticesDR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                alNoticesData.clear();
                for (DataSnapshot child : children) {
                    NoticesData nd = child.getValue(NoticesData.class);
                    alNoticesData.add(0, nd);
                }

                //HashMap
                data.clear();
                for (int i = 0; i < alNoticesData.size(); i++) {
                    map = new HashMap<String, String>();
                    map.put("Name", alNoticesData.get(i).getName());
                    map.put("Datetime", alNoticesData.get(i).getDatetime());
                    data.add(map);
                }

                //Keys in Map
                String[] from = {"Name", "Datetime"};

                //Id's of Views
                int[] to = {R.id.data_name, R.id.data_datetime};

                //Adapter
                SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.custom_list_item, from, to);
                adapter.notifyDataSetChanged();
                setListAdapter(adapter);
            }

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

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setMessage(alNoticesData.get(pos).getName()+" - Continue Download??");
                alertBuilder.setCancelable(false);

                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl(alNoticesData.get(pos).getUrl());
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                download_url = uri;

                                dm = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(download_url);
                                request.setTitle(alNoticesData.get(pos).getName());
                                request.setDescription("File is being downloaded..");

                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "my_file");

                                queueId = dm.enqueue(request);

                                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        String action = intent.getAction();
                                        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                            DownloadManager.Query query = new DownloadManager.Query();
                                            query.setFilterById(queueId);

                                            System.out.println("Notices: "+dm+" -- "+query);
                                            Cursor c = dm.query(query);

                                            if(c.moveToFirst()) {
                                                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                                                if(DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                                    //progressDialog.dismiss();
                                                    Toast.makeText(getContext(), "Download Complete!", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(getContext(), "Download Not Successful.. Please Try again later..", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                };

                                getContext().registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                System.out.println("Error: "+exception);
                            }
                        });
                    }
                });

                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alertBuilder.create().show();
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
