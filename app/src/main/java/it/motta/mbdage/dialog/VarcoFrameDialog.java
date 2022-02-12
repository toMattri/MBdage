package it.motta.mbdage.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import it.motta.mbdage.R;
import it.motta.mbdage.controller.QrCodeController;
import it.motta.mbdage.models.Varco;

public class VarcoFrameDialog extends DialogFragment implements View.OnClickListener{

  public static final String TAG = "vf_dialog";
  private final Varco varco;
  private final FragmentManager fragmentManager;
  private CardView llExport;

  public VarcoFrameDialog(FragmentManager fragmentManager, Varco varco) {
    this.fragmentManager = fragmentManager;
    this.varco = varco;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_MBdage);

  }

  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.btEsporta:

        try {
          Bitmap scBitmap;
          llExport.setDrawingCacheEnabled(true);
          scBitmap = Bitmap.createBitmap(llExport.getDrawingCache());
          llExport.setDrawingCacheEnabled(false);
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          scBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
          byte[] byteArray = stream.toByteArray();
          Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
          PdfDocument pdfDocument = new PdfDocument();
          Paint paint = new Paint();

          PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(llExport.getWidth(), llExport.getHeight(), 1).create();
          PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
          Canvas canvas = myPage.getCanvas();
          canvas.drawBitmap(bitmap, 0, 0, paint);

          File varcoFile = File.createTempFile(varco.getDescrizione(), ".pdf", getContext().getFilesDir());

          pdfDocument.finishPage(myPage);
          pdfDocument.writeTo(new FileOutputStream(varcoFile));
          Intent sendIntent = new Intent();
          sendIntent.setAction(Intent.ACTION_SEND);
          sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          sendIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

          sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), "it.motta.mbdage.fileprovider", varcoFile));
          sendIntent.setType("application/pdf");

          startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_varcoimg)));
        }catch (Exception ex){
          ex.printStackTrace();
        }
    }
    dismiss();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    setCancelable(false);
    View dialog = inflater.inflate(R.layout.varco_frame_dialog, container, false);
    Button btClose,btExport;
    TextView txtMessage = dialog.findViewById(R.id.txtVarco);
    btClose = dialog.findViewById(R.id.btClose);
    btExport = dialog.findViewById(R.id.btEsporta);
    llExport = dialog.findViewById(R.id.llExport);
    txtMessage.setText(varco.getDescrizione());
    btClose.setOnClickListener(this);
    btExport.setOnClickListener(this);
    ImageView imgQr = dialog.findViewById(R.id.icQrCode);
    try {
      imgQr.setImageBitmap(QrCodeController.traduceQrCode(varco.getJsonObj(),500));
    } catch (WriterException e) {
      e.printStackTrace();
    }

    return dialog;
  }

  @Override
  public void onStart() {
    super.onStart();
    final Window window = Objects.requireNonNull(getDialog()).getWindow();

    if(window != null) {
      ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
      InsetDrawable inset = new InsetDrawable(back, 24, 24, 24, 24);
      window.setBackgroundDrawable(inset);
      window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
      window.setGravity(Gravity.CENTER);
      window.setWindowAnimations(R.style.Animation_showDialog_Varco);
    }

  }

  public void show(){
    show(fragmentManager,TAG);
  }

}