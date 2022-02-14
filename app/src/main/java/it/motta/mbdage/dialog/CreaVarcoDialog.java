package it.motta.mbdage.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import it.motta.mbdage.R;
import it.motta.mbdage.interfaces.ICreateVarco;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.worker.CreaVarcoWorker;

@SuppressLint("NonConstantResourceId")
public class CreaVarcoDialog extends Dialog implements View.OnClickListener{

  private static final int RESULT_OK = 1 ;
  private static final int REQUEST_IMAGE_CAPTURE = 2;
  private Button btCrea,btAnnulla;
  private ImageButton btDelete;
  private CardView takePhoto;
  private ImageView photo;
  private Context mContext;
  private LatLng latLng;
  private Bitmap imageBitmap;
  private TextInputEditText edtDescVarco;
  private ICreateVarco iCreateVarco;
  private TextView txtLat,txtLong;

  public CreaVarcoDialog(@NonNull Context context, LatLng latLng,ICreateVarco iCreateVarco) {
    super(context);
    this.latLng = latLng;
    this.mContext = context;
    this.iCreateVarco = iCreateVarco;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.crea_varco_dialog);
    setCancelable(false);

    btCrea = findViewById(R.id.btCrea);
    btAnnulla = findViewById(R.id.btAnnulla);
    photo = findViewById(R.id.imgVarco);
    takePhoto = findViewById(R.id.takePhoto);
    edtDescVarco = findViewById(R.id.edtDescVarco);

    txtLong = findViewById(R.id.txtLong);
    txtLat = findViewById(R.id.txtLat);
    btDelete = findViewById(R.id.btDelete);

    txtLat.setText(mContext.getResources().getText(R.string.lati) + " : " + latLng.latitude);
    txtLong.setText(mContext.getResources().getText(R.string.longi) + " : " + latLng.longitude);

    btCrea.setOnClickListener(this);
    btAnnulla.setOnClickListener(this);
    takePhoto.setOnClickListener(this);
    btDelete.setOnClickListener(this);
    btDelete.setVisibility(View.GONE);
    photo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_photo,null));
    photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
    photo.setPadding(4,4,4,4);
    edtDescVarco.setOnKeyListener((v, keyCode, event) -> {
      ((TextInputLayout)findViewById(v.getId()).getParent().getParent()).setEndIconVisible(true);
      return false;
    });
  }

  public  void onTakePhoto(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE) {
      Bundle extras = data.getExtras();
      imageBitmap = (Bitmap) extras.get("data");
      photo.setImageBitmap(imageBitmap);
      btDelete.setVisibility(View.VISIBLE);
    } else {
      btDelete.setVisibility(View.GONE);
      photo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_photo,null));
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.btDelete:
        imageBitmap = null;
        btDelete.setVisibility(View.GONE);
        photo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_photo,null));
        return;
      case R.id.btCrea:
        if (edtDescVarco.getText().toString().length() == 0 || edtDescVarco.getText().toString().length() > 20) {
          TextInputLayout textInputLayout = (TextInputLayout) findViewById(edtDescVarco.getId()).getParent().getParent();
          textInputLayout.setEndIconVisible(false);
          edtDescVarco.setError(mContext.getResources().getString(R.string.err_password));
          return;
        }
        new CreaVarcoWorker(mContext,new Varco(0,latLng.latitude,latLng.longitude,edtDescVarco.getText().toString(),""),imageBitmap,iCreateVarco).execute();
        break;
      case R.id. takePhoto:
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((FragmentActivity)mContext).startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        return;
    }
    dismiss();
  }


}
