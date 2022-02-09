package it.motta.mbdage.interfaces;

public interface IOpenVarco {

  void OnSuccess(boolean completeUtente);
  void ErrorParam();
  void ErrorApertura();
  void ErroGeneric();
  void ErrorConnection();
  void ErrToken();


}
