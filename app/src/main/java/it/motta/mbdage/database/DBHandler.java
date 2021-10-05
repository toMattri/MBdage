package it.motta.mbdage.database;

import android.content.ContentValues;
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

            db.execSQL(DispositiviTable.TABLE);

            db.execSQL(LogTable.TABLE);

            db.execSQL(PassagiTable.TABLE);

            db.execSQL(UtenteTable.TABLE);

            db.execSQL(VarchiTable.TABLE);

            db.execSQL(TokenTable.TABLE);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public void updateToken(String token){
        getWritableDatabase().delete(TokenTable.TABLE_NAME,TokenTable._ID + " > 0",null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TokenTable.CL_LAST_TOKEN,token);
        getWritableDatabase().insertOrThrow(TokenTable.TABLE_NAME,null,contentValues);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion < newVersion){

        }

    }
}
