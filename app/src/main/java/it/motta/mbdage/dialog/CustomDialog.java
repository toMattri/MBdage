package it.motta.mbdage.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import it.motta.mbdage.R;
import it.motta.mbdage.models.evalue.TypeDialog;

@SuppressLint("UseCompatLoadingForDrawables")
public class CustomDialog extends Dialog {

    private final Context mContext;
    private TypeDialog typeDialog;
    private String title,message;
    private LinearLayout llTitle;
    private TextView txtTitle,txtMessage;

    public CustomDialog(@NonNull Context context) {
        this(context,"","");
    }

    public CustomDialog(@NonNull Context context,String title,String message) {
        this(context,title,message, TypeDialog.INFO);
    }

    public CustomDialog(@NonNull Context context, String title, String message, TypeDialog typeDialog) {
        super(context);
        this.mContext = context;
        this.message = message;
        this.title = title;
        this.typeDialog = typeDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setContentView(R.layout.dialog_custom);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        txtTitle = findViewById(R.id.txtTitle);
        txtMessage = findViewById(R.id.txtMessage);
        llTitle = findViewById(R.id.llTitle);
        setTitle(title);
        setMessage(message);
        setTypeDialog(typeDialog);
        findViewById(R.id.btClose).setOnClickListener(v -> dismiss());
    }


    public void setTypeDialog(TypeDialog typeDialog){
        this.typeDialog = typeDialog;
        if(llTitle == null)return;
        switch (typeDialog){
            case ERROR:
                llTitle.setBackground(mContext.getResources().getDrawable(R.drawable.background_dialog_error,mContext.getTheme()));
                break;
            default:
            case INFO:
                llTitle.setBackground(mContext.getResources().getDrawable(R.drawable.background_dialog_info,mContext.getTheme()));
                break;
            case SUCCESS:
                llTitle.setBackground(mContext.getResources().getDrawable(R.drawable.background_dialog_success,mContext.getTheme()));
                break;
            case WARING:
                llTitle.setBackground(mContext.getResources().getDrawable(R.drawable.background_dialog_warnig,mContext.getTheme()));
                break;
        }
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

}