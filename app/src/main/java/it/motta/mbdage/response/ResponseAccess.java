package it.motta.mbdage.response;

public enum ResponseAccess {

        SUCCESS(1),
        NOT_CREATED(-1),
        ALREADY_EXIST(-2),
        NOT_FIND(-3),
        ERR_PARAM(-4),
        ERROR_ON_UPGRADE(-5);

        int value;
        ResponseAccess(int value){
                this.value = value;
        }


        public static ResponseAccess fromValue(int value){
                for(ResponseAccess res : ResponseAccess.values()){
                        if(res.value == value)
                                return res;
                }
                return NOT_FIND;

        }

}
