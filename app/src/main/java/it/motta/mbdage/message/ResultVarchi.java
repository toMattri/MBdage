package it.motta.mbdage.message;

public enum ResultVarchi {

        SUCCESS(1),
        NOT_FIND(0);
        int value;
        ResultVarchi(int value){
                this.value = value;
        }

        public static ResultVarchi fromValue(int value){
                for(ResultVarchi res : ResultVarchi.values()){
                        if(res.value == value)
                                return res;
                }
                return NOT_FIND;

        }

}