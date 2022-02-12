package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.TraduceComunication;

@SuppressLint("StaticFieldLeak")
public class UpdateTokenWorker extends AsyncTask<Void,Void,Void> {

    private final int idUtente;
    private final Context mContext;
    private final String token,deviceName;

    public UpdateTokenWorker(Context context,int idUtente,String token) {
        super();
        this.token = token;
        this.mContext = context;
        this.idUtente = idUtente;
        this.deviceName = Build.BRAND.toUpperCase(Locale.ROOT) ;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MakeHttpRequest.sendPost(mContext,MakeHttpRequest.BASE_IP + MakeHttpRequest.REGISTER_TOKEN, TraduceComunication.traduce(idUtente,deviceName,token), response -> {
            Log.e("Response token",response);
        }, Throwable::printStackTrace);
        return null;
    }
}