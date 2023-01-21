package com.example.ibuy2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibuy2.databinding.ActivityItemsBinding;
import com.example.ibuy2.databinding.ActivityRecommendationBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RecommendationActivity extends ListActivity {

    private ActivityRecommendationBinding binding;
    public static ArrayList<String[]> list_stores = new ArrayList<String[]>();
    private ArrayAdapter<String[]> adapter;
    public static String store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecommendationActivity.ExecutingTaskInBackGround executingTaskInBackGround = new ExecutingTaskInBackGround(this);
        try {
            executingTaskInBackGround.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        adapter = new ArrayAdapter<String[]>(this, R.layout.price_layout, R.id.text1, list_stores) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String[] entry = list_stores.get(position);
                TextView text1 = (TextView)view.findViewById(R.id.text1);
                TextView text2 = (TextView)view.findViewById(R.id.text2);
                text1.setText(entry[0]);
                text2.setText(entry[1]);

                return view;

            }
        };

        setListAdapter(adapter);


    }

    public static class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        public static Context c;

        ExecutingTaskInBackGround(Context c) {
            this.c = c;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            store_id = ServerCalls.getStoreId(MainActivity.store_name);
            list_stores = ServerCalls.getRecommendations(store_id);

            return null;
        }
    }
}