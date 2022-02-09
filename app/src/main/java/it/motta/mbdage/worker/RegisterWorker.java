package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import it.motta.mbdage.dialog.ProgressCDialog;
import it.motta.mbdage.interfaces.IAccessOperation;
import it.motta.mbdage.response.ResponseAccess;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.TraduceComunication;

@SuppressLint("StaticFieldLeak")
public class RegisterWorker extends AsyncTask<Void,Void,String> {

    private final Utente utente;
    private final Context mContext;
    private final String typeLogin;
    private final IAccessOperation iAccessOperation;
    private ProgressCDialog progressCDialog;

    public RegisterWorker(Context mContext, Utente utente, String typeLogin, IAccessOperation iAccessOperation) {
        super();
        this.utente = utente;
        this.typeLogin = typeLogin;
        this.mContext = mContext;
        this.iAccessOperation = iAccessOperation;
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
        return null;
    }

}
