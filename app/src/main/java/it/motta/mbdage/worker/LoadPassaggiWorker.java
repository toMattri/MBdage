package it.motta.mbdage.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import it.motta.mbdage.database.DBHandler;
import it.motta.mbdage.interfaces.ILoadPassaggi;
import it.motta.mbdage.models.filter.FilterPassaggi;
import it.motta.mbdage.models.response.ResponsePassaggi;
import it.motta.mbdage.utils.MakeHttpRequest;
import it.motta.mbdage.utils.TraduceComunication;
import it.motta.mbdage.utils.Utils;

@SuppressLint("StaticFieldLeak")
public class LoadPassaggiWorker extends AsyncTask<Void,Void,String> {

    private final Context mContext;
    private final FilterPassaggi filterPassaggi;
    private final DBHandler dbHandler;
    private final ILoadPassaggi iLoadPassaggi;
    private final boolean reload;

    public LoadPassaggiWorker(Context mContext, FilterPassaggi filterPassaggi, ILoadPassaggi iLoadPassaggi,boolean reload) {
        super();
        this.mContext = mContext;
        this.reload = reload;
        this.iLoadPassaggi = iLoadPassaggi;
        this.filterPassaggi = filterPassaggi;
        this.dbHandler = DBHandler.getIstance(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        if(Utils.internetAvailability()) {
            MakeHttpRequest.sendPost(mContext, MakeHttpRequest.BASE_IP + MakeHttpRequest.GET_PASSAGGI,TraduceComunication.traduce(filterPassaggi), response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    switch (ResponsePassaggi.fromValue(jsonObject.getInt("result"))) {
                        case NOT_FIND:
                            if(iLoadPassaggi != null)
                                iLoadPassaggi.OnCompleteWithout();
                            break;
                        case SUCCESS:
                            dbHandler.writePassaggi(TraduceComunication.getPassaggi(jsonObject.getJSONArray("Passaggi")),reload);
                            iLoadPassaggi.OnComplete();
                            break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    iLoadPassaggi.OnError();
                }
            }, error -> {
                iLoadPassaggi.OnError();
            });
        }
        return null;
    }

}
