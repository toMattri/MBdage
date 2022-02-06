package it.motta.mbdage.models;

public class Varco {

    private long id;
    private double latitudine,longitudine;
    private String descrizione,img;

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
}
