package it.motta.mbdage.interfaces;

import org.json.JSONObject;

public interface IAccessOperation {

    void OnCompleteOperation(JSONObject response);
    void OnError();


}
