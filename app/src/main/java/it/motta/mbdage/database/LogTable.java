package it.motta.mbdage.database;

import android.provider.BaseColumns;

import java.text.MessageFormat;

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

    String TABLE = MessageFormat.format("CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} INTEGER NOT NULL, " +
                    "{3} TEXT NOT NULL," +
                    "{4} TEXT NOT NULL," +
                    "{5} DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);",
            TABLE_NAME,
            _ID,
            CL_ID_UTENTE,
            CL_OPERAZIONE,
            CL_TOKEN_DISPOSITIVO,
            CL_TIME_STAMP);

}
