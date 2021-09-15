package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.dialog.ConfirmDialog;
import it.motta.mbdage.dialog.DateTimePickerDialog;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.utils.Utils;


@SuppressLint({"ResourceType","ClickableViewAccessibility","NonConstantResourceId"})
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmailAccedi, edtPasswordAccedi,
            edtNomeRegistrati, edtCognomeRegistrati, edtEmailRegistrati, edtPasswordRegistrati, edtDataRegistrati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        Button btAccedi, btRegistrati;
        TextView txtRegistrati, txtDesc;
        CardView cardLogin, cardRegistrati;

        txtRegistrati = findViewById(R.id.txtRegistrati);
        txtDesc = findViewById(R.id.txtDesc);

        cardLogin = findViewById(R.id.cardLogin);
        cardRegistrati = findViewById(R.id.cardRegister);

        edtEmailAccedi = findViewById(R.id.edtEmailAccedi);
        edtPasswordAccedi = findViewById(R.id.edtPasswordAccedi);

        edtNomeRegistrati = findViewById(R.id.edtNomeRegistrati);
        edtCognomeRegistrati = findViewById(R.id.edtCognomeRegistrati);
        edtEmailRegistrati = findViewById(R.id.edtEmailRegistrati);

        edtPasswordRegistrati = findViewById(R.id.edtPasswordRegistrati);

        edtDataRegistrati = findViewById(R.id.edtDataRegistrati);

        btRegistrati = findViewById(R.id.btRegistrati);
        btAccedi = findViewById(R.id.btAccedi);

        txtRegistrati.setTag(true);
        Animation centerToRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pushright);
        Animation leftToCenter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fromleft);

        View.OnClickListener handler = v -> {
            switch (v.getId()) {
                case R.id.txtRegistrati:
                    if ((boolean) v.getTag()) {
                        cardLogin.setVisibility(View.GONE);
                        cardRegistrati.setVisibility(View.VISIBLE);
                        cardLogin.startAnimation(centerToRight);
                        cardRegistrati.startAnimation(leftToCenter);
                        txtRegistrati.setTag(false);
                        txtRegistrati.setText("Accedi");
                        txtDesc.setText("Hai già un account ?");
                    } else {
                        txtRegistrati.setTag(true);
                        txtDesc.setText("Non hai ancora un account ?");
                        txtRegistrati.setText("Registrati");
                        cardLogin.setVisibility(View.VISIBLE);
                        cardRegistrati.setVisibility(View.GONE);
                        cardLogin.startAnimation(leftToCenter);
                        cardRegistrati.startAnimation(centerToRight);
                    }
                    disableAllerror();
                    break;
                case R.id.btRegistrati:
                    boolean blRegistrati = true;
                    if (edtDataRegistrati.getText().toString().length() == 0) {
                        edtDataRegistrati.setError("");
                        blRegistrati = false;
                    }

                    if (edtNomeRegistrati.getText().toString().length() == 0 || edtNomeRegistrati.getText().toString().length() > 20) {
                        edtNomeRegistrati.setError("Inserire un nome valido");
                        blRegistrati = false;
                    }
                    if (edtCognomeRegistrati.getText().toString().length() == 0 || edtCognomeRegistrati.getText().toString().length() > 20) {
                        edtCognomeRegistrati.setError("Inserire un cognome valido");
                        blRegistrati = false;
                    }
                    if (edtEmailRegistrati.getText().toString().length() == 0 || Utils.valideEmail(edtEmailRegistrati.getText().toString())) {
                        edtEmailRegistrati.setError("Inserire una email valida");
                        blRegistrati = false;
                    }

                    if (edtPasswordRegistrati.getText().toString().length() == 0 || edtPasswordRegistrati.getText().toString().length() > 20) {
                        blRegistrati = false;
                        TextInputLayout textInputLayout = (TextInputLayout) findViewById(edtPasswordRegistrati.getId()).getParent().getParent();
                        textInputLayout.setEndIconVisible(false);
                        edtPasswordRegistrati.setError("Inserire una password valida");
                    }

                    if (!blRegistrati) return;

                    /* TODO REGISTREAZIONe*/
                    break;
                case R.id.btAccedi:

                    MakeHttpRequest.sendPost();

                    boolean blAccedi = true;
                    if (edtEmailAccedi.getText().toString().length() == 0 || Utils.valideEmail(edtEmailAccedi.getText().toString())) {
                        edtEmailAccedi.setError("Inserire una email valida");
                        blAccedi = false;
                    }

                    if (edtPasswordAccedi.getText().toString().length() == 0 || edtPasswordAccedi.getText().toString().length() > 20) {
                        blAccedi = false;
                        TextInputLayout textInputLayout = (TextInputLayout) findViewById(edtPasswordAccedi.getId()).getParent().getParent();
                        textInputLayout.setEndIconVisible(false);
                        edtPasswordAccedi.setError("Inserire una password valida");
                    }

                    if (!blAccedi) return;
                    /* TODO Accesso*/

            }
        };

        txtRegistrati.setOnClickListener(handler);

        btRegistrati.setOnClickListener(handler);
        btAccedi.setOnClickListener(handler);

        edtPasswordRegistrati.setOnKeyListener((v, keyCode, event) -> {
            ((TextInputLayout) findViewById(v.getId()).getParent().getParent()).setEndIconVisible(true);
            return false;
        });

        edtPasswordAccedi.setOnKeyListener((v, keyCode, event) -> {
            ((TextInputLayout) findViewById(v.getId()).getParent().getParent()).setEndIconVisible(true);
            return false;
        });

        edtDataRegistrati.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ((TextInputLayout) findViewById(edtEmailRegistrati.getId()).getParent().getParent()).setEndIconVisible(true);
                saveDataRegistrazione(edtDataRegistrati);
                edtDataRegistrati.clearFocus();
            }
        });

        edtDataRegistrati.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                saveDataRegistrazione(edtDataRegistrati);
            }
            return true;
        });

    }

    @SuppressLint("SimpleDateFormat")
    private void saveDataRegistrazione(TextInputEditText edt) {
        try {

            Date date = Objects.requireNonNull(edt.getText()).toString().length() == 0 ? Utils.getFirstDayOfYear(20) : new SimpleDateFormat("dd/MM/yyyy").parse(edt.getText().toString());
            DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(this, "Data di nascita", date);
            dateTimePickerDialog.setOnDismissListener(dialog -> {
                if (dateTimePickerDialog.getDateChoosed() != null) {
                    edt.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTimePickerDialog.getDateChoosed()));
                }
            });
            dateTimePickerDialog.show();
        } catch (ParseException e) {
            if (Parameters.DEBUG_MODE) e.printStackTrace();

        }
    }

    private void disableAllerror() {
        edtPasswordAccedi.setError(null);
        edtEmailAccedi.setError(null);
        edtEmailRegistrati.setError(null);
        edtPasswordRegistrati.setError(null);
        edtNomeRegistrati.setError(null);
        edtDataRegistrati.setError(null);
        edtCognomeRegistrati.setError(null);
        edtPasswordAccedi.setText("");
        edtEmailAccedi.setText("");
        edtEmailRegistrati.setText("");
        edtPasswordRegistrati.setText("");
        edtNomeRegistrati.setText("");
        edtDataRegistrati.setText("");
        edtCognomeRegistrati.setText("");
    }

}