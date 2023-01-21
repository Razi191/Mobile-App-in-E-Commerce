package com.example.supmart3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.supmart3.databinding.ActivityViewItemsBinding;
import com.example.supmart3.databinding.ActivityViewReportsBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ViewReportsActivity extends ListActivity {

    private ActivityViewReportsBinding binding;
    private ArrayList<String[]> list_reports = new ArrayList<String[]>();
    private ArrayAdapter<String[]> adapter;
    private String store_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        store_name = intent.getStringExtra("store_name");

        binding = ActivityViewReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ViewReportsActivity.ExecutingTaskInBackGround ex = new ViewReportsActivity.ExecutingTaskInBackGround();

        try {
            ex.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<String[]>(this, R.layout.report_layout, R.id.text1, list_reports) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String[] entry = list_reports.get(position);
                TextView text1 = (TextView)view.findViewById(R.id.text1);
                TextView text2 = (TextView)view.findViewById(R.id.text2);
                TextView text3 = (TextView)view.findViewById(R.id.text3);
                TextView text4 = (TextView)view.findViewById(R.id.text4);
                text1.setText(entry[0]);
                text2.setText(entry[1]);
                text3.setText(entry[2]);
                text4.setText(entry[3]);

                return view;
            }
        };

        setListAdapter(adapter);

    }

    public class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_reports = ServerCalls.getStoreReports(ServerCalls.store_id);
            return null;
        }

    }

}