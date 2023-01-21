package com.example.supmart3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supmart3.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutionException;

public class SignInActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AutoCompleteTextView autoCompleteTxt;
    private ArrayAdapter<String> adapterItems;
    private Button done_button;
    private EditText password;
    public static String store_name;
    private String[] items;
    public static boolean auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInActivity.ExecutingTaskInBackGround_2 ex = new SignInActivity.ExecutingTaskInBackGround_2();

        try {
            ex.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);

        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                store_name = (String) adapterView.getItemAtPosition(i);
            }
        });

        password = (EditText)findViewById(R.id.password_text) ;

        done_button = (Button)findViewById(R.id.log_in);
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignInActivity.ExecutingTaskInBackGround executingTaskInBackGround = new SignInActivity.ExecutingTaskInBackGround(store_name, password.getText().toString(), SignInActivity.this);

                try {
                    executingTaskInBackGround.execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if(auth) {
                    Toast.makeText(SignInActivity.this, "Login Succeeded!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(SignInActivity.this, OpActivity.class);
                    myIntent.putExtra("store_name", store_name);
                    startActivity(myIntent);
                }
                else
                    Toast.makeText(SignInActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public class ExecutingTaskInBackGround_2 extends AsyncTask<Void, Void, Void> {


        ExecutingTaskInBackGround_2() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            items = ServerCalls.getStores();
            return null;
        }
    }

    public class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        private String store_name;
        private String password;
        private Context c;

        ExecutingTaskInBackGround(String store_name, String password, Context c) {
            this.store_name = store_name;
            this.password = password;
            this.c = c;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServerCalls.store_id = ServerCalls.getStoreId(store_name);

            if(ServerCalls.authenticate(store_name, password))
                auth = true;

            else
                auth = false;

            return null;
        }
    }

}