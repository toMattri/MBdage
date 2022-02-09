package it.motta.mbdage.response;

public enum ResponsePassaggi {

        SUCCESS(1),
        NOT_FIND(0);
        int value;
        ResponsePassaggi(int value){
                this.value = value;
        }

        public static ResponsePassaggi fromValue(int value){
                for(ResponsePassaggi res : ResponsePassaggi.values()){
                        if(res.value == value)
                                return res;
                }
                return NOT_FIND;

        }

}