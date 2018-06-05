package by.bavilor.coffee.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by bosak on 6/5/2018.
 */
public class CustomInputStream extends ServletInputStream {

    private ByteArrayInputStream bais;
    private int numberOfCalls = -1;
    private byte[] decrDataBytes;

    public CustomInputStream(byte[] decrDataBytes) {
        this.bais = new ByteArrayInputStream(decrDataBytes);
        this.decrDataBytes = decrDataBytes;
    }


    @Override
    public int read() throws IOException {
        numberOfCalls++;
        if(numberOfCalls < decrDataBytes.length){
            return decrDataBytes[numberOfCalls];
        }else{
            return -1;
        }
    }

    @Override
    public boolean isFinished() {
        if(numberOfCalls < decrDataBytes.length){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
}
