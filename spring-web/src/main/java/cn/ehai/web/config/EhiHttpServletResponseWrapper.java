package cn.ehai.web.config;




import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import org.apache.commons.io.output.TeeOutputStream;

/**
 * @Description: TODO
 * @author:闵豪
 * @time:18-8-8 下午5:19
 */
public class EhiHttpServletResponseWrapper extends HttpServletResponseWrapper {

    /**
     * 我们的分支流
     */
    private ByteArrayOutputStream output;

    private WrapperOutputStream wrapperOutputStream;

    public EhiHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new ByteArrayOutputStream();
        wrapperOutputStream = new WrapperOutputStream(output);
    }

    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        return wrapperOutputStream;
    }

    @Override
    public void flushBuffer()
        throws IOException
    {
        if (wrapperOutputStream != null)
        {
            wrapperOutputStream.flush();
        }
    }

    public byte[] getContent()
        throws IOException
    {
        flushBuffer();
        return output.toByteArray();
    }


    class WrapperOutputStream extends ServletOutputStream
    {
        private ByteArrayOutputStream bos;

        public WrapperOutputStream(ByteArrayOutputStream bos)
        {
            this.bos = bos;
        }

        @Override
        public void write(int b)
            throws IOException
        {
            bos.write(b);
        }

        @Override
        public boolean isReady()
        {

            // TODO Auto-generated method stub
            return false;

        }

        @Override
        public void setWriteListener(WriteListener arg0)
        {

            // TODO Auto-generated method stub

        }
    }

}
