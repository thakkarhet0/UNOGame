package com.example.uno;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

public class CommonActivity extends AppCompatActivity {

    ProgressDialog dialog;

    public void actionBar(boolean show) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
    }

    public void alert(String alert) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle(R.string.info)
                .setMessage(alert)
                .setPositiveButton(R.string.okay, null)
                .show());
    }

    public void toggleDialog(boolean show) {
        toggleDialog(show, null);
    }

    public void toggleDialog(boolean show, String msg) {
        if (show) {
            dialog = new ProgressDialog(this);
            if (msg == null)
                dialog.setMessage(getString(R.string.loading));
            else
                dialog.setMessage(msg);
            dialog.setCancelable(false);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

}
