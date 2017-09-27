package br.com.adrianob.cadastrodeproduto.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by drink on 12/09/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "dbprodutos";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PRODUTO (_id INTEGER " +
                " PRIMARY KEY AUTOINCREMENT, remoteid INTEGER, " +
                " estado TEXT, nome TEXT ); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
