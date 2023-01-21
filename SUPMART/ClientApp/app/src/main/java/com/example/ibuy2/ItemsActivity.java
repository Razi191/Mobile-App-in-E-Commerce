package com.example.ibuy2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.ibuy2.databinding.ActivityItemsBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ItemsActivity extends FragmentActivity implements NumberPicker.OnValueChangeListener {

    private ActivityItemsBinding binding;
    private ArrayList<String[]> listItems = new ArrayList<String[]>();
    private ArrayAdapter<String[]> adapter;
    private EditText barcode;
    private NumberPicker number_picker;
    private Button done_button;
    private Button report_button;
    private Button scan_button;
    private int count = 1;
    private String store_name;
    private boolean res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        store_name = intent.getStringExtra("store_name");

        binding = ActivityItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        barcode = (EditText) findViewById(R.id.barcode);

        adapter = new ArrayAdapter<String[]>(this, R.layout.item_layout, R.id.text1, listItems) {

            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String[] entry = listItems.get(position);
                TextView text1 = (TextView)view.findViewById(R.id.text1);
                TextView text2 = (TextView)view.findViewById(R.id.text2);
                text1.setText(entry[0]);
                text2.setText(entry[1]);

                return view;
            }
        };
        ListView items = (ListView)findViewById(R.id.list);
        items.setAdapter(adapter);

        done_button = (Button)findViewById(R.id.done_button);
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),RecommendationActivity.class);
                startActivity(i);
            }
        });

        report_button = (Button)findViewById(R.id.report_button);
        report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(store_name);
            }
        });

        number_picker = (NumberPicker) findViewById(R.id.count);
        number_picker.setMinValue(1);
        number_picker.setMaxValue(100);
        number_picker.setOnValueChangedListener(this);

        scan_button = (Button) findViewById(R.id.scan_button);
        scan_button.setOnClickListener(c ->
        {
            scanCode();
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        if(result.getContents() != null) {
            barcode.setText(result.getContents());
        }
    });

    public void addItems(View v) {
        barcode = (EditText) findViewById(R.id.barcode);
        String barcode_str = barcode.getText().toString();
        if(barcode_str.equals(""))
            return;
        String cnt = Integer.toString(count);
        barcode.getText().clear();
        adapter.notifyDataSetChanged();

        ItemsActivity.ExecutingTaskInBackGround executingTaskInBackGround = new ItemsActivity.ExecutingTaskInBackGround(store_name, barcode_str, cnt);

        try {
            executingTaskInBackGround.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(res == true) {
            listItems.add(new String[] {barcode_str, cnt});
        }
        else {
            Toast.makeText(this,"Item doesn't exist!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void openDialog(String store_name){
        PriceDialog price_dialog = new PriceDialog(store_name);
        price_dialog.show(getSupportFragmentManager(), "dialog");

    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        count = i1;
    }

    public class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        private String store_name;
        private String barcode;
        private String cnt;

        public ExecutingTaskInBackGround(String store_name, String barcode, String cnt) {
            super();
            this.store_name = store_name;
            this.barcode = barcode;
            this.cnt = cnt;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String store_id = ServerCalls.getStoreId(store_name);
            res = ServerCalls.addItem(store_id, barcode, cnt);
            return null;
        }
    }
}