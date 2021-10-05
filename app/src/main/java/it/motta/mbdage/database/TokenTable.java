package it.motta.mbdage.database;

import android.provider.BaseColumns;

import java.text.MessageFormat;

interface TokenTable extends BaseColumns {

    String TABLE_NAME = "tb_token";
    String CL_LAST_TOKEN = "last_token";
    String CL_TIME_STAMP = "time_stamp";

    String[] COllUMS = {
            _ID,
            CL_LAST_TOKEN,
            CL_TIME_STAMP
    };

    String TABLE = MessageFormat.format("CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} TEXT NOT NULL, " +
                    "{3} DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);",
            TokenTable.TABLE_NAME, TokenTable._ID,
            TokenTable.CL_LAST_TOKEN, TokenTable.CL_TIME_STAMP);

}