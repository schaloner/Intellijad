package net.stevechaloner.intellijad;

public class Jad {

/*    public InputStream decompile(Config config,
                                 String[] files) throws IOException,
                                                        InterruptedException {
        //System.out.println(execPath + config.toString() + " " + files);
        List<CommandLinePropertyDescriptor> commandLineProperties = config.getCommandLinePropertyDescriptors();
        String[] command = new String[files.length + optionsArray.length + 1];
        int i = 0;
        command[i++] = execPath;
        for (String anOptionsArray : optionsArray) {
            command[i++] = anOptionsArray;
        }
        for (String file : files) {
            command[i++] = file;
        }
        //String _command = execPath + config.toString() + " " + files;
        // Thanks to Edoardo Comar for this workaround for Linux
        //if (!DecompileAction.WINDOWS) _command = _command.replace('"',' ');
        Process p = Runtime.getRuntime().exec(command);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        new StreamPumper(p.getErrorStream(), err).start();
        new StreamPumper(p.getInputStream(), out).start();
        p.waitFor();
        if (err.size() > 0) {
            Logger.getInstance(getClass().getName()).info("System.err: " + err.toString());
        }
        if (out.size() > 0) {
            Logger.getInstance(getClass().getName()).info("System.out: " + out.toString());
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
                err.toByteArray())));
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("Parsing")) {
                throw new JadException(err.toString(),
                                       command);
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }*/
}