package it.motta.mbdage.database;

import android.provider.BaseColumns;

import java.text.MessageFormat;

public interface PassagiTable extends BaseColumns {

    String TABLE_NAME = "tb_passaggi";

    String CL_ID_UTENTE = "id_utente";
    String CL_ID_VARCO = "id_varco";
    String CL_DATA = "data";
    String CL_DATA_SYN = "data_sync";

    String [] COLUMNS = {
        _ID,
        CL_ID_UTENTE,
        CL_ID_VARCO,
        CL_DATA,
        CL_DATA_SYN
    };

    String TABLE = MessageFormat.format("CREATE TABLE {0} " +
            "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "{2} INTEGER NOT NULL, " +
            "{3} TEXT NOT NULL," +
            "{4} DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"+
            "{5} DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);",
        TABLE_NAME,
        _ID,
        CL_ID_UTENTE,
        CL_ID_VARCO,
        CL_DATA,
        CL_DATA_SYN);

}