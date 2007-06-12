package net.stevechaloner.intellijad.io;

import java.util.zip.ZipEntry;

/**
 *
 */
public class ZipEntryPrefixFilter implements ZipEntryFilter {
    private String prefix;

    public ZipEntryPrefixFilter(String aPrefix) {
        if (aPrefix != null && aPrefix.startsWith("/")) {
            aPrefix = aPrefix.substring(1);
        }

        prefix = aPrefix;
    }

    public boolean accept(ZipEntry anEntry) {
        return prefix == null || anEntry.getName().startsWith(prefix);
    }
}
