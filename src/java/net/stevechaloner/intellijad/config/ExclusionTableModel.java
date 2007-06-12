package net.stevechaloner.intellijad.config;

import net.stevechaloner.intellijad.IntelliJadResourceBundle;

import javax.swing.table.DefaultTableModel;

/**
 * @author Steve Chaloner
 */
public class ExclusionTableModel extends DefaultTableModel
{
    public ExclusionTableModel()
    {
        setColumnCount(2);
        setColumnIdentifiers(new String[]
                {
                        IntelliJadResourceBundle.message("config.path"),
                        IntelliJadResourceBundle.message("config.recursive")
                });
    }


    public Class<?> getColumnClass(int i)
    {
        return i == 0 ? String.class : Boolean.class;
    }

    /**
     * Add an exclusion to the table model.
     *
     * @param packageName the name of the package to exclude
     * @param recursive   true iff all subpackages should also be excluded
     */
    public void addExclusion(String packageName,
                             boolean recursive)
    {
        this.addRow(new Object[]{packageName,
                                 recursive});
    }
}
