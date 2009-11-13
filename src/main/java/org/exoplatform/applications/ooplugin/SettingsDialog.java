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

import org.exoplatform.applications.ooplugin.dialog.Component;
import org.exoplatform.applications.ooplugin.events.ActionListener;
import org.exoplatform.applications.ooplugin.utils.WebDavUtils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.frame.XFrame;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * Created by The eXo Platform SAS Author.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class SettingsDialog
   extends PlugInDialog
{

   private static final Log LOG = ExoLogger.getLogger(SettingsDialog.class);

   public static final String NAME = "_SettingsDialog";

   public static final String BTN_SAVE = "btnSave";

   public static final String BTN_TEST = "btnTest";

   public static final String EDT_SERVERNAME = "edtServerName";

   public static final String EDT_PORT = "edtPort";

   public static final String EDT_SERVLET = "edtServlet";

   public static final String EDT_REPOSITORY = "edtRepository";

   public static final String EDT_WORKSPACE = "edtWorkSpace";

   public static final String EDT_USER = "edtUserName";

   public static final String EDT_PASS = "edtPassword";

   private Thread launchThread;

   private Thread enableTestButtonThread;

   public SettingsDialog(WebDavConfig config, XComponentContext xComponentContext, XFrame xFrame, XToolkit xToolkit)
   {
      super(config, xComponentContext, xFrame, xToolkit);
      dialogName = NAME;

      addHandler(BTN_SAVE, Component.XTYPE_XBUTTON, new SaveClick());
      addHandler(BTN_TEST, Component.XTYPE_XBUTTON, new TestClick());

      launchThread = new LaunchThread();
      launchThread.start();
   }

   private class LaunchThread
      extends Thread
   {
      public void run()
      {
         try
         {
            while (!enabled)
            {
               Thread.sleep(100);
            }
            Thread.sleep(100);

            enableTestButtonThread = new EnableTestButtonThread();
            enableTestButtonThread.start();

            setTextBoxValue(EDT_SERVERNAME, config.getHost());
            setTextBoxValue(EDT_PORT, "" + config.getPort());
            setTextBoxValue(EDT_SERVLET, config.getServlet());
            setTextBoxValue(EDT_REPOSITORY, config.getRepository());
            setTextBoxValue(EDT_WORKSPACE, config.getWorkSpace());
            setTextBoxValue(EDT_USER, config.getUserId());
            setTextBoxValue(EDT_PASS, config.getUserPass());

         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception: " + exc.getMessage(), exc);
         }
      }
   }

   private boolean isEntheredAll()
   {
      if ("".equals(getTextBoxValue(EDT_SERVERNAME)))
      {
         return false;
      }

      if ("".equals(getTextBoxValue(EDT_PORT)))
      {
         return false;
      }

      if ("".equals(getTextBoxValue(EDT_SERVLET)))
      {
         return false;
      }

      if ("".equals(getTextBoxValue(EDT_REPOSITORY)))
      {
         return false;
      }

      if ("".equals(getTextBoxValue(EDT_WORKSPACE)))
      {
         return false;
      }

      if ("".equals(getTextBoxValue(EDT_USER)))
      {
         return false;
      }

      if ("".equals(getTextBoxValue(EDT_PASS)))
      {
         return false;
      }

      return true;
   }

   private class EnableTestButtonThread
      extends Thread
   {
      public void run()
      {
         try
         {
            while (true)
            {
               Thread.sleep(100);

               if (isEntheredAll())
               {
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_TEST)))
                           .setEnable(true);
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_SAVE)))
                           .setEnable(true);
               }
               else
               {
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_TEST)))
                           .setEnable(false);
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_SAVE)))
                           .setEnable(false);
               }
            }
         }
         catch (Exception exc)
         {
         }
      }
   }

   protected void setTextBoxValue(String componentName, String textValue)
   {
      XTextComponent xComboText =
               (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer
                        .getControl(componentName));
      xComboText.setText(textValue);
   }

   protected String getTextBoxValue(String componentName)
   {
      XTextComponent xComboText =
               (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer
                        .getControl(componentName));
      return xComboText.getText();
   }

   private class TestClick
      extends ActionListener
   {
      public void actionPerformed(ActionEvent arg0)
      {

         try
         {
            String host = getTextBoxValue(EDT_SERVERNAME);
            int port = new Integer(getTextBoxValue(EDT_PORT));
            String path = getTextBoxValue(EDT_SERVLET);
            String repository = getTextBoxValue(EDT_REPOSITORY);
            String workSpace = getTextBoxValue(EDT_WORKSPACE);
            String userId = getTextBoxValue(EDT_USER);
            String userPass = getTextBoxValue(EDT_PASS);

            WebDavConfig testConfig = config;

            testConfig.setHost(host);
            testConfig.setPort(port);
            testConfig.setServlet(path);
            testConfig.setRepository(repository);
            testConfig.setWorkSpace(workSpace);
            testConfig.setUserId(userId);
            testConfig.setUserPass(userPass);

            HTTPConnection connection = WebDavUtils.getAuthConnection(testConfig);

            String filePath = WebDavUtils.getFullPath(testConfig);
            HTTPResponse response = connection.Head(filePath);

            LOG.info("Testing connection....");

            int status = response.getStatusCode();

            if (status == HTTPStatus.OK)
            {
               showMessageBox("Connection successful!");
               return;
            }

         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception: " + exc.getMessage(), exc);
         }

         showMessageBox(" Can not connect to repository!");

      }
   }

   private class SaveClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {
         try
         {
            String host = getTextBoxValue(EDT_SERVERNAME);
            int port = new Integer(getTextBoxValue(EDT_PORT));
            String path = getTextBoxValue(EDT_SERVLET);
            String repository = getTextBoxValue(EDT_REPOSITORY);
            String workSpace = getTextBoxValue(EDT_WORKSPACE);
            String userId = getTextBoxValue(EDT_USER);
            String userPass = getTextBoxValue(EDT_PASS);

            config.setHost(host);
            config.setPort(port);
            config.setServlet(path);
            config.setRepository(repository);
            config.setWorkSpace(workSpace);
            config.setUserId(userId);
            config.setUserPass(userPass);

            config.saveConfig();
         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
            showMessageBox("Parameters incorrect!!!");
         }

         xDialog.endExecute();
      }

   }

}
