package it.motta.mbdage.database;

import android.provider.BaseColumns;

import java.text.MessageFormat;

public interface VarchiTable extends BaseColumns {

    String TABLE_NAME = "tb_varchi";

    String CL_LONGITUDINE = "longitudine";
    String CL_LATITUDINE = "latitudine";
    String CL_DESCRIZIONE = "descrizione";
    String CL_IMAGE = "img_prev";

    String[] COLUMNS = {
        _ID,
        CL_DESCRIZIONE,
        CL_LONGITUDINE,
        CL_LATITUDINE,
        CL_IMAGE
    };

    String TABLE = MessageFormat.format("CREATE TABLE {0} " +
            "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "{2} TEXT NOT NULL, " +
            "{3} TEXT NOT NULL," +
            "{4} DOUBLE NOT NULL," +
            "{5} DOUBLE NOT NULL);",
        TABLE_NAME,
        _ID,
        CL_DESCRIZIONE,
        CL_IMAGE,
        CL_LATITUDINE,
        CL_LONGITUDINE);
}