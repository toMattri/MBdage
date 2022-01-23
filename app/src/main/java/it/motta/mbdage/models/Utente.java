package it.motta.mbdage.models;

import it.motta.mbdage.models.evalue.TypeUtente;

public class Utente {

    private final String displayName,email,password,data,UID;
    private final TypeUtente tipoUtente;

    public Utente(String displayName, String email, String password, String data, TypeUtente tipoUtente,String UID) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.data = data;
        this.UID = UID;
        this.tipoUtente = tipoUtente;
    }
    public Utente(String displayName, String email, String data, TypeUtente tipoUtente,String UID) {
        this(displayName,email,"",data,tipoUtente,UID);

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

    public String getPassword() {
        return password;
    }

    public String getData() {
        return data;
    }

    public TypeUtente getTipoUtente() {
        return tipoUtente;
    }
}
