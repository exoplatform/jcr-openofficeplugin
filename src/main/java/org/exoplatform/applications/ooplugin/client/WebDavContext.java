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

package org.exoplatform.applications.ooplugin.client;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class WebDavContext
{

   protected String serverHost;

   protected int serverPort;

   protected String servletPath;

   protected String userId = null;

   protected String userPass = null;

   public WebDavContext()
   {
   }

   public WebDavContext(String serverHost, int serverPort, String servletPath)
   {

      this.serverHost = serverHost;
      this.serverPort = serverPort;
      this.servletPath = servletPath;
   }

   public WebDavContext(String serverHost, int serverPort, String servletPath, String userId, String userPass)
   {

      this.serverHost = serverHost;
      this.serverPort = serverPort;
      this.servletPath = servletPath;
      this.userId = userId;
      this.userPass = userPass;
   }

   public void setHost(String serverHost)
   {
      this.serverHost = serverHost;
   }

   public String getHost()
   {
      return serverHost;
   }

   public void setPort(int serverPort)
   {
      this.serverPort = serverPort;
   }

   public int getPort()
   {
      return serverPort;
   }

   public void setServletPath(String servletPath)
   {
      this.servletPath = servletPath;
   }

   public String getServletPath()
   {
      return servletPath;
   }

   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   public String getUserId()
   {
      return userId;
   }

   public void setUserPass(String userPass)
   {
      this.userPass = userPass;
   }

   public String getUserPass()
   {
      return userPass;
   }

   public String getServerPrefix()
   {
      String port = (serverPort == 80) ? "" : ":" + serverPort;
      return "http://" + serverHost + port + servletPath;
   }

}
