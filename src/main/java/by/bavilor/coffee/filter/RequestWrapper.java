package by.bavilor.coffee.filter;

import jdk.internal.util.xml.impl.Input;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Created by bosak on 6/1/2018.
 */
public class RequestWrapper extends HttpServletRequestWrapper{

    private final String nameKeyHeader = "key";
    private CustomInputStream cis;
    private BufferedReader reader;
    private byte[] decrDataBytes;

    public RequestWrapper(HttpServletRequest request, byte[] decrDataBytes){
        super(request);
        this.decrDataBytes = decrDataBytes;
    }

    @Override
    public String getHeader(String name) {
        if(name.equals(nameKeyHeader)){
            return null;
        }else{
            return super.getHeader(name);
        }
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if(name.equals(nameKeyHeader)){
            return null;
        }else{
            return super.getHeaders(name);
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> headersName = Collections.list(super.getHeaderNames());
        for(int i = 0; i<headersName.size(); i++){
            if(headersName.get(i).equals(nameKeyHeader)){
                headersName.remove(i);
            }
        }
        return (Enumeration<String>) new Vector(headersName).elements();
    }

    @Override
    public int getIntHeader(String name) {
        if(name.equals(nameKeyHeader)){
            return -1;
        }else{
            return super.getIntHeader(name);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if(cis == null){
            cis = new CustomInputStream(decrDataBytes);
        }
        return cis;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (cis != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (reader == null) {
            cis = new CustomInputStream(decrDataBytes);
            reader = new BufferedReader(super.getReader());
        }
        return reader;
    }
}
