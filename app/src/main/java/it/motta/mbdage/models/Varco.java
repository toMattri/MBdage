package it.motta.mbdage.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Varco {

    private long id;
    private final double latitudine,longitudine;
    private String descrizione,img;

    public Varco(JSONObject jsonObject) throws JSONException {
       this(jsonObject.getLong("idVarco"),jsonObject.getDouble("lat"),jsonObject.getDouble("long"),"","");
    }


    public Varco(long id, double latitudine, double longitudine, String descrizione, String img) {
        this.id = id;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.descrizione = descrizione;
        this.img = img;
    }

    public long getId() {
        return id;
    }


    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJsonObj(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idVarco", getId());
            jsonObject.put("long", getLongitudine());
            jsonObject.put("lat", getLatitudine());
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return jsonObject.toString();
    }

}
