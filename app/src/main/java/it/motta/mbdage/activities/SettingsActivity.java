package it.motta.mbdage.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import it.motta.mbdage.BuildConfig;
import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.dialog.CustomDialog;
import it.motta.mbdage.dialog.DateTimePickerDialog;
import it.motta.mbdage.interfaces.IAccessOperation;
import it.motta.mbdage.message.ResultAccess;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.evalue.TypeDialog;
import it.motta.mbdage.models.evalue.TypeLogin;
import it.motta.mbdage.models.evalue.TypeUtente;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.utils.Utils;
import it.motta.mbdage.worker.RegisterWorker;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

  private TextInputEditText edtDataRegistrati;
  private Utente utente;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null)
      actionBar.setDisplayHomeAsUpEnabled(true);

    utente = (Utente) getIntent().getSerializableExtra(Parameters.INTENT_UTENTE);
    TextView txtDisplayName = findViewById(R.id.txtDisplayName);
    TextView txtEmail = findViewById(R.id.txtEmail);
    TextView txtInfo = findViewById(R.id.txtInfo);
    edtDataRegistrati = findViewById(R.id.edtDataSetting);
    txtDisplayName.setText(utente.getDisplayName());
    txtEmail.setText(utente.getEmail());
    if(!StringUtils.isEmpty(utente.getData()) && !utente.getData().equalsIgnoreCase("null")) {
      try {
       String data = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat( "yyyy-MM-dd").parse(utente.getData()));
        edtDataRegistrati.setText((data));
      }catch (Exception exx){
        exx.printStackTrace();
      }
    } else
      edtDataRegistrati.setText("");


    if(!utente.getTipoUtente().equals(TypeUtente.NOCOMPLETED)){
      txtInfo.setVisibility(View.GONE);
    }

    edtDataRegistrati.setOnFocusChangeListener((v, hasFocus) -> {
      if(hasFocus){
        saveDataRegistrazione(edtDataRegistrati);
        edtDataRegistrati.clearFocus();
      }
    });

    edtDataRegistrati.setOnTouchListener((v, event) -> {
      if (event.getAction() == MotionEvent.ACTION_DOWN)
        saveDataRegistrazione(edtDataRegistrati);
      return true;
    });

    Button btSave = findViewById(R.id.btSave);
    Button btLogOut = findViewById(R.id.btLogOut);
    btSave.setOnClickListener(this);
    btLogOut.setOnClickListener(this);
  }

  @SuppressLint({"SimpleDateFormat", "NonConstantResourceId"})
  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.btSave:
        String data =edtDataRegistrati.getText().toString() ;
        if(StringUtils.isEmpty(data)) {
          new CustomDialog(this, "Attenzione", "Si prega di inserire una data!", TypeDialog.WARING).show();
          return;
        }
        try{
          data = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(data));
          utente.setData(data);
        }catch (Exception ex){
          ex.printStackTrace();
          new CustomDialog(this,"Errore","Si è verificato un errore", TypeDialog.ERROR).show();
          return;
        }

        new RegisterWorker(this,utente, TypeLogin.EMIAL,iAccessOperation).execute();

        break;
      case R.id.btLogOut:
        Utils.logOut(this);
        break;
    }

  }

  private final IAccessOperation iAccessOperation = new IAccessOperation() {
    @Override
    public void OnCompleteOperation(JSONObject response) {
      try {
        switch (ResultAccess.fromValue(response.getInt("result"))){
          case SUCCESS:
            DBHandler.getIstance(SettingsActivity.this).logginUser(utente);
            new CustomDialog(SettingsActivity.this,"Operazione completata","Informazioni aggiornate", TypeDialog.SUCCESS).show();
            break;
          case ERROR_ON_UPGRADE:
            new CustomDialog(SettingsActivity.this,"Errore ","Si è verificato un errore durante l'aggiornamento dei parametri!", TypeDialog.WARING).show();
            break;
          case ERR_PARAM:
            new CustomDialog(SettingsActivity.this,"Errore ","Si è verificato un errore nel passaggio dei parametri!", TypeDialog.WARING).show();

        }

      }catch (Exception ex){
        ex.printStackTrace();
        new CustomDialog(SettingsActivity.this,"Errore","Si è verificato un errore", TypeDialog.ERROR).show();
      }
    }

    @Override
    public void OnError() {
      new CustomDialog(SettingsActivity.this,"Errore ","Si è verificato un errore durante la comunicazione con il server", TypeDialog.WARING).show();
    }
  };





  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  @SuppressLint("SimpleDateFormat")
  private void saveDataRegistrazione(TextInputEditText edt) {
    try {
      Date date = Objects.requireNonNull(edt.getText()).toString().length() == 0 ? Utils.getFirstDayOfYear(20) : new SimpleDateFormat("dd/MM/yyyy").parse(edt.getText().toString());
      DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(this, "Data di nascita", date);
      dateTimePickerDialog.setOnDismissListener(dialog -> {
        if (dateTimePickerDialog.getDateChoosed() != null)
          edt.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTimePickerDialog.getDateChoosed()));
      });
      dateTimePickerDialog.show();
    } catch (ParseException e) {
      if (BuildConfig.DEBUG)
        e.printStackTrace();
    }
  }


}