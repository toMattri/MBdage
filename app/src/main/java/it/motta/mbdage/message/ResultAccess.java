package it.motta.mbdage.message;

public enum ResultAccess {

        SUCCESS(1),
        NOT_CREATED(-1),
        ALREADY_EXIST(-2),
        NOT_FIND(-3),
        ERR_PARAM(-4),
        ERROR_ON_UPGRADE(-5);

        int value;
        ResultAccess(int value){
                this.value = value;
        }


        public static ResultAccess fromValue(int value){
                for(ResultAccess res : ResultAccess.values()){
                        if(res.value == value)
                                return res;
                }
                return NOT_FIND;

        }

}
