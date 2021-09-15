package it.motta.mbdage.database;

import android.provider.BaseColumns;

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

}