package it.motta.mbdage.models.filter;

import it.motta.mbdage.utils.Utils;

public class FilterPassaggi {

  public int idUtente,idVarco;
  public String timeDal,timeAl;

  public FilterPassaggi(int idUtente, int idVarco, String timeDal, String timeAl) {
    this.idUtente = idUtente;
    this.idVarco = idVarco;
    this.timeDal = timeDal;
    this.timeAl = timeAl;
  }

  public FilterPassaggi(int idUtente){
    this(idUtente,0, Utils.getDateLessDay(30).toString(),Utils.getDateLessDay(0).toString());
  }

  public int getIdUtente() {
    return idUtente;
  }

  public int getIdVarco() {
    return idVarco;
  }

  public String getTimeDal() {
    return timeDal;
  }

  public String getTimeAl() {
    return timeAl;
  }
}