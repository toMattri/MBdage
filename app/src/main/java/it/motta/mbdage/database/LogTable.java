package it.motta.mbdage.database;

import android.provider.BaseColumns;

public interface LogTable extends BaseColumns {

    String TABLE_NAME = "tb_log";

    String CL_OPERAZIONE = "operazione";
    String CL_TIME_STAMP = "time_stamp";
    String CL_ID_UTENTE = "id_utente";
    String CL_TOKEN_DISPOSITIVO = "token_dispositovo";


    String [] COLUMNS = {
            _ID,
            CL_ID_UTENTE,
            CL_OPERAZIONE,
            CL_TIME_STAMP,
            CL_TOKEN_DISPOSITIVO

    };

}
