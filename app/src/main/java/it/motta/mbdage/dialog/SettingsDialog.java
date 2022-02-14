package it.motta.mbdage.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import it.motta.mbdage.BuildConfig;
import it.motta.mbdage.R;
import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.interfaces.IAccessOperation;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.evalue.TypeDialog;
import it.motta.mbdage.models.evalue.TypeLogin;
import it.motta.mbdage.models.evalue.TypeUtente;
import it.motta.mbdage.models.response.ResponseAccess;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.utils.Utils;
import it.motta.mbdage.worker.RegisterWorker;

@SuppressLint({"SimpleDateFormat", "NonConstantResourceId","ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
public class SettingsDialog extends DialogFragment implements View.OnClickListener{

  public static final String TAG = "st_dialog";
  private final FragmentManager fragmentManager;

  private TextInputEditText edtDataRegistrati;
  private Utente utente;
  private static final int REQUEST_IMAGE_CAPTURE = 2;
  private ImageView imgProfilo;
  private ImageButton btDelete;
  private Bitmap imageBitmap;
  private View.OnClickListener dismiss;
  public SettingsDialog(FragmentManager fragmentManager, Utente utente,View.OnClickListener dismiss) {
    this.fragmentManager = fragmentManager;
    this.utente = utente;
    this.dismiss = dismiss;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  public void onTakePhoto(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE) {
      if(data != null) {
        Bundle extras = data.getExtras();
        imageBitmap = (Bitmap) extras.get("data");
        imgProfilo.setImageBitmap(imageBitmap);
        btDelete.setVisibility(View.VISIBLE);
      }
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.btSave:
        String data = edtDataRegistrati.getText().toString() ;
        if(StringUtils.isEmpty(data)) {
          new CustomDialog(getContext() , getResources().getString(R.string.attenzione), getResources().getString(R.string.err_dats), TypeDialog.WARING).show();
          return;
        }
        try{
          data = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(data));
          utente.setData(data);
        }catch (Exception ex){
          ex.printStackTrace();
          new CustomDialog(getContext(), getResources().getString(R.string.errore), getResources().getString(R.string.err_generico), TypeDialog.ERROR).show();
          return;
        }
        new RegisterWorker(getContext() ,utente, TypeLogin.EMIAL,iAccessOperation,imageBitmap).execute();
        break;
      case R.id.btLogOut:
        Utils.logOut(getContext() );
        break;
      case R.id.btDelete:
        imageBitmap = null;
        btDelete.setVisibility(View.GONE);
        imgProfilo.setImageDrawable(getResources().getDrawable(R.drawable.ic_default_user,null));
        break;
      case  R.id.imgProfilo:
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        break;
      case R.id.btClose:
        dismiss.onClick(view);
        dismiss();
    }
  }


  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    onTakePhoto(requestCode, resultCode, data);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View dialog = inflater.inflate(R.layout.settings_dialog, container, false);
    setCancelable(false);
    TextView txtDisplayName = dialog.findViewById(R.id.txtDisplayName);
    TextView txtEmail = dialog.findViewById(R.id.txtEmail);
    TextView txtInfo = dialog.findViewById(R.id.txtInfo);
    edtDataRegistrati = dialog.findViewById(R.id.edtDataSetting);
    imgProfilo = dialog.findViewById(R.id.imgProfilo);
    btDelete = dialog.findViewById(R.id.btDelete);
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

    if (!StringUtils.isEmpty(utente.getImageUrl())) {
      if(utente.getImageUrl().contains("https"))
        Picasso.get().load(Uri.decode(utente.getImageUrl())).resize(80, 80).centerInside().into(imgProfilo);
      else{
        FirebaseStorage storage = FirebaseStorage.getInstance(Parameters.PATH_STORAGE);
        StorageReference storageRef = storage.getReference();
        storageRef.child(utente.getImageUrl()).getDownloadUrl().addOnSuccessListener( uri -> Picasso.get().load(uri).into(imgProfilo)).
            addOnFailureListener(Throwable::printStackTrace);
      }
    }else
      btDelete.setVisibility(View.GONE);

    Button btSave = dialog.findViewById(R.id.btSave);
    Button btLogOut = dialog.findViewById(R.id.btLogOut);
    Button btClose = dialog.findViewById(R.id.btClose);
    btSave.setOnClickListener(this);
    btDelete.setOnClickListener(this);
    btClose.setOnClickListener(this);
    imgProfilo.setOnClickListener(this);
    btLogOut.setOnClickListener(this);

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
      window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
      window.setGravity(Gravity.CENTER);
      window.setWindowAnimations(R.style.Animation_showDialogSetting);
    }

  }

  private final IAccessOperation iAccessOperation = new IAccessOperation() {
    @Override
    public void OnCompleteOperation(JSONObject response) {
      try {
        switch (ResponseAccess.fromValue(response.getInt("result"))){
          case SUCCESS:
            DBHandler.getIstance(getContext()).logginUser(utente);
            new CustomDialog(getContext() ,getResources().getString(R.string.successo),getResources().getString(R.string.info_aggiorn), TypeDialog.SUCCESS).show();
            break;
          case ERROR_ON_UPGRADE:
            new CustomDialog(getContext() ,getResources().getString(R.string.errore),getResources().getString(R.string.err_update_param), TypeDialog.WARING).show();
            break;
          case ERR_PARAM:
            new CustomDialog(getContext() ,getResources().getString(R.string.errore),getResources().getString(R.string.err_param), TypeDialog.WARING).show();
        }
      }catch (Exception ex){
        ex.printStackTrace();
        new CustomDialog(getContext() ,getResources().getString(R.string.errore),getResources().getString(R.string.err_generico), TypeDialog.ERROR).show();
      }
    }
    @Override
    public void OnError() {
      new CustomDialog(getContext() ,getResources().getString(R.string.errore),getResources().getString(R.string.err_server), TypeDialog.WARING).show();
    }

    @Override
    public void OnErroreLoadImage() {
      new CustomDialog(getContext() ,getResources().getString(R.string.errore),getResources().getString(R.string.err_load_imgutente), TypeDialog.WARING).show();
    }

  };

  private void saveDataRegistrazione(TextInputEditText edt) {
    try {
      Date date = Objects.requireNonNull(edt.getText()).toString().length() == 0 ? Utils.getFirstDayOfYear(20) : new SimpleDateFormat("dd/MM/yyyy").parse(edt.getText().toString());
      DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(getContext(), getResources().getString(R.string.data_nascita), date);
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


  public void show(){
    show(fragmentManager,TAG);
  }

}