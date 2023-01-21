package com.example.ibuy2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ibuy2.databinding.ActivityMainBinding;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String[] items;
    private AutoCompleteTextView autoCompleteTxt;
    private Button scan_button;
    private ArrayAdapter<String> adapterItems;
    public static String store_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExecutingTaskInBackGround executingTaskInBackGround = new ExecutingTaskInBackGround();

        try {
            executingTaskInBackGround.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                store_name = (String) adapterView.getItemAtPosition(i);
            }
        });

        scan_button = findViewById(R.id.scan_button);
        scan_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                Intent myIntent = new Intent(MainActivity.this, ItemsActivity.class);
                myIntent.putExtra("store_name", store_name); //Optional parameters
                MainActivity.this.startActivity(myIntent);

            }
        });

    }

    public class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                items = ServerCalls.getStores();
                ServerCalls.createUser();
                ServerCalls.getUserId();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}