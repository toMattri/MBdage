package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import it.motta.mbdage.R;
import it.motta.mbdage.dialog.ProgressCDialog;
import it.motta.mbdage.interfaces.IAccessOperation;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.response.ResponseAccess;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.utils.TraduceComunication;
import it.motta.mbdage.utils.Utils;

@SuppressLint("StaticFieldLeak")
public class RegisterWorker extends AsyncTask<Void,Void,String> {

    private final Utente utente;
    private final Context mContext;
    private final String typeLogin;
    private final IAccessOperation iAccessOperation;
    private final Bitmap bitmap;
    private ProgressCDialog progressCDialog;

    public RegisterWorker(Context mContext, Utente utente, String typeLogin, IAccessOperation iAccessOperation, Bitmap bitmap) {
        super();
        this.utente = utente;
        this.bitmap = bitmap;
        this.typeLogin = typeLogin;
        this.mContext = mContext;
        this.iAccessOperation = iAccessOperation;
    }

    public RegisterWorker(Context mContext, Utente utente, String typeLogin, IAccessOperation iAccessOperation) {
        this(mContext,utente, typeLogin,iAccessOperation,null);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressCDialog = new ProgressCDialog(mContext);
        progressCDialog.setTitle(mContext.getResources().getString(R.string.loading));
        progressCDialog.setMessage(mContext.getResources().getString(R.string.registrazione_loading));
        progressCDialog.show();
    }

    private void closeLogging(){
        if(progressCDialog != null)
            progressCDialog.dismiss();
    }

    @Override
    protected String doInBackground(Void... voids) {
        if(bitmap != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance(Parameters.PATH_STORAGE);
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("imageUtente");
            StorageReference image = imagesRef.child(Utils.md5Work(utente.getId() + utente.getEmail()) + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = image.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                iAccessOperation.OnErroreLoadImage();
            }).addOnSuccessListener(taskSnapshot -> {
                utente.setImageUrl(image.getPath());
                registerWorker();
            });
        }else {
            if(utente.getId() != 0 && !StringUtils.isEmpty(utente.getImageUrl()))
                utente.setImageUrl("");
            registerWorker();
        }


        return null;
    }


    private  void registerWorker(){
        MakeHttpRequest.sendPost(mContext,MakeHttpRequest.BASE_IP + MakeHttpRequest.REGISTER, TraduceComunication.traduce(utente, typeLogin), response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(ResponseAccess.ALREADY_EXIST.equals(ResponseAccess.fromValue(jsonObject.getInt("result")))) {
                    MakeHttpRequest.sendPost(mContext, MakeHttpRequest.BASE_IP + MakeHttpRequest.LOGIN, TraduceComunication.traduce(utente, typeLogin), responseLogin -> {
                        try {
                            iAccessOperation.OnCompleteOperation(new JSONObject(responseLogin));
                            closeLogging();
                        }
                        catch (Exception exception){
                            exception.printStackTrace();
                            iAccessOperation.OnError();
                            closeLogging();
                        }
                    }, error -> {
                        iAccessOperation.OnError();
                        closeLogging();
                    });

                }
                else{
                    iAccessOperation.OnCompleteOperation(new JSONObject(response));
                    closeLogging();
                }
            }
            catch (Exception exception){
                exception.printStackTrace();
                iAccessOperation.OnError();
                closeLogging();
            }
        }, error -> {
            iAccessOperation.OnError();
            closeLogging();
        });
    }


}
