package it.motta.mbdage.database;

import android.provider.BaseColumns;

import java.text.MessageFormat;

public interface UtenteTable extends BaseColumns {

    String TABLE_NAME = "tb_utente";

    String CL_NOME = "nome";
    String CL_COGNOME = "cognome";
    String CL_NASCITA = "data_nascita";
    String CL_TIPO = "tipo";


    String[] COLUMNS = {
            _ID,
            CL_NOME,
            CL_COGNOME,
            CL_NASCITA,
            CL_TIPO
    };

    String TABLE = MessageFormat.format("CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} TEXT NOT NULL, " +
                    "{3} TEXT NOT NULL," +
                    "{4} TEXT NOT NULL," +
                    "{5} TEXT NOT NULL);",
            TABLE_NAME,
            _ID,
            CL_NOME,
            CL_COGNOME,
            CL_NASCITA,
            CL_TIPO);

}