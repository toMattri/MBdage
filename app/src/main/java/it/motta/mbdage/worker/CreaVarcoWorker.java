package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import it.motta.mbdage.dialog.ProgressCDialog;
import it.motta.mbdage.interfaces.ICreateVarco;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.response.ResponseCreaVarco;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.Parameters;
import it.motta.mbdage.utils.TraduceComunication;
import it.motta.mbdage.utils.Utils;

@SuppressLint("StaticFieldLeak")
public class CreaVarcoWorker extends AsyncTask<Void,Void,String> {

    private final Context mContext;
    private final ICreateVarco iCreateVarco;
    private final Varco varco;
    private final Bitmap img;
    private ProgressCDialog progressCDialog;

    public CreaVarcoWorker(Context mContext, Varco varco, Bitmap img, ICreateVarco iCreateVarco) {
        super();
        this.mContext = mContext;
        this.varco = varco;
        this.img = img;
        this.iCreateVarco = iCreateVarco;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressCDialog = new ProgressCDialog(mContext);
        progressCDialog.setTitle("Caricamento in corso");
        progressCDialog.setMessage("Registrazione in corso...");
        progressCDialog.show();
    }

    private void closeLogging(){
        if(progressCDialog != null)
            progressCDialog.dismiss();
    }

    @Override
    protected String doInBackground(Void... voids) {
        if(img != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance(Parameters.PATH_STORAGE);
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("imagesVarco");
            StorageReference image = imagesRef.child(Utils.md5Work(varco.getDescrizione() + varco.getLongitudine()) + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = image.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                iCreateVarco.ErroOnLoadImageg();
            }).addOnSuccessListener(taskSnapshot -> {
                varco.setImg(image.getPath());
                loadVarco();
            });
        }else
            loadVarco();
        return null;
    }

    private void loadVarco(){
        MakeHttpRequest.sendPost(mContext, MakeHttpRequest.BASE_IP + MakeHttpRequest.CREA_VARCO, TraduceComunication.traduce(varco), response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                switch (ResponseCreaVarco.fromValue(jsonObject.getInt("result"))) {
                    case SUCCESS:
                        varco.setId(jsonObject.getInt("id"));
                        iCreateVarco.OnSuccess(varco);
                        break;
                    case ERROR:
                        iCreateVarco.ErroGeneric();
                        break;
                    case ERR_PARAM:
                        iCreateVarco.ErrorParam();
                        break;
                    case ALREADY:
                        iCreateVarco.AlreadyCreated();
                }
                closeLogging();
            } catch (Exception exception) {
                exception.printStackTrace();
                iCreateVarco.ErroGeneric();
                closeLogging();
            }
        }, error -> {
            iCreateVarco.ErrorConnection();
            closeLogging();
        });
    }

}