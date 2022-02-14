package it.motta.mbdage.models;

public class ItemPassaggi {

    private final int idPassaggio;
    private final long idUtente;
    private final String data;
    private final Varco varco;

    public ItemPassaggi(int idPassaggio,long idUtente, String data, Varco varco) {
        this.idUtente = idUtente;
        this.idPassaggio = idPassaggio;
        this.data = data;
        this.varco = varco;
    }

    public long getIdUtente() {
        return idUtente;
    }

    public String getData() {
        return data;
    }

    public int getIdPassaggio() {
        return idPassaggio;
    }

    public Varco getVarco() {
        return varco;
    }
}
