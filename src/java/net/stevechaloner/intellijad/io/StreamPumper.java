package net.stevechaloner.intellijad.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Pump streams so that there is no blocking.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @version @version@,  $Id: StreamPumper.java,v 1.2 2003/11/13 21:45:44 hendriks73 Exp $
 */
public class StreamPumper extends Thread {

    private boolean endOfStream = false;
    private OutputStream out;
    private InputStream in;
    private byte[] buf;

    public StreamPumper(InputStream in) {
        this(in, null);
    }

    public StreamPumper(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        buf = new byte[512];
    }

    public void pump() throws IOException {
        if (!endOfStream) {
            int bytesRead = in.read(buf, 0, 512);

            if (bytesRead > 0 && out != null) {
                out.write(buf, 0, bytesRead);
                //System.out.print(new String(buf, 0, bytesRead));
            } else if (bytesRead == -1) {
                endOfStream = true;
            }
        }
    }

    public void run() {
        try {
            while (!endOfStream) {
                pump();
                sleep(5);
            }
        } catch (InterruptedException ie) {
            // ignore
        } catch (IOException ioe) {
            // ignore
        }
    }


}
