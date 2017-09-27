package br.com.adrianob.cadastrodeproduto.view;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import br.com.adrianob.cadastrodeproduto.R;
import br.com.adrianob.cadastrodeproduto.dao.DaoProduto;
import br.com.adrianob.cadastrodeproduto.models.Produto;
import br.com.adrianob.cadastrodeproduto.services.DBHelper;

public class Formulario extends AppCompatActivity implements View.OnClickListener {

    private Produto p;
    private ImageButton btnSalvar;
    private ImageButton btnCancelar;
    private EditText edDescricao;
    private DaoProduto dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        btnSalvar = (ImageButton) findViewById(R.id.btnSalvar);
        btnCancelar = (ImageButton) findViewById(R.id.btnCancelar);
        edDescricao = (EditText) findViewById(R.id.txDescricao);
        btnCancelar.setOnClickListener(this);
        btnSalvar.setOnClickListener(this);

        Intent i = getIntent();
        long id = i.getLongExtra("_id",0);
        dp = new DaoProduto(new DBHelper(getBaseContext()));
        p = dp.getProdutoById(id);
        if (p==null) {
            p = new Produto();
        }
        if (p.getStatus().equals(p.getStringStatus(Produto.stat.EXCLUIDO))) {
            finish();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        edDescricao.setText(p.getNome());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelar: {
                this.finish();
                break;
            }
            case R.id.btnSalvar:{
                if (
                        edDescricao.getText().toString().isEmpty()
                        ||
                                edDescricao.getText().toString().trim().equals("")
                        ){
                    AlertDialog.Builder msg = new AlertDialog.Builder(this);
                    msg.setMessage("A descrição do produto é obrigatória!");
                    msg.setPositiveButton("OK",null);
                    msg.show();
                    return;
                }
                p.setNome(edDescricao.getText().toString());

                if (p.getStatus().equals(p.getStringStatus(Produto.stat.SINCRONIZADO))) {
                    p.setStatus(p.getStringStatus(Produto.stat.MODIFICADO));
                }
                dp.save(p);
                this.finish();
                break;
            }
            default: break;
        }
    }
}
