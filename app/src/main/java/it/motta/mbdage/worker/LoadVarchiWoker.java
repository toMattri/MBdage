package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.interfaces.ILoadVarchi;
import it.motta.mbdage.response.ResponsePassaggi;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.TraduceComunication;

@SuppressLint("StaticFieldLeak")
public class LoadVarchiWoker extends AsyncTask<Void,Void,String> {

    private final Context mContext;
    private final int idUtente;
    private final DBHandler dbHandler;
    private final ILoadVarchi iLoadVarchi;

    public LoadVarchiWoker(Context mContext,int idUtente) {
        this(mContext,idUtente,null);
    }

    public LoadVarchiWoker(Context mContext,int idUtente,ILoadVarchi iLoadVarchi) {
        super();
        this.mContext = mContext;
        this.idUtente = idUtente;
        this.iLoadVarchi = iLoadVarchi;
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
                        if(iLoadVarchi != null)iLoadVarchi.OnSuccess();
                        break;
                    case SUCCESS:
                        dbHandler.writeVarchi(TraduceComunication.getVarchi(jsonObject.getJSONArray("Varchi")));
                        if(iLoadVarchi != null)iLoadVarchi.OnSuccess();
                        break;
                    default:
                        if(iLoadVarchi != null)iLoadVarchi.ErroGeneric();

                }
            }catch (Exception ex) {
                ex.printStackTrace();
                if(iLoadVarchi != null)iLoadVarchi.ErroGeneric();
            }
        }, error -> {
            error.printStackTrace();
            if(iLoadVarchi != null)iLoadVarchi.ErroGeneric();
        });
        return null;
    }

}
