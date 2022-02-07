package it.motta.mbdage.models;

public class Passaggio {

  public int id,idUtente,idVarco;
  public String data;

  public Passaggio(int id,int idUtente, int idVarco, String data) {
    this.idUtente = idUtente;
    this.idVarco = idVarco;
    this.data = data;
    this.id = id;
  }

  public int getIdUtente() {
    return idUtente;
  }

  public int getIdVarco() {
    return idVarco;
  }

  public String getData() {
    return data;
  }

  public int getId() {
    return id;
  }
}
