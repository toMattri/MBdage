package it.motta.mbdage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.MessageFormat;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "mbadge.sqlite";
    private static final int SCHEMA_VERSION  = 1;
    private Context mContext;

    public DBHandler(Context mContext){
        super(mContext,DB_NAME,null,SCHEMA_VERSION);
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            String sql = "CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} TEXT NOT NULL, " +
                    "{3} TEXT NOT NULL);";

            db.execSQL(MessageFormat.format(sql, DispositiviTable.TABLE_NAME,
                    DispositiviTable._ID,
                    DispositiviTable.CL_DESCRIZIONE,
                    DispositiviTable.CL_TOKEN));


            sql = "CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} INTEGER NOT NULL, " +
                    "{3} TEXT NOT NULL," +
                    "{4} TEXT NOT NULL," +
                    "{5} DATETIME NOT NULL DEFAULT TIMESTAMP);";

            db.execSQL(MessageFormat.format(sql, LogTable.TABLE_NAME,
                    LogTable._ID,
                    LogTable.CL_ID_UTENTE,
                    LogTable.CL_OPERAZIONE,
                    LogTable.CL_TOKEN_DISPOSITIVO,
                    LogTable.CL_TIME_STAMP));

            sql = "CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} INTEGER NOT NULL, " +
                    "{3} TEXT NOT NULL," +
                    "{4} TEXT NOT NULL," +
                    "{5} DATETIME NOT NULL DEFAULT TIMESTAMP,"+
                    "{6} DATETIME NOT NULL DEFAULT TIMESTAMP);";

            db.execSQL(MessageFormat.format(sql, PassagiTable.TABLE_NAME,
                    PassagiTable._ID,
                    PassagiTable.CL_ID_UTENTE,
                    PassagiTable.CL_ID_VARCO,
                    PassagiTable.CL_TOKEN_DISPOSITIVO,
                    PassagiTable.CL_DATA,
                    PassagiTable.CL_DATA_SYN));

            sql = "CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} TEXT NOT NULL, " +
                    "{3} TEXT NOT NULL," +
                    "{4} TEXT NOT NULL," +
                    "{5} TEXT NOT NULL);";

            db.execSQL(MessageFormat.format(sql, UtenteTable.TABLE_NAME,
                    UtenteTable._ID,
                    UtenteTable.CL_NOME,
                    UtenteTable.CL_COGNOME,
                    UtenteTable.CL_NASCITA,
                    UtenteTable.CL_TIPO));

            sql = "CREATE TABLE {0} " +
                    "({1} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "{2} TEXT NOT NULL, " +
                    "{3} TEXT NOT NULL," +
                    "{4} TEXT NOT NULL," +
                    "{5} TEXT NOT NULL );";

            db.execSQL(MessageFormat.format(sql, VarchiTable.TABLE_NAME,
                    VarchiTable._ID,
                    VarchiTable.CL_DESCRIZIONE,
                    VarchiTable.CL_IMAGE,
                    VarchiTable.CL_LATITUDINE,
                    VarchiTable.CL_LONGITUDINE));

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion < newVersion){

        }

    }
}
