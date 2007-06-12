package net.stevechaloner.intellijad.config;

import net.stevechaloner.intellijad.IntelliJadResourceBundle;

import javax.swing.table.DefaultTableModel;

/**
 * @author Steve Chaloner
 */
class ExcludesTableModel extends DefaultTableModel {
    ExcludesTableModel() {
        setColumnCount(2);
        setColumnIdentifiers(new String[]
                {
                        IntelliJadResourceBundle.message("config.path"),
                        IntelliJadResourceBundle.message("config.recursive")
                });
    }


    public Class<?> getColumnClass(int i) {
        return i == 0 ? String.class : Boolean.class;
    }
}
