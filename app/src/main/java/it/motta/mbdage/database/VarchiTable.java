package it.motta.mbdage.database;

import it.motta.mbdage.interfaces.TableInterface;

public interface VarchiTable extends TableInterface {

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

    @Override
    default String createTable() {
        return null;
    }
}