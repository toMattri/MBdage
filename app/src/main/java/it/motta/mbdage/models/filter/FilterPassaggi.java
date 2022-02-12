package it.motta.mbdage.models.filter;

import it.motta.mbdage.utils.Utils;

public class FilterPassaggi {

  private final int idUtente,idVarco;
  private final String timeDal,timeAl;

  public FilterPassaggi(int idUtente, int idVarco, String timeDal, String timeAl) {
    this.idUtente = idUtente;
    this.idVarco = idVarco;
    this.timeDal = timeDal;
    this.timeAl = timeAl;
  }

  public FilterPassaggi(int idUtente){
    this(idUtente,0, Utils.dateQuery(Utils.getDateLessDay(30)), Utils.dateQuery(Utils.getDateLessDay(0)));
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