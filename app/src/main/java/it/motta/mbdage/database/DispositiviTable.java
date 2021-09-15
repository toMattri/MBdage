package it.motta.mbdage.database;

import android.provider.BaseColumns;

public interface DispositiviTable extends BaseColumns {

    String TABLE_NAME = "tb_dispositivi";

    String CL_TOKEN = "token";
    String CL_DESCRIZIONE = "descrizione";

    String[] COLUMNS = {
            _ID,
            CL_TOKEN,
            CL_DESCRIZIONE
    };

}