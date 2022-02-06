package it.motta.mbdage.models;

public class Passaggio {

  public int idUtente,idVarco;
  public String data;

  public Passaggio(int idUtente, int idVarco, String data) {
    this.idUtente = idUtente;
    this.idVarco = idVarco;
    this.data = data;
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
}
