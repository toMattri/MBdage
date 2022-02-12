package it.motta.mbdage.interfaces;

import it.motta.mbdage.models.Varco;

public interface ICreateVarco {

  void OnSuccess(Varco varco);
  void ErrorParam();
  void AlreadyCreated();
  void ErroGeneric();
  void ErrorConnection();
  void ErroOnLoadImageg();

}
