package br.com.adrianob.cadastrodeproduto.view;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.adrianob.cadastrodeproduto.R;
import br.com.adrianob.cadastrodeproduto.models.Produto;

/**
 * Created by drink on 12/09/2017.
 */

public class AdapterProduto extends BaseAdapter {

    private List<Produto> lst = null;
    private Activity act = null;

    public AdapterProduto(List<Produto> lst, Activity act) {
        this.lst = lst;
        this.act = act;
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return lst.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lst.get(position).getCodigo();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = act.getLayoutInflater()
                .inflate(R.layout.adapter_produto,
                        parent,false);
        TextView tvDescricao = (TextView)
                v.findViewById(R.id.tvDescricao);
        TextView tvCodigo = (TextView)
                v.findViewById(R.id.tvCodigo);
        TextView tvStatus = (TextView)
                v.findViewById(R.id.tvStatus);
        Produto p = (Produto)  getItem(position);

        tvDescricao.setText(p.getNome());
        tvCodigo.setText(String.valueOf(p.getCodigo()));

        tvStatus.setText(p.getStatus());

        if (p.getStatus().equals(p.getStringStatus(Produto.stat.NOVO))) {
            v.setBackgroundColor(Color.GREEN);
        }
        if (p.getStatus().equals(p.getStringStatus(Produto.stat.MODIFICADO))) {
            v.setBackgroundColor(Color.YELLOW);
        }
        if (p.getStatus().equals(p.getStringStatus(Produto.stat.SINCRONIZADO))) {
            v.setBackgroundColor(Color.WHITE);
        }
        if (p.getStatus().equals(p.getStringStatus(Produto.stat.EXCLUIDO))) {
            v.setBackgroundColor(Color.RED);
        }
        v.getBackground().setAlpha(128);
        return v;
    }
}
