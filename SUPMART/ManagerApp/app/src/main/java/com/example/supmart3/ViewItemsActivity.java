package com.example.supmart3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.supmart3.databinding.ActivityViewItemsBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ViewItemsActivity extends ListActivity {

    private ActivityViewItemsBinding binding;
    private ArrayList<String[]> list_items = new ArrayList<String[]>();
    private ArrayAdapter<String[]> adapter;
    private String store_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
        store_name = intent.getStringExtra("store_name");

        binding = ActivityViewItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewItemsActivity.ExecutingTaskInBackGround ex = new ViewItemsActivity.ExecutingTaskInBackGround();

        try {
            ex.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<String[]>(this, R.layout.item_layout, R.id.text1, list_items) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String[] entry = list_items.get(position);
                TextView text1 = (TextView)view.findViewById(R.id.text1);
                TextView text2 = (TextView)view.findViewById(R.id.text2);
                TextView text3 = (TextView)view.findViewById(R.id.text3);
                text1.setText(entry[0]);
                text2.setText(entry[1]);
                text3.setText(entry[2]);

                return view;

            }
        };

        setListAdapter(adapter);

    }

    public class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String store_id = ServerCalls.getStoreId(store_name);
            list_items = ServerCalls.getStoreItems(store_id);
            return null;
        }
    }
}