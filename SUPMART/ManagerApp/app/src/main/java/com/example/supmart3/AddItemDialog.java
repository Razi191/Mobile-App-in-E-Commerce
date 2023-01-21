package com.example.supmart3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.concurrent.ExecutionException;

public class AddItemDialog extends AppCompatDialogFragment {

    private EditText editTextItem;
    private EditText editTextBarcode;
    private EditText editTextPrice;
    private Button scanButton;
    private String store_name;
    public boolean result = false;

    AddItemDialog(String store_name) {
        this.store_name = store_name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_item_dialog, null);

        editTextItem = view.findViewById(R.id.edit_item);
        editTextBarcode = view.findViewById(R.id.edit_barcode);
        editTextPrice = view.findViewById(R.id.edit_price);
        scanButton = view.findViewById(R.id.scan_add_item);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        builder.setView(view)
                .setTitle("Add Item")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AddItemDialog.ExecutingTaskInBackGround ex = new AddItemDialog.ExecutingTaskInBackGround();

                        try {
                            ex.execute().get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(result) {
                            Toast.makeText(getContext(),"Item was added!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getContext(),"Item already exists!",
                                    Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
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
            editTextBarcode.setText(result.getContents());
        }
    });

    public class ExecutingTaskInBackGround extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            boolean res = ServerCalls.addItem(store_name, editTextItem.getText().toString(),
                    editTextBarcode.getText().toString(),
                    editTextPrice.getText().toString());

            result = res;

            return null;
        }
    }
}
