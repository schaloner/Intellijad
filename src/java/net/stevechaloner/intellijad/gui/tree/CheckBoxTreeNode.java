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

package net.stevechaloner.intellijad.gui.tree;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class CheckBoxTreeNode
{
    @NotNull
    private final Object userObject;

    private boolean selected = false;

    public CheckBoxTreeNode(@NotNull Object userObject)
    {
        this(userObject,
             false);
    }

    public CheckBoxTreeNode(@NotNull Object userObject,
                            boolean selected)
    {
        this.userObject = userObject;
        this.selected = selected;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public String getText()
    {
        return userObject.toString();
    }

    @NotNull
    public Object getUserObject()
    {
        return userObject;
    }

    /** {@inheritDoc} */
    public String toString()
    {
        return getText();
    }
}
