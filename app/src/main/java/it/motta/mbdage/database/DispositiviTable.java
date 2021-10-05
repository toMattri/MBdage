package it.motta.mbdage.database;

import android.provider.BaseColumns;

import java.text.MessageFormat;

public interface DispositiviTable extends BaseColumns {

    String TABLE_NAME = "tb_dispositivi";

    String CL_TOKEN = "token";
    String CL_DESCRIZIONE = "descrizione";

    String[] COLUMNS = {
            _ID,
            CL_TOKEN,
            CL_DESCRIZIONE
    };

    String TABLE = MessageFormat.format("CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} TEXT NOT NULL, " +
                    "{3} TEXT NOT NULL);",
            TABLE_NAME,
            _ID,
            CL_DESCRIZIONE,
            CL_TOKEN);

}