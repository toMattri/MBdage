package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import it.motta.mbdage.R;
import it.motta.mbdage.dialog.ProgressCDialog;
import it.motta.mbdage.interfaces.IOpenVarco;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.models.response.ResponseOpenVarco;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.TraduceComunication;

@SuppressLint("StaticFieldLeak")
public class OpenVarcoWorker extends AsyncTask<Void,Void,String> {

  private final Utente utente;
  private final Varco varco;
  private final Context mContext;
  private final IOpenVarco iOpenVarco;
  private ProgressCDialog progressCDialog;

  public OpenVarcoWorker(Context mContext, Utente utente, Varco varco, IOpenVarco iOpenVarco) {
    super();
    this.utente = utente;
    this.varco = varco;
    this.mContext = mContext;
    this.iOpenVarco = iOpenVarco;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    progressCDialog = new ProgressCDialog(mContext);
    progressCDialog.setTitle(mContext.getResources().getString(R.string.loading));
    progressCDialog.setMessage(mContext.getResources().getString(R.string.open_varco));
    progressCDialog.show();
  }

  private void closeLogging(){
    if(progressCDialog != null)
      progressCDialog.dismiss();
  }

  @Override
  protected String doInBackground(Void... voids) {
    MakeHttpRequest.sendPost(mContext,MakeHttpRequest.BASE_IP + MakeHttpRequest.OPEN_VARCO, TraduceComunication.traduce(utente, varco), response -> {
      try {
        JSONObject jsonObject = new JSONObject(response);
        switch (ResponseOpenVarco.fromValue(jsonObject.getInt("result"))) {
          case SUCCESS:
            iOpenVarco.OnSuccess(jsonObject.has("update"));
            break;
          case ERR_PARAM:
            iOpenVarco.ErrorParam();
            break;
          case ERR_APERTURA:
            iOpenVarco.ErrorApertura();
            break;
          case TOKEN_EXPIRE:
            iOpenVarco.ErrToken();
        }
        closeLogging();
      }
      catch (Exception exception){
        exception.printStackTrace();
        iOpenVarco.ErroGeneric();
        closeLogging();

      }
    }, error -> {
      iOpenVarco.ErrorConnection();
      closeLogging();
    });
    return null;
  }

}
