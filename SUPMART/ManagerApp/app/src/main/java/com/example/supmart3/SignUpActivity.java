package com.example.supmart3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class SignUpActivity extends AppCompatActivity {

    private EditText store_edit_txt;
    private EditText password_edit_txt;
    private Button log_in_button;
    private String store_name;
    private String password;
    public static boolean res = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        store_edit_txt = (EditText) findViewById(R.id.store_text);
        password_edit_txt = (EditText) findViewById(R.id.password_text);

        log_in_button = (Button)findViewById(R.id.log_in);
        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                store_name = store_edit_txt.getText().toString();
                password = password_edit_txt.getText().toString();

                SignUpActivity.ExecutingTaskInBackGround executingTaskInBackGround = new SignUpActivity.ExecutingTaskInBackGround(store_name, password, "loc");

                try {
                    executingTaskInBackGround.execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if(res == true) {
                    Toast.makeText(SignUpActivity.this, "Sign Up Succeeded!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, OpActivity.class));
                }
                else
                    Toast.makeText(SignUpActivity.this, "Store Exists!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        private String store_name;
        private String password;
        private String loc;

        ExecutingTaskInBackGround(String store_name, String password, String loc) {
            this.store_name = store_name;
            this.password = password;
            this.loc = loc;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(ServerCalls.addStore(store_name, password, loc)) {
                SignUpActivity.res = true;
                return null;
            }
            SignUpActivity.res = false;

            return null;
        }
    }
}