package it.motta.mbdage.dialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import it.motta.mbdage.R;


@SuppressLint("UseCompatLoadingForDrawables")
public class ConfirmDialog extends DialogFragment implements View.OnClickListener{

    public static final String TAG = "cf_dialog";
    private String title,message,postiveText,negativeText;
    private View.OnClickListener positiveHandler,negativeHandler;
    private FragmentManager fragmentManager;
    private Button btPositive,btNegative;
    private int bgPositive,bgNegative;

    public ConfirmDialog(FragmentManager fragmentManager,String title,String message){
        this.fragmentManager = fragmentManager;
        this.title = title;
        this.message = message;
    }

    public void setPositiveClickListner(String message,View.OnClickListener handler){
        setPositiveClickListner(message, handler, -1);
    }

    public void setPositiveClickListner(String message, View.OnClickListener handler, @DrawableRes int background){
        postiveText = message;
        positiveHandler = handler;
         bgPositive = background;
        if(btPositive != null){
            btPositive.setText(postiveText);
            btPositive.setOnClickListener(this);
            if(background != -1)
            btPositive.setBackground(getResources().getDrawable(background, getActivity().getTheme()));
            btPositive.setVisibility(View.VISIBLE);
        }
    }


    public void setNegativeClickListner(String message,View.OnClickListener handler){
        setNegativeClickListner(message, handler, -1);
    }

    public void setNegativeClickListner(String message, View.OnClickListener handler, @DrawableRes int background){
        negativeText = message;
        negativeHandler = handler;
        bgNegative = background;
        if(btNegative != null){
            btNegative.setText(negativeText);
            btNegative.setOnClickListener(this);
            if(background != -1)
                btNegative.setBackground(getResources().getDrawable(background, getActivity().getTheme()));
            btNegative.setVisibility(View.VISIBLE);
        }
    }

    public void show(){
        show(fragmentManager,TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCancelable(false);
        View dialog = inflater.inflate(R.layout.dialog_confirm, container, false);
        btPositive = dialog.findViewById(R.id.btPositive);
        btNegative = dialog.findViewById(R.id.btNegative);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText(message);
        txtTitle.setText(title);
        btPositive.setVisibility(View.INVISIBLE);
        if(postiveText.length() > 0)
            setPositiveClickListner(postiveText, positiveHandler,bgPositive);
        if(negativeText.length() > 0)
            setNegativeClickListner(negativeText, negativeHandler,bgNegative);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btPositive:
                if(positiveHandler != null)positiveHandler.onClick(v);
                break;
            case R.id.btNegative:
                if(negativeHandler != null)negativeHandler.onClick(v);
                break;
        }
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Window window = Objects.requireNonNull(getDialog()).getWindow();

        if(window != null) {
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 48, 24, 48, 36);
            window.setBackgroundDrawable(inset);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.Animation_showDialog);
        }

    }
}
