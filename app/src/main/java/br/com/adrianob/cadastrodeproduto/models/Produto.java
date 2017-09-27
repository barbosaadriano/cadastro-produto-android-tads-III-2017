package br.com.adrianob.cadastrodeproduto.models;

/**
 * Created by drink on 05/09/2017.
 */

public class Produto {

    public enum stat {
        NOVO,MODIFICADO, SINCRONIZADO, EXCLUIDO
    }

    public String getStringStatus(Enum stat) {
        switch ((stat)stat) {
            case NOVO: {
                return "Novo";
            }
            case MODIFICADO: {
                return "Modificado";
            }
            case EXCLUIDO: {
                return "Excluido";
            }
            case SINCRONIZADO: {
                return "Sincronizado";
            }
            default: return null;
        }
    }

    private long codigo;
    private long remoteCodigo;
    private String status;
    private String nome;

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public long getRemoteCodigo() {
        return remoteCodigo;
    }

    public void setRemoteCodigo(long remoteCodigo) {
        this.remoteCodigo = remoteCodigo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Produto() {
        this.setStatus(this.getStringStatus(stat.NOVO));
        this.remoteCodigo = 0;
    }
}
