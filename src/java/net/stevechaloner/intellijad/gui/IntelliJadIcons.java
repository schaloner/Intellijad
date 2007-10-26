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

package net.stevechaloner.intellijad.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.stevechaloner.intellijad.IntelliJad;

/**
 *
 */
public class IntelliJadIcons
{
    public static final Icon INTELLIJAD_LOGO_12X12 = new ImageIcon(IntelliJadIcons.class.getClassLoader().getResource("scn-idea-12.png"));

    public static final Icon INTELLIJAD_LOGO_32X32 = new ImageIcon(IntelliJad.class.getClassLoader().getResource("scn-idea-32.png"));

    public static final Icon ARCHIVE = new ImageIcon(IntelliJadIcons.class.getClassLoader().getResource("fileTypes/archive.png"));

    public static final Icon JAVA = new ImageIcon(IntelliJadIcons.class.getClassLoader().getResource("fileTypes/java.png"));

    public static final Icon LIBRARIES = new ImageIcon(IntelliJadIcons.class.getClassLoader().getResource("modules/libraries.png"));

    public static final Icon INFO = new ImageIcon(IntelliJadIcons.class.getClassLoader().getResource("compiler/information.png"));

    public static final Icon ERROR = new ImageIcon(IntelliJadIcons.class.getClassLoader().getResource("compiler/error.png"));
}
