package it.motta.mbdage.models;

public class ItemPassaggi {

    private long idUtente;
    private String data;
    private Varco varco;

    public ItemPassaggi(long idUtente, String data, Varco varco) {
        this.idUtente = idUtente;
        this.data = data;
        this.varco = varco;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public String getData() {
        return data;
    }

    public Varco getVarco() {
        return varco;
    }
}
