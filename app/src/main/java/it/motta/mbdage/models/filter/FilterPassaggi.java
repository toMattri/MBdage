package it.motta.mbdage.models.filter;

import it.motta.mbdage.utils.Utils;

public class FilterPassaggi {

  public int idUtente,idVarco,pager;
  public String timeDal,timeAl;

  public FilterPassaggi(int idUtente, int idVarco, String timeDal, String timeAl) {
    this.idUtente = idUtente;
    this.idVarco = idVarco;
    this.timeDal = timeDal;
    this.timeAl = timeAl;
    this.pager = 0;
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

  public int getPager() {
    return pager;
  }

  public void resetPager(){
    pager = 0;
  }

  public void updatePager(){
    pager +=20;
  }

}