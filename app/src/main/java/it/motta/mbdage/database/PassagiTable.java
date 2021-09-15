package it.motta.mbdage.database;

import android.provider.BaseColumns;

public interface PassagiTable extends BaseColumns {

    String TABLE_NAME = "tb_passaggi";

    String CL_TOKEN_DISPOSITIVO = "token_dispositivo";
    String CL_ID_UTENTE = "id_utente";
    String CL_ID_VARCO = "id_varco";
    String CL_DATA = "data";
    String CL_DATA_SYN = "data_sync";

    String [] COLUMNS = {
            _ID,
            CL_ID_UTENTE,
            CL_ID_VARCO,
            CL_TOKEN_DISPOSITIVO,
            CL_DATA,
            CL_DATA_SYN
    };

}