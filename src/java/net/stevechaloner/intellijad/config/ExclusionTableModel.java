package net.stevechaloner.intellijad.config;

import net.stevechaloner.intellijad.IntelliJadResourceBundle;
import org.jetbrains.annotations.NotNull;

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
    public void addExclusion(@NotNull String packageName,
                             boolean recursive)
    {

        if (!containsPackage(packageName))
        {
            this.addRow(new Object[]{packageName,
                                     recursive});
        }
    }

    /**
     * Checks if the package is already specified in the model.
     *
     * @param packageName the name of the package
     * @return true iff the model already contains the package
     */
    public boolean containsPackage(@NotNull String packageName)
    {
        boolean containsPackage = false;
        for (int i = 0; !containsPackage && i < getRowCount(); i++)
        {
            containsPackage = packageName.equals(getValueAt(i, 0));
        }
        return containsPackage;
    }
}
