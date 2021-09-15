package it.motta.mbdage.database;

import android.provider.BaseColumns;

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

}