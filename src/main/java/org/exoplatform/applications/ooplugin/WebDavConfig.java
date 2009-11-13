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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.exoplatform.applications.ooplugin.client.WebDavContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS Author.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class WebDavConfig
{

   private static final Log LOG = ExoLogger.getLogger(WebDavConfig.class);

   public static final String WHOST = "Host";

   public static final String WPORT = "Port";

   public static final String WSERVLET = "Servlet";

   public static final String WREPOSITORY = "Repository";

   public static final String WWORKSPACE = "WorkSpace";

   public static final String WUSER = "User";

   public static final String WPASS = "Pass";

   private String host = "localhost";

   private int port = 8080;

   private String servlet = "/rest/jcr/";

   private String repository = "repository";

   private String workSpace = "production";

   private String user_id = "root";

   private String user_pass = "exo";

   private String configFileName;

   public WebDavConfig()
   {
      configFileName = LocalFileSystem.getDocumentsPath() + File.separatorChar + "exoplugin.config";
      loadConfig();
   }

   public WebDavContext getContext()
   {
      String path = servlet + "/" + repository + "/" + workSpace;

      while (true)
      {
         String replaced = path.replace("//", "/");
         if (replaced.equals(path))
         {
            break;
         }
         path = replaced;
      }

      while (path.endsWith("/"))
      {
         path = path.substring(0, path.length() - 1);
      }

      return new WebDavContext(host, port, path, user_id, user_pass);
   }

   public String getServerPrefix()
   {
      return "http://" + host + ":" + port + servlet + repository + "/" + workSpace;
   }

   public String getHost()
   {
      return host;
   }

   public void setHost(String host)
   {
      this.host = host;
   }

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

   public String getServlet()
   {
      return servlet;
   }

   public void setServlet(String servlet)
   {
      this.servlet = servlet;
   }

   public void setRepository(String repository)
   {
      this.repository = repository;
   }

   public String getRepository()
   {

      String localRepository = repository;

      while (localRepository.startsWith("/"))
      {
         localRepository = localRepository.substring(1);
      }

      while (localRepository.endsWith("/"))
      {
         localRepository = localRepository.substring(0, localRepository.length() - 1);
      }

      return localRepository;
   }

   public String getWorkSpace()
   {
      String localWorkspace = workSpace;

      while (localWorkspace.startsWith("/"))
      {
         localWorkspace = localWorkspace.substring(1);
      }

      while (localWorkspace.endsWith("/"))
      {
         localWorkspace = localWorkspace.substring(0, localWorkspace.length() - 1);
      }

      return localWorkspace;
   }

   public void setWorkSpace(String workSpace)
   {
      this.workSpace = workSpace;
   }

   public String getUserId()
   {
      return user_id;
   }

   public void setUserId(String user_id)
   {
      this.user_id = user_id;
   }

   public String getUserPass()
   {
      return user_pass;
   }

   public void setUserPass(String user_pass)
   {
      this.user_pass = user_pass;
   }

   public void saveConfig() throws Exception
   {
      String outParams = WHOST + "=" + host + "\r\n";
      outParams += WPORT + "=" + port + "\r\n";
      outParams += WSERVLET + "=" + servlet + "\r\n";
      outParams += WREPOSITORY + "=" + repository + "\r\n";
      outParams += WWORKSPACE + "=" + workSpace + "\r\n";
      outParams += WUSER + "=" + user_id + "\r\n";
      outParams += WPASS + "=" + user_pass + "\r\n";

      File outConfigFile = new File(configFileName);
      outConfigFile.createNewFile();

      FileOutputStream outStream = new FileOutputStream(outConfigFile);
      outStream.write(outParams.getBytes());
      outStream.close();
   }

   public void loadConfig()
   {
      try
      {
         File configFile = new File(configFileName);

         if (!configFile.exists())
         {
            LOG.info("Config file not exist!!!!!! USE DEFAULT !!!");
            return;
         }

         FileInputStream inStream = new FileInputStream(configFile);

         byte[] data = new byte[inStream.available()];
         inStream.read(data);
         String confParams = new String(data);
         String[] params = confParams.split("\r\n");

         HashMap<String, String> hParams = new HashMap<String, String>();
         for (int i = 0; i < params.length; i++)
         {
            String[] curParams = params[i].split("=");
            hParams.put(curParams[0], curParams[1]);
         }

         host = hParams.get(WHOST);
         port = new Integer(hParams.get(WPORT));
         servlet = hParams.get(WSERVLET);
         repository = hParams.get(WREPOSITORY);
         workSpace = hParams.get(WWORKSPACE);
         user_id = hParams.get(WUSER);
         user_pass = hParams.get(WPASS);
      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception. " + exc.getMessage(), exc);
      }

   }

}
