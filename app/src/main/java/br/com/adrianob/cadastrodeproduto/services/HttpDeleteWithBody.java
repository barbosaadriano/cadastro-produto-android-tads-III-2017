package br.com.adrianob.cadastrodeproduto.services;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * Created by drink on 12/09/2017.
 */

public class HttpDeleteWithBody  extends HttpEntityEnclosingRequestBase {

    public static final String METHOD_NAME = "DELETE";


    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWithBody(){
        super();
    }

}
