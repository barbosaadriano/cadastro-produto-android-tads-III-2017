package br.com.adrianob.cadastrodeproduto.dao;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import br.com.adrianob.cadastrodeproduto.models.Produto;
import br.com.adrianob.cadastrodeproduto.services.DBHelper;

/**
 * Created by drink on 12/09/2017.
 */

public class DaoProduto {

    private DBHelper db;

    public DaoProduto(DBHelper db) {
        this.db = db;
    }

    public List<Produto> getAllProdutos() {
        List<Produto> lst = new ArrayList<>();
        Cursor c = this.db.getReadableDatabase()
                .rawQuery("SELECT * FROM PRODUTO", null);
        for(int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Produto p = this.getProduto(c);
            lst.add(p);
        }
        c.close();
        db.close();
        return lst;
    }
    public Produto getProdutoById(long id) {
     Cursor c = this.db.getReadableDatabase()
             .rawQuery("SELECT * FROM PRODUTO WHERE _id = "
             +String.valueOf(id),null);
        if (c.getCount()>0) {
            c.moveToFirst();
            Produto p = this.getProduto(c);
            c.close();
            return p;
        }
        return null;
    }
    public Produto getProdutoByRemoteId(long id) {
        Cursor c = this.db.getReadableDatabase()
                .rawQuery("SELECT * FROM PRODUTO WHERE remoteid = "
                        +String.valueOf(id),null);
        if (c.getCount()>0) {
            c.moveToFirst();
            Produto p = this.getProduto(c);
            c.close();
            return p;
        }
        return null;
    }

    public Produto getProduto(Cursor c) {
        Produto p = new Produto();
        p.setCodigo( c.getInt( c.getColumnIndex("_id") ) );
        p.setRemoteCodigo(c.getInt( c.getColumnIndex("remoteid") ));
        p.setNome(c.getString(c.getColumnIndex("nome")));
        p.setStatus(c.getString(c.getColumnIndex("estado")));
        return p;
    }

    public void save(Produto p) {
        ContentValues cv = new ContentValues();
        cv.put("remoteid",p.getRemoteCodigo());
        cv.put("estado",p.getStatus());
        cv.put("nome",p.getNome());
        if (p.getCodigo()==0) {
            db.getWritableDatabase()
                    .insert("PRODUTO",null,cv);
        } else {
            cv.put("_id",p.getCodigo());
            db.getWritableDatabase()
                    .update("PRODUTO",cv,
                            "_id = "+String.valueOf(p.getCodigo()),
                            null);
        }

    }
    public void remover(Produto p) {
        this.db.getWritableDatabase()
                .delete("PRODUTO","_id="+String.valueOf(p.getCodigo()),
                        null);
    }


}
