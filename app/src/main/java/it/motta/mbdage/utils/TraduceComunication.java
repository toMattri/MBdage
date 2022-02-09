package it.motta.mbdage.utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.motta.mbdage.models.Passaggio;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.models.evalue.TypeUtente;
import it.motta.mbdage.models.filter.FilterPassaggi;

public class TraduceComunication {

    private static final String EMAIL = "email";
    private static final String DISPLAYNAME = "displayName";
    private static final String DATA_NASCITA = "datanascita";
    private static final String ID = "id";
    private static final String UID = "UID";
    private static final String TIPO = "tipo";
    private static final String ACCESSTYPE  = "accessType";
    private static final String URL_IMAGE  = "url_image";

    public static Utente getUtente(JSONObject jsonObject){
        try {
            return new Utente(jsonObject.getInt(ID),jsonObject.getString(DISPLAYNAME),jsonObject.getString(EMAIL),jsonObject.getString(DATA_NASCITA),TypeUtente.values()[jsonObject.getInt(TIPO)],jsonObject.getString(UID),jsonObject.getString(URL_IMAGE));
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Varco> getVarchi(JSONArray jsonArray){
        try {
            ArrayList<Varco> varchi = new ArrayList<>(jsonArray.length());
            JSONObject js;
            for(int i = 0;i<jsonArray.length();i++){
                js = jsonArray.getJSONObject(i);
                varchi.add(new Varco(js.getInt("id"),js.getDouble("latitudine"),js.getDouble("longitudine"),js.getString("descrizione"),js.getString("url_image")));
            }
            return varchi;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return new ArrayList<>();
        }

    }

    public static ArrayList<Passaggio> getPassaggi(JSONArray jsonArray){
        try {
            ArrayList<Passaggio> passaggis = new ArrayList<>(jsonArray.length());
            JSONObject js;
            for(int i = 0;i<jsonArray.length();i++){
                js = jsonArray.getJSONObject(i);
                passaggis.add(new Passaggio(js.getInt("id"),js.getInt("id_utente"),js.getInt("id_varco"),js.getString("data")));
            }
            return passaggis;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Map<String,String> traduce(Varco varco)  {
        Map<String,String> request = new HashMap<>();
        request.put("descrizione",varco.getDescrizione());
        if(!StringUtils.isEmpty(varco.getImg()))
            request.put("url_image",varco.getImg());

        request.put("longitudine",String.valueOf(varco.getLongitudine()));
        request.put("latitudine",String.valueOf(varco.getLatitudine()));
        return request;
    }


    public static Map<String,String> traduce(Utente utente,String typeLogin)  {
        Map<String,String> request = new HashMap<>();
        request.put(EMAIL,utente.getEmail());
        if(!StringUtils.isEmpty(utente.getDisplayName()))
            request.put(DISPLAYNAME,utente.getDisplayName());
        if(!StringUtils.isEmpty(utente.getData()))
            request.put(DATA_NASCITA,utente.getData());
        if(!StringUtils.isEmpty(utente.getImageUrl()))
            request.put(URL_IMAGE,utente.getImageUrl());
        if(utente.getId() > 0)
            request.put(ID,String.valueOf(utente.getId()));
        request.put(UID,utente.getUID());
        request.put(TIPO,String.valueOf(utente.getTipoUtente().ordinal()));
        request.put(ACCESSTYPE,typeLogin);
        return request;
    }

    private static final String IDUTENTE = "IdUtente";
    private static final String TOKEN = "Token";
    private static final String NOMEDEVICE = "deviceName";

    public static Map<String,String> traduce(int id_Utente,String nomeDispositivo,String token){
        Map<String,String> request = new HashMap<>();
        request.put(IDUTENTE,String.valueOf(id_Utente));
        request.put(TOKEN,token);
        request.put(NOMEDEVICE,nomeDispositivo);
        return request;
    }
    public static Map<String,String> traduce(int id_Utente){
        Map<String,String> request = new HashMap<>();
        if(id_Utente > 0)
            request.put(IDUTENTE,String.valueOf(id_Utente));
        return request;
    }

    private static final String IDVARCO = "IdVarco";
    private static final String DATADAL = "DataDal";
    private static final String DATAAL = "DataAl";
    private static final String PAGER = "Pager";


    public static Map<String,String> traduce(FilterPassaggi filterPassaggi){
        Map<String,String> request = new HashMap<>();
        if(filterPassaggi != null) {
            if (filterPassaggi.getIdUtente() > 0)
                request.put(IDUTENTE, String.valueOf(filterPassaggi.getIdUtente()));
            if (filterPassaggi.getIdVarco() > 0)
                request.put(IDVARCO, String.valueOf(filterPassaggi.getIdVarco()));
            if (!StringUtils.isEmpty(filterPassaggi.getTimeDal()))
                request.put(DATADAL, filterPassaggi.getTimeDal());
            if (!StringUtils.isEmpty(filterPassaggi.getTimeAl()))
                request.put(DATAAL, filterPassaggi.getTimeAl());
            request.put(PAGER, String.valueOf(filterPassaggi.getPager()));

        }
        return request;
    }


    public static Map<String,String> traduce(Utente utente,Varco varco){
        Map<String,String> request = new HashMap<>();
        String token = utente.getId() + ";" + varco.getId() + ";" + Calendar.getInstance().getTimeInMillis();
        String encodedString = Base64.getEncoder().encodeToString(token.getBytes());
        request.put("token",encodedString);
        return request;
    }

}
