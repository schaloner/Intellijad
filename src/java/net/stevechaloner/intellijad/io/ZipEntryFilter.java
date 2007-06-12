package net.stevechaloner.intellijad.io;

import java.util.zip.ZipEntry;

/**
 *
 */
public interface ZipEntryFilter {
    public boolean accept(ZipEntry anEntry);
}
