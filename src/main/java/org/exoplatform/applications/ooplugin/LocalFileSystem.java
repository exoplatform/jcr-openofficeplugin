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
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * 
 * @version $Id: $
 */

public class LocalFileSystem
{

   public static String DOCUMENDIR = "eXo-Platform Documents";

   public static String STORAGEDIR = "jcr";

   public LocalFileSystem()
   {
   }

   public static String getDocumentsPath()
   {
      String path = File.separatorChar + "tmp" + File.separatorChar + DOCUMENDIR;
      File f = new File(path);

      if (!f.exists())
      {
         f.mkdirs();
      }

      return f.getAbsolutePath();
   }

   public static String getLocalPath(String dst, String name)
   {
      String path =
               getDocumentsPath() + dst.replace('/', File.separatorChar) + File.separatorChar
                        + name.replace('/', File.separatorChar);

      File f = new File(path);
      if (!f.exists())
      {
         f.mkdirs();
      }

      f.delete();
      return f.getAbsolutePath();
   }

}
