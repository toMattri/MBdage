package it.motta.mbdage.models.response;

public enum ResponseOpenVarco {

        SUCCESS(1),
        ERR_APERTURA(-1),
        TOKEN_EXPIRE(-2),
        ERR_PARAM(-4);

        int value;
        ResponseOpenVarco(int value){
                this.value = value;
        }

        public static ResponseOpenVarco fromValue(int value){
                for(ResponseOpenVarco res : ResponseOpenVarco.values()){
                        if(res.value == value)
                                return res;
                }
                return ERR_APERTURA;

        }

}
