package it.motta.mbdage.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;


import com.google.android.gms.maps.model.LatLng;

import it.motta.mbdage.R;

@SuppressLint("NonConstantResourceId")
public class CreaVarcoDialog extends Dialog implements View.OnClickListener{

  private static final int RESULT_OK = 1 ;
  private static final int REQUEST_IMAGE_CAPTURE = 2;
  private Button btCrea,btAnnulla;
  private CardView takePhoto;
  private ImageView photo;
  private Context mContext;
  private LatLng latLng;
  private Bitmap imageBitmap;
  public CreaVarcoDialog(@NonNull Context context, LatLng latLng) {
    super(context);
    this.latLng = latLng;
    this.mContext = context;
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

    btCrea.setOnClickListener(this);
    btAnnulla.setOnClickListener(this);
    takePhoto.setOnClickListener(this);





  }


  public  void onTakePhoto(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE) {
      Bundle extras = data.getExtras();
      imageBitmap = (Bitmap) extras.get("data");
      photo.setImageBitmap(imageBitmap);
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.btCrea:
        break;
      case R.id. takePhoto:
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((FragmentActivity)mContext).startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        return;
    }
    dismiss();
  }


}
