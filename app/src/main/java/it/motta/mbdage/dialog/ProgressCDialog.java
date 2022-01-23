package it.motta.mbdage.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class ProgressCDialog extends Dialog {

    private final Context mContext;
    private String title,message;


    public ProgressCDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}