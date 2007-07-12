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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Steve Chaloner
 */
public class LibraryUtil
{
    private LibraryUtil() {
    }

    public static List<Library> findModuleLibrariesByClass(String fqn,
                                                           Project project)
    {

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        List<Module> modules = new ArrayList<Module>(Arrays.asList(moduleManager.getSortedModules()));
        List<Library> libraries = new ArrayList<Library>();
        for (Module module : modules) {
            ModuleRootManager mrm = ModuleRootManager.getInstance(module);
            LibraryTable table = mrm.getModifiableModel().getModuleLibraryTable();
            Library library = findInTable(table,
                                          fqn);
            if (library != null)
            {
                libraries.add(library);
            }
        }
        return libraries;
    }

    public static boolean isClassAvailableInLibrary(Library library, String fqn) {
        return isClassAvailableInLibrary(library.getFiles(OrderRootType.CLASSES), fqn);
    }

    public static boolean isClassAvailableInLibrary(VirtualFile files[], String fqn) {
        int len = files.length;
        boolean found = false;
        for (int i = 0; !found && i < len; i++) {
            if (findInFile(files[i],
                    new StringTokenizer(fqn, "."))) {
                found = true;
            }
        }

        return found;
    }

    public static Library findLibraryByClass(String fqn, Project project) {
        if (project != null) {
            LibraryTable projectTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
            Library library = findInTable(projectTable, fqn);
            if (library != null) {
                return library;
            }
        }
        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable();
        return findInTable(table, fqn);
    }

    private static boolean findInFile(VirtualFile file, StringTokenizer tokenizer) {
        if (!tokenizer.hasMoreTokens()) {
            return true;
        }
        StringBuffer name = new StringBuffer(tokenizer.nextToken());
        if (!tokenizer.hasMoreTokens()) {
            name.append(".class");
        }
        VirtualFile child = file.findChild(name.toString());
        return child != null && findInFile(child, tokenizer);
    }

    private static Library findInTable(LibraryTable table, String fqn) {
        Library[] arr = table.getLibraries();
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            Library library = arr[i];
            if (isClassAvailableInLibrary(library, fqn)) {
                return library;
            }
        }

        return null;
    }

    public static Library createLibrary(LibraryTable libraryTable, String baseName) {
        String name = baseName;
        int count = 2;
        for (; libraryTable.getLibraryByName(name) != null; name = (new StringBuilder()).append(baseName).append(" (").append(count++).append(")").toString())
        {
        }
        return libraryTable.createLibrary(name);
    }
}
