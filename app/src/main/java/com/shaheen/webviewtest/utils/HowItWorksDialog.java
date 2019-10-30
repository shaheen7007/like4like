package com.shaheen.webviewtest.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.shaheen.webviewtest.R;

import androidx.appcompat.widget.AppCompatCheckBox;

public class HowItWorksDialog {
    PrefManager prefManager;
    AppCompatCheckBox checkBox;

    public void showDialog(Context activity){
        prefManager = PrefManager.getInstance(activity);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.how_it_works_dialog);

        TextView dialogButton = (TextView) dialog.findViewById(R.id.close);
        checkBox=dialog.findViewById(R.id.chk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefManager.setHowItWorksFlag(checkBox.isChecked());
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}