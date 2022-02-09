package it.motta.mbdage.response;

public enum ResponseCreaVarco {

        SUCCESS(1),
        ERROR(-1),
        ERR_PARAM(-4);

        int value;
        ResponseCreaVarco(int value){
                this.value = value;
        }

        public static ResponseCreaVarco fromValue(int value){
                for(ResponseCreaVarco res : ResponseCreaVarco.values()){
                        if(res.value == value)
                                return res;
                }
                return ERROR;

        }

}