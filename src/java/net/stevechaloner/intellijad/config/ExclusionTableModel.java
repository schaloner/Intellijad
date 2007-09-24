/*
 * Copyright 2007 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package net.stevechaloner.intellijad.config;

import javax.swing.table.DefaultTableModel;

import net.stevechaloner.intellijad.IntelliJadResourceBundle;

import org.jetbrains.annotations.NotNull;

/**
 * Table model for managing automatic decompilation exclusions.
 * 
 * @author Steve Chaloner
 */
public class ExclusionTableModel extends DefaultTableModel
{
    /**
     * The combinations possible for exclusion.
     */
    public enum ExclusionType { NOT_EXCLUDED, EXCLUDED, EXCLUSION_DISABLED }

    public ExclusionTableModel()
    {
        setColumnCount(3);
        setColumnIdentifiers(new String[]
                {
                        IntelliJadResourceBundle.message("config.path"),
                        IntelliJadResourceBundle.message("config.recursive"),
                        IntelliJadResourceBundle.message("config.enabled")
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
     * @param enabled   true iff tthe exclusion is active
     */
    public void addExclusion(@NotNull String packageName,
                             boolean recursive,
                             boolean enabled)
    {

        if (containsPackage(packageName) == ExclusionType.NOT_EXCLUDED)
        {
            this.addRow(new Object[]{packageName,
                                     recursive,
                                     enabled});
        }
    }

    /**
     * Checks if the package is already specified in the model.
     *
     * @param packageName the name of the package
     * @return the exclusion status of the package
     */
    public ExclusionType containsPackage(@NotNull String packageName)
    {
        boolean containsPackage = false;
        ExclusionType exclusionType = ExclusionType.NOT_EXCLUDED;
        for (int i = 0; !containsPackage && i < getRowCount(); i++)
        {
            containsPackage = packageName.equals(getValueAt(i, 0));
            if (containsPackage)
            {
                exclusionType = ((Boolean)getValueAt(i, 2)) ? ExclusionType.EXCLUSION_DISABLED : ExclusionType.EXCLUDED;
            }
        }
        return exclusionType;
    }
}
