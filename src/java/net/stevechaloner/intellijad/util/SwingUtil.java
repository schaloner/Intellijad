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

package net.stevechaloner.intellijad.util;

import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Utilities for working with Swing components.
 * 
 * @author Steve Chaloner
 */
public class SwingUtil
{
    private SwingUtil()
    {
    }

    public static void center(JDialog dialog)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((int) (screenSize.getWidth() - dialog.getWidth()) / 2,
                           (int) (screenSize.getHeight() - dialog.getHeight()) / 2);
    }
}
