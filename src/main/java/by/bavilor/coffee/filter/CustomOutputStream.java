package by.bavilor.coffee.filter;

import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by bosak on 6/4/2018.
 */
public class CustomOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream baos;

    public CustomOutputStream() {
        this.baos = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        baos.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        baos.write(b);
    }

    public byte[] getByteArray() {
        return baos.toByteArray();
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    @Override
    public void flush() throws IOException {
        super.flush();
    }
}
