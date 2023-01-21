package com.example.supmart3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class OpActivity extends AppCompatActivity {

    private TextView warning_count_text_view;
    private Button view_items_button;
    private Button add_item_button;
    private Button update_item_button;
    private Button delete_item_button;
    private Button view_reports_button;
    private String count;
    private String store_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op);

        Intent intent=getIntent();
        store_name = intent.getStringExtra("store_name");

        warning_count_text_view = (TextView) findViewById(R.id.warning_count_text_view);

        OpActivity.ExecutingTaskInBackGroundGetWarnings ex1 = new OpActivity.ExecutingTaskInBackGroundGetWarnings();

        try {
            ex1.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        warning_count_text_view.setText(count);
        int int_count = Integer.valueOf(count);
        if(int_count < 5)
            warning_count_text_view.setTextColor(getResources().getColor(R.color.green));
        else if(int_count == 5)
            warning_count_text_view.setTextColor(getResources().getColor(R.color.yellow));
        else
            warning_count_text_view.setTextColor(getResources().getColor(R.color.red));

        view_items_button = (Button)findViewById(R.id.view_items_button);
        view_items_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(OpActivity.this, ViewItemsActivity.class);
                myIntent.putExtra("store_name", store_name);
                startActivity(myIntent);
            }
        });

        add_item_button = (Button)findViewById(R.id.add_item_button);
        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddItemDialog(store_name);
            }
        });

        update_item_button = (Button)findViewById(R.id.update_item_button);
        update_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUpdateItemDialog(store_name);
            }
        });

        delete_item_button = (Button)findViewById(R.id.delete_item_button);
        delete_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeleteItemDialog(store_name);
            }
        });

        view_reports_button = (Button)findViewById(R.id.view_reports_button);
        view_reports_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(OpActivity.this, ViewReportsActivity.class);
                myIntent.putExtra("store_name", store_name);
                startActivity(myIntent);
            }
        });
    }

    private void openAddItemDialog(String store_name){
        AddItemDialog add_item_dialog = new AddItemDialog(store_name);
        add_item_dialog.show(getSupportFragmentManager(), "dialog");

    }

    private void openUpdateItemDialog(String store_name) {
        UpdateItemDialog update_item_dialog = new UpdateItemDialog(store_name);
        update_item_dialog.show(getSupportFragmentManager(), "dialog");
    }

    private void openDeleteItemDialog(String store_name){
        DeleteItemDialog delete_item_dialog = new DeleteItemDialog(store_name);
        delete_item_dialog.show(getSupportFragmentManager(), "dialog");
    }

    public class ExecutingTaskInBackGroundGetWarnings extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            count = ServerCalls.getWarnings(ServerCalls.store_id);
            return null;
        }
    }
}