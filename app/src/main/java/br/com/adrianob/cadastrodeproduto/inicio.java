package br.com.adrianob.cadastrodeproduto;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.adrianob.cadastrodeproduto.dao.DaoProduto;
import br.com.adrianob.cadastrodeproduto.models.Produto;
import br.com.adrianob.cadastrodeproduto.services.DBHelper;
import br.com.adrianob.cadastrodeproduto.services.HttpDeleteWithBody;
import br.com.adrianob.cadastrodeproduto.view.AdapterProduto;
import br.com.adrianob.cadastrodeproduto.view.Formulario;

public class inicio extends AppCompatActivity
implements View.OnClickListener,
        AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener
{

    private ListView lv;
    private ImageButton btnSinc;
    private ImageButton btnNovo;
    private DaoProduto dp;
    private ProgressDialog pg;
    private static final String SERVICE_URL =
            "http://service.adrianob.com.br/produto/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        this.lv = (ListView) findViewById(R.id.lvDados);
        this.btnNovo = (ImageButton) findViewById(R.id.btnNovo);
        this.btnSinc = (ImageButton) findViewById(R.id.btnSync);
        btnNovo.setOnClickListener(this);
        btnSinc.setOnClickListener(this);

        lv.setLongClickable(true);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        dp = new DaoProduto(new DBHelper(getBaseContext()));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        List<Produto> allPrd = dp.getAllProdutos();
        AdapterProduto adp = new AdapterProduto(allPrd,this);
        lv.setAdapter(adp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNovo : {
                Intent i = new Intent(this, Formulario.class);
                i.putExtra("_id",0);
                startActivity(i);
                break;
            }
            case R.id.btnSync : {
                AlertDialog.Builder msg
                        = new AlertDialog.Builder(this);
                msg.setMessage("Deseja sincronizar os produtos?");
                msg.setPositiveButton("SIM",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Sincronizar().execute();
                            }
                        });
                msg.setNegativeButton("NÃO",null);
                msg.show();
                break;
            }
            default:break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this,Formulario.class);
        i.putExtra("_id",id);
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
        AlertDialog.Builder msg = new AlertDialog.Builder(this);
        msg.setMessage("Deseja realmente remover este item?");
        msg.setPositiveButton("Sim",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Produto prd = dp.getProdutoById(id);
                            if (prd!=null) {
                               // dp.remover(prd);
                                prd.setStatus(prd.getStringStatus(Produto.stat.EXCLUIDO));
                                dp.save(prd);
                                onPostResume();
                            }
                        }
                    }
                );
        msg.setNegativeButton("Não",null);
        msg.show();
        return true;
    }

    class Sincronizar extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = ProgressDialog.show(
                    inicio.this,
                    "Sincronizando...",
                    "Aguarde, isso pode demorar!",
                    true,false,null
            );
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pg.dismiss();
            onPostResume();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Produto> prds = dp.getAllProdutos();
            Iterator<Produto> it = prds.iterator();
            HttpClient hc = new DefaultHttpClient();
            while (it.hasNext()) {
                Produto p = it.next();
                if (p.getStatus().equals(p.getStringStatus(Produto.stat.EXCLUIDO))) {
                    if (p.getRemoteCodigo()>0) {
                        HttpDeleteWithBody del =
                                new HttpDeleteWithBody(SERVICE_URL);
                         List<NameValuePair> parDel =
                                 new ArrayList<>();
                        parDel.add(new BasicNameValuePair("id",String.valueOf(p.getRemoteCodigo())));
                        try {
                            del.setEntity(new UrlEncodedFormEntity(parDel));
                            HttpResponse response = hc.execute(del);
                            HttpEntity edel = response.getEntity();
                            String s  = EntityUtils.toString(edel);
                            System.out.println(s);
                        } catch (UnsupportedEncodingException e) {
                            System.out.println(e.getMessage());
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    dp.remover(p);
                } else if (p.getStatus().equals(p.getStringStatus(Produto.stat.NOVO))) {
                    HttpPost hpost = new HttpPost(SERVICE_URL);
                    List<NameValuePair> parPost = new ArrayList<>();
                    parPost.add(new BasicNameValuePair("nome",p.getNome()));

                    try {
                        hpost.setEntity(new UrlEncodedFormEntity(parPost));
                        HttpResponse resp = hc.execute(hpost);
                        HttpEntity ent = resp.getEntity();
                        String s = EntityUtils.toString(ent);
                        JSONObject reader = new JSONObject(s);
                        if (reader.getString("status").equals("success")) {
                            JSONObject data = reader.getJSONObject("data");
                            long idp = data.getLong("id");
                            p.setRemoteCodigo(idp);
                            p.setStatus(p.getStringStatus(Produto.stat.SINCRONIZADO));
                            dp.save(p);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (p.getStatus().equals(p.getStringStatus(Produto.stat.MODIFICADO))) {

                    HttpPut hput = new HttpPut(SERVICE_URL);
                    List<NameValuePair> parPut = new ArrayList<>();
                    parPut.add(new BasicNameValuePair("nome",p.getNome()));
                    parPut.add(new BasicNameValuePair("id",String.valueOf(p.getRemoteCodigo())));

                    try {
                        hput.setEntity(new UrlEncodedFormEntity(parPut));
                        HttpResponse resp = hc.execute(hput);
                        HttpEntity ent = resp.getEntity();
                        String s = EntityUtils.toString(ent);
                        JSONObject reader = new JSONObject(s);
                        if (reader.getString("status").equals("success")) {
                            p.setStatus(p.getStringStatus(Produto.stat.SINCRONIZADO));
                            dp.save(p);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            HttpGet getAll = new HttpGet(SERVICE_URL);
            try {
                HttpResponse responseAll = hc.execute(getAll);
                HttpEntity resEntity = responseAll.getEntity();
                String allS = EntityUtils.toString(resEntity);
                JSONObject resJs = new JSONObject(allS);
                if (resJs.getString("status").equals("success")) {
                    JSONArray data = resJs.getJSONArray("data");
                    if (data.length()>0) {
                        for (int i = 0; i <data.length(); i++) {
                            JSONObject ln = data.getJSONObject(i);
                            long pId = ln.getLong("ModelProdutoid");
                            String pNm = ln.getString("ModelProdutonome");
                            Produto prod = dp.getProdutoByRemoteId(pId);
                            if (prod == null) {
                                prod = new Produto();
                            }
                            prod.setRemoteCodigo(pId);
                            prod.setNome(pNm);
                            prod.setStatus(prod.getStringStatus(Produto.stat.SINCRONIZADO));
                            dp.save(prod);
                        }
                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            prds = dp.getAllProdutos();
            it = prds.iterator();
            while (it.hasNext()) {
                Produto p = it.next();
                HttpGet getOne = new HttpGet(
                        SERVICE_URL +
                                "?id="+
                                String.valueOf(p.getRemoteCodigo())
                );
                try {
                    HttpResponse respOne = hc.execute(getOne);
                    HttpEntity resEntity = respOne.getEntity();
                    String one = EntityUtils.toString(resEntity);
                    JSONObject resJs = new JSONObject(one);
                    if (resJs.getString("status").equals("success")) {
                        JSONArray data = resJs.getJSONArray("data");
                        if (data.length()==0) {
                            dp.remover(p);
                        }
                    }

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}
