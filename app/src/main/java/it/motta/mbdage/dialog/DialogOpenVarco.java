package it.motta.mbdage.dialog;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import it.motta.mbdage.R;

public class DialogOpenVarco extends DialogFragment {

  public static final String TAG = "vo_dialog";
  private final FragmentManager fragmentManager;

  public DialogOpenVarco(FragmentManager fragmentManager) {
    this.fragmentManager = fragmentManager;
  }

  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View dialog = inflater.inflate(R.layout.open_varco_dialog, container, false);
    return dialog;
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
      window.setGravity(Gravity.TOP);
      window.setWindowAnimations(R.style.Animation_showDialog_Varco);
    }

  }

  public void show(){
    show(fragmentManager,TAG);
  }

}
