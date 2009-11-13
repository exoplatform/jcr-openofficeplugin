/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.applications.ooplugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.exoplatform.applications.ooplugin.WebDavConfig;
import org.exoplatform.applications.ooplugin.WebDavConstants.WebDav;
import org.exoplatform.common.http.client.CookieModule;
import org.exoplatform.common.http.client.HTTPConnection;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:work.visor.ck@gmail.com">Dmytro Katayev</a>
 */
public class WebDavUtils
{

   public static HTTPConnection getAuthConnection(WebDavConfig config)
   {

      CookieModule.setCookiePolicyHandler(null);

      HTTPConnection connection = new HTTPConnection(config.getHost(), config.getPort());
      connection.addBasicAuthorization(WebDav.REALM, config.getUserId(), config.getUserPass());

      return connection;
   }

   public static String getFullPath(WebDavConfig config)
   {

      String host = config.getHost();
      int port = config.getPort();
      String path = config.getServlet();
      String repository = config.getRepository();
      String workspace = config.getWorkSpace();

      String fullPath = "http://" + host + ":" + port + path + repository + "/" + workspace;

      return fullPath;
   }

   public static byte[] getBytes(File inFile) throws Exception
   {

      FileInputStream fis = new FileInputStream(inFile);
      FileChannel fc = fis.getChannel();
      byte[] data = new byte[(int) fc.size()];
      ByteBuffer bb = ByteBuffer.wrap(data);
      fc.read(bb);
      return data;

   }
}
