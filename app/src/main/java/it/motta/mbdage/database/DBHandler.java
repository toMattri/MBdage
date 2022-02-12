package it.motta.mbdage.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import it.motta.mbdage.models.ItemPassaggi;
import it.motta.mbdage.models.Passaggio;
import it.motta.mbdage.models.Utente;
import it.motta.mbdage.models.Varco;
import it.motta.mbdage.models.evalue.TypeUtente;

@SuppressLint({"StaticFieldLeak","Range"})
public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "mbadge.sqlite";
    private static final int SCHEMA_VERSION  = 1;
    private final Context mContext;
    private static DBHandler dbHandlerIstance;

    public static DBHandler getIstance(Context mContext){
        if(dbHandlerIstance == null)
            dbHandlerIstance = new DBHandler(mContext);
        return dbHandlerIstance;
    }

    private DBHandler(Context mContext){
        super(mContext,DB_NAME,null,SCHEMA_VERSION);
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(PassagiTable.TABLE);
            db.execSQL(UtenteTable.TABLE);
            db.execSQL(VarchiTable.TABLE);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Utente getUtente(){
        try(Cursor c = getReadableDatabase().rawQuery("Select * FROM " + UtenteTable.TABLE_NAME +" LIMIT 1",null)) {
            if(c.moveToFirst()){
                return new Utente(c.getInt(c.getColumnIndex(UtenteTable._ID)),c.getString(c.getColumnIndex(UtenteTable.CL_DISPLAY_NAME)),c.getString(c.getColumnIndex(UtenteTable.CL_EMAIL)),
                    c.getString(c.getColumnIndex(UtenteTable.CL_NASCITA)), TypeUtente.values()[c.getInt(c.getColumnIndex(UtenteTable.CL_TIPO))],"", c.getString(c.getColumnIndex(UtenteTable.CL_URL_IMAGE)));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void logginUser(Utente utente){
        deleteUtente();
        ContentValues cv = new ContentValues();
        cv.put(UtenteTable._ID,utente.getId());
        cv.put(UtenteTable.CL_DISPLAY_NAME,utente.getDisplayName());
        cv.put(UtenteTable.CL_EMAIL,utente.getEmail());
        cv.put(UtenteTable.CL_NASCITA,utente.getData());
        cv.put(UtenteTable.CL_TIPO,utente.getTipoUtente().ordinal());
        cv.put(UtenteTable.CL_URL_IMAGE,utente.getImageUrl());
        getWritableDatabase().insertOrThrow(UtenteTable.TABLE_NAME,null,cv);
    }

    public void deleteUtente(){
        getWritableDatabase().delete(UtenteTable.TABLE_NAME,UtenteTable._ID + " > ?",new String[]{"0"});
    }

    public void writeVarchi(ArrayList<Varco> varchi){
        getWritableDatabase().delete(VarchiTable.TABLE_NAME,VarchiTable._ID + " > ?",new String[]{"0"});
        ContentValues cv;
        for(Varco vc : varchi) {
            cv = new ContentValues();
            cv.put(VarchiTable._ID, vc.getId());
            cv.put(VarchiTable.CL_DESCRIZIONE, vc.getDescrizione());
            cv.put(VarchiTable.CL_IMAGE, vc.getImg());
            cv.put(VarchiTable.CL_LATITUDINE, vc.getLatitudine());
            cv.put(VarchiTable.CL_LONGITUDINE, vc.getLongitudine());
            getWritableDatabase().insertOrThrow(VarchiTable.TABLE_NAME, null, cv);
        }
    }

    public ArrayList<Varco> getVarchi(){
        ArrayList<Varco> varcos = new ArrayList<>();
        try(Cursor c = getReadableDatabase().rawQuery("Select * FROM " + VarchiTable.TABLE_NAME ,null)) {
            while(c.moveToNext()){
                varcos.add(new Varco(c.getLong(c.getColumnIndex(VarchiTable._ID)),c.getDouble(c.getColumnIndex(VarchiTable.CL_LATITUDINE)),
                    c.getDouble(c.getColumnIndex(VarchiTable.CL_LONGITUDINE)),c.getString(c.getColumnIndex(VarchiTable.CL_DESCRIZIONE)),
                    c.getString(c.getColumnIndex(VarchiTable.CL_IMAGE))));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return varcos;

    }

    public void completeUtente(Utente utente){
        ContentValues cv = new ContentValues();
        cv.put(UtenteTable.CL_TIPO,TypeUtente.COMPLETED.ordinal());
        getWritableDatabase().update(UtenteTable.TABLE_NAME,cv,UtenteTable._ID +" = ?",new String[]{String.valueOf(utente.getId())});
        utente.setTipoUtente(TypeUtente.COMPLETED);
    }

    public void writePassaggi(ArrayList<Passaggio> passaggi, boolean reload){
        if(reload){
            getWritableDatabase().delete(PassagiTable.TABLE_NAME,PassagiTable._ID + " > 0",null);
        } else {
            ArrayList<Integer> mapInt = (ArrayList<Integer>) passaggi.stream().mapToInt(Passaggio::getId).boxed().collect(Collectors.toList());
            getWritableDatabase().delete(PassagiTable.TABLE_NAME, PassagiTable._ID + " IN (?)", new String[]{mapInt.toString().substring(1, mapInt.toString().length() - 1)});
        }
        ContentValues cv;
        for(Passaggio pas : passaggi) {
            cv = new ContentValues();
            cv.put(PassagiTable.CL_ID_UTENTE, pas.getIdUtente());
            cv.put(PassagiTable._ID, pas.getId());
            cv.put(PassagiTable.CL_ID_VARCO, pas.getIdVarco());
            cv.put(PassagiTable.CL_DATA, pas.getData());
            getWritableDatabase().insertWithOnConflict(PassagiTable.TABLE_NAME, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public ArrayList<ItemPassaggi> getItemPassaggi(int idUtente){
        ArrayList<ItemPassaggi> passaggios = new ArrayList<>();
        String sql = "SELECT " +
            PassagiTable.TABLE_NAME + "." + PassagiTable._ID +"," +
            PassagiTable.TABLE_NAME + "." + PassagiTable.CL_ID_UTENTE +"," +
            PassagiTable.TABLE_NAME + "." + PassagiTable.CL_ID_VARCO +"," +
            PassagiTable.TABLE_NAME + "." + PassagiTable.CL_DATA  +"," +
            VarchiTable.TABLE_NAME + "." + VarchiTable.CL_DESCRIZIONE  +"," +
            VarchiTable.TABLE_NAME + "." + VarchiTable.CL_LATITUDINE  +"," +
            VarchiTable.TABLE_NAME + "." + VarchiTable.CL_LONGITUDINE  +"," +
            VarchiTable.TABLE_NAME + "." + VarchiTable.CL_IMAGE  +
            " FROM " + PassagiTable.TABLE_NAME +" INNER JOIN " + VarchiTable.TABLE_NAME + " ON " +
            PassagiTable.TABLE_NAME + "." + PassagiTable.CL_ID_VARCO +" = " + VarchiTable.TABLE_NAME + "." + VarchiTable._ID +

            " Where " + PassagiTable.TABLE_NAME + "." + PassagiTable.CL_ID_UTENTE + " = " + idUtente + " ORDER BY "+
            PassagiTable.TABLE_NAME + "." + PassagiTable.CL_DATA + " DESC";

        try(Cursor c = getReadableDatabase().rawQuery(sql, null)) {
            while(c.moveToNext()){
                passaggios.add(new ItemPassaggi(c.getInt(0),c.getInt(1),c.getString(3),new Varco(c.getInt(2),c.getDouble(5),c.getDouble(6), c.getString(4),c.getString(7))));

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return passaggios;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion) {

        }
    }

}