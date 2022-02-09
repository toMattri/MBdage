package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.response.ResponsePassaggi;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.TraduceComunication;

@SuppressLint("StaticFieldLeak")
public class LoadVarchiWoker extends AsyncTask<Void,Void,String> {

    private final Context mContext;
    private final int idUtente;
    private final DBHandler dbHandler;

    public LoadVarchiWoker(Context mContext,int idUtente) {
        super();
        this.mContext = mContext;
        this.idUtente = idUtente;
        this.dbHandler = DBHandler.getIstance(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        MakeHttpRequest.sendPost(mContext, MakeHttpRequest.BASE_IP + MakeHttpRequest.GET_VARCHI, TraduceComunication.traduce(idUtente), response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                switch (ResponsePassaggi.fromValue(jsonObject.getInt("result"))){
                    case NOT_FIND:
                        break;
                    case SUCCESS:
                        dbHandler.writeVarchi(TraduceComunication.getVarchi(jsonObject.getJSONArray("Varchi")));
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        });
        return null;
    }

}
