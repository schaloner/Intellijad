package net.stevechaloner.intellijad.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Helpermethods for Zip.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @version @version@,  $Id: ZipHelper.java,v 1.2 2003/11/13 21:45:44 hendriks73 Exp $
 */
public final class ZipHelper {

    /**
     * Source-Version
     */
    public static String vcid = "$Id: ZipHelper.java,v 1.2 2003/11/13 21:45:44 hendriks73 Exp $";

    /**
     */
    public static void extract(ZipFile aZipFile,
                               File destinationDir,
                               boolean overwrite,
                               String aPrefix) throws IOException {

        // create dir if necessary
        if (!destinationDir.exists()) {
            if (!destinationDir.mkdirs()) {
                throw new IOException("Failed to create destination directory " + destinationDir);

                // make sure it's a directory
            }
            destinationDir.deleteOnExit();
        }

        if (!destinationDir.isDirectory()) {
            throw new IllegalArgumentException(
                    "Destinationfile has to be a directory.");

            // get Enumeration
        }

        Enumeration e = entries(aZipFile, aPrefix);

        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            File file = new File(destinationDir, ze.getName());

            // System.out.println("Creating " + file);
            if (ze.isDirectory()) {
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        throw new IOException("Failed to create directory " + file);
                    }
                }
                file.deleteOnExit();
            } else {
                File parent = new File(file.getParent());

                // make sure directory exists
                if (!parent.exists()) {
                    if (!parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);

                        // shall we overwrite?
                    }
                }

                if (!overwrite && file.exists()) {
                    throw new IOException("Cannot extract file " + file + ", because it already exists.");
                }
                file.deleteOnExit();

                InputStream in = null;
                OutputStream out = null;

                try {
                    in = aZipFile.getInputStream(ze);
                    out = new FileOutputStream(file);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ioe) {
                    }

                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ioe) {
                    }
                }

                // set lastModified
                long lm = ze.getTime();

                if (lm != -1) {
                    file.setLastModified(lm);
                }
            }
        }
    }

    /**
     */
    public static Enumeration entries(ZipFile aZipFile,
                                      String aPrefix) {
        return new ZipEntryEnumeration(aZipFile,
                new ZipEntryPrefixFilter(aPrefix));
    }

    private static class ZipEntryEnumeration implements Enumeration {
        private ZipEntryFilter filter;
        private Enumeration entries;
        private Object next;

        public ZipEntryEnumeration(ZipFile aZipFile,
                                   ZipEntryFilter anEntryFilter) {
            filter = anEntryFilter;
            entries = aZipFile.entries();
            next = null;
        }

        public boolean hasMoreElements() {
            if (next != null) {
                return true;
            }

            while (entries.hasMoreElements() && next == null) {
                ZipEntry ze = (ZipEntry) entries.nextElement();

                if (filter.accept(ze)) {
                    next = ze;
                }
            }

            return (next != null);
        }

        public Object nextElement() {
            if (hasMoreElements()) {
                Object nextObject = next;

                next = null;

                return nextObject;
            }

            throw new NoSuchElementException();
        }

    }
}

