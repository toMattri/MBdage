package it.motta.mbdage.models;

import java.io.Serializable;

import it.motta.mbdage.models.evalue.TypeUtente;

public class Utente implements Serializable {


    private final int id;
    private final String displayName,email,UID;
    private String data,imageUrl;
    private final TypeUtente tipoUtente;

    public Utente(int id,String displayName, String email,String data, TypeUtente tipoUtente,String UID,String imageUrl) {
        this.displayName = displayName;
        this.id = id;
        this.email = email;
        this.data = data;
        this.UID = UID;
        this.tipoUtente = tipoUtente;
        this.imageUrl = imageUrl;
    }
    public Utente(String displayName, String email, String data, TypeUtente tipoUtente,String UID,String imageUrl) {
        this(0,displayName,email,data,tipoUtente,UID,imageUrl);
    }

    public String getUID() {
        return UID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public TypeUtente getTipoUtente() {
        return tipoUtente;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}