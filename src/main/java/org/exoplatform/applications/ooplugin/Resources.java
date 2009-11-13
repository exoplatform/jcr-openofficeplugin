/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.applications.ooplugin;

import java.io.File;

/**
 * Created by The eXo Platform SAS Author.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class Resources
{

   private static String extensionPath = null;

   public Resources()
   {
   }

   public static void getextensionPath()
   {
      if (extensionPath == null)
      {
         String classPath = System.getProperty("java.class.path");
         String tab[] = classPath.split(";");
         String arr$[] = tab;
         int len$ = arr$.length;
         int i$ = 0;
         do
         {
            if (i$ >= len$)
            {
               break;
            }
            String s = arr$[i$];
            int pos;
            if ((pos = s.indexOf("exo-oo-addon")) != -1 && s.indexOf(".zip") > pos)
            {
               s = s.substring(0, pos + 20);
               File path = new File(s);
               extensionPath = path.getPath();
               break;
            }
            i$++;
         }
         while (true);
      }
   }

   public static String getImage(String name)
   {
      getextensionPath();
      return (new StringBuilder()).append(extensionPath).append(File.separator).append("images").append(File.separator)
               .append(name).toString();
   }

   public static String getFile(String name)
   {
      getextensionPath();
      return (new StringBuilder()).append(extensionPath).append(File.separator).append(name).toString();
   }

}
