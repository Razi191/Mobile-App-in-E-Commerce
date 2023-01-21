package com.example.ibuy2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
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
import java.util.zip.Inflater;

public class PriceDialog extends AppCompatDialogFragment {

    private EditText editTextBarcode;
    private EditText editTextPrice;
    private Button scan_button;
    private String store_name;
    public static String barcode;
    public static String price;
    private boolean res;

    PriceDialog(String store_name) {
        this.store_name = store_name;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        scan_button = view.findViewById(R.id.scan_barcode_dialog);
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        builder.setView(view)
                .setTitle("Report Price")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        barcode = editTextBarcode.getText().toString();
                        price = editTextPrice.getText().toString();

                        PriceDialog.ExecutingTaskInBackGround executingTaskInBackGround = new PriceDialog.ExecutingTaskInBackGround(barcode, price);

                        try {
                            executingTaskInBackGround.execute().get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(res)
                            Toast.makeText(getActivity(), "Report was Sent!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Illegal report!", Toast.LENGTH_SHORT).show();
                    }
                });

        editTextBarcode = view.findViewById(R.id.edit_barcode);
        editTextPrice = view.findViewById(R.id.edit_price);

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

        private String barcode;
        private String price;

        ExecutingTaskInBackGround(String barcode, String price) {
            this.barcode = barcode;
            this.price = price;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String store_id = ServerCalls.getStoreId(store_name);
            String product_id = ServerCalls.getProductId(barcode);
            res = ServerCalls.reportPrice(store_id, barcode, product_id, price);
            return null;
        }
    }
}
