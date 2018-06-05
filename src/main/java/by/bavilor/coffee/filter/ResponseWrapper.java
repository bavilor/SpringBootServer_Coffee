package by.bavilor.coffee.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.security.PublicKey;

/**
 * Created by bosak on 6/1/2018.
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private PrintWriter writer;
    private CustomOutputStream cos;


    public ResponseWrapper(HttpServletResponse response){
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if(cos == null){
            cos = new CustomOutputStream();
        }
        return cos;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (cos != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            cos = new CustomOutputStream();
            writer = new PrintWriter(getResponse().getOutputStream(), true);
        }
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        System.out.println("flush buffer");
        super.flushBuffer();
    }

    public byte[] getCopy() {
        if (cos != null) {
            return cos.getByteArray();
        } else {
            return new byte[0];
        }
    }
}
