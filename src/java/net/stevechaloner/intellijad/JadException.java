package net.stevechaloner.intellijad;

import java.io.IOException;

/**
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @version @version@,  $Id: JadException.java,v 1.2 2003/11/13 21:45:43 hendriks73 Exp $
 */
public class JadException extends IOException {

    private final String output;

    public JadException(String output,
                        String... command) {
        super(getCommandLine(command));
        this.output = output;
    }

    private static String getCommandLine(String... command) {
        StringBuffer sb = new StringBuffer();
        for (String aCommand : command) {
            sb.append(aCommand);
            sb.append(' ');
        }
        return sb.toString();
    }

    public String getOutput() {
        return output;
    }
}
