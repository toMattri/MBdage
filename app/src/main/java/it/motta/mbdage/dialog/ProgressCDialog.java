package it.motta.mbdage.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import it.motta.mbdage.R;

public class ProgressCDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private String title,message;
    private boolean indeterminate;
    private TextView txtTitle,txtMessage;
    private ProgressBar progressBar;
    private Button btProgress;
    private View.OnClickListener handler;

    public ProgressCDialog(@NonNull Context context){
        this(context,"","");
    }

    public ProgressCDialog(@NonNull Context context,String title,String message) {
        super(context);
        this.mContext = context;
        this.title = title;
        this.message = message;
        this.indeterminate = true;
        this.handler = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialog_c_progress);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        txtTitle = findViewById(R.id.titleprog);
        txtMessage = findViewById(R.id.messageprog);
        btProgress = findViewById(R.id.btProg);
        progressBar = findViewById(R.id.progressBar);
        btProgress.setOnClickListener(this);
        btProgress.setVisibility(View.GONE);
        txtMessage.setText(message);
        txtTitle.setText(title);
    }

    public void setTitle(String title) {
        this.title = title;
        if(txtTitle != null)
            txtTitle.setText(title);
    }

    public void setMessage(String message) {
        this.message = message;
        if(txtMessage != null)
            txtMessage.setText(message);
    }

    public void setOnClickListner(String text,View.OnClickListener handler){
        btProgress.setVisibility(View.VISIBLE);
        btProgress.setText(text);
        this.handler = handler;
    }

    @Override
    public void onClick(View view) {
        if(handler != null)handler.onClick(view);
    }

}