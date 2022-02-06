package it.motta.mbdage.database;

import android.provider.BaseColumns;

import java.text.MessageFormat;

public interface UtenteTable extends BaseColumns {

    String TABLE_NAME = "tb_utente";

    String CL_DISPLAY_NAME = "display_name";

    String CL_NASCITA = "data_nascita";
    String CL_TIPO = "tipo";
    String CL_EMAIL = "email";
    String CL_URL_IMAGE = "image";

    String[] COLUMNS = {
            _ID,
            CL_DISPLAY_NAME,
            CL_NASCITA,
            CL_TIPO,
            CL_EMAIL,
            CL_URL_IMAGE
    };

    String TABLE = MessageFormat.format("CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} TEXT NOT NULL, " +
                    "{3} TEXT ," +
                    "{4} TEXT ," +
                    "{5} INTEGER NOT NULL," +
                    "{6} TEXT NOT NULL);",
            TABLE_NAME,
            _ID,
            CL_DISPLAY_NAME,
            CL_NASCITA,
            CL_URL_IMAGE,
            CL_TIPO,
            CL_EMAIL);

}