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

import org.exoplatform.applications.ooplugin.WebDavConstants.WebDavProp;
import org.exoplatform.applications.ooplugin.client.ResponseDoc;
import org.exoplatform.applications.ooplugin.dialog.Component;
import org.exoplatform.applications.ooplugin.events.ActionListener;
import org.exoplatform.applications.ooplugin.events.ItemListener;
import org.exoplatform.applications.ooplugin.props.VersionNameProp;
import org.exoplatform.applications.ooplugin.utils.TextUtils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.ItemEvent;
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

public class OpenDialog
   extends BrowseDialog
{

   private static final Log LOG = ExoLogger.getLogger(OpenDialog.class);

   private static final String DIALOGNAME = "_OpenDialog";

   public static final String BTN_VERSIONS = "btnVersions";

   public static final String BTN_OPEN = "btnOpen";

   private Thread launchThread;

   private Thread viewVersionEnableThread;

   public OpenDialog(WebDavConfig config, XComponentContext xComponentContext, XFrame xFrame, XToolkit xToolkit)
   {
      super(config, xComponentContext, xFrame, xToolkit);
      dialogName = DIALOGNAME;

      addHandler(BTN_OPEN, Component.XTYPE_XBUTTON, new OpenClick());

      addHandler(COMBO_PATH, Component.XTYPE_XCOMBOBOX, new PathChanged());
      addHandler(BTN_VERSIONS, Component.XTYPE_XBUTTON, new VersionsClick());

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

            viewVersionEnableThread = new ViewVersionsButtonEnableThread();
            viewVersionEnableThread.start();

            doPropFind();
         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
            exc.printStackTrace(System.out);
         }
      }
   }

   protected void enableVersionView(boolean isEnabled)
   {
      try
      {
         ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_VERSIONS)))
                  .setEnable(isEnabled);
      }
      catch (NullPointerException nullExc)
      {
      }
   }

   private class ViewVersionsButtonEnableThread
      extends Thread
   {

      public void run()
      {
         while (true)
         {
            try
            {
               Thread.sleep(100);
               int selectedPos = getSelectedItemPos();

               if (selectedPos >= 0)
               {
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_OPEN)))
                           .setEnable(true);

                  ResponseDoc response = responses.get(selectedPos);
                  VersionNameProp versionNameProperty = (VersionNameProp) response.getProperty(WebDavProp.VERSIONNAME);
                  if (versionNameProperty != null && versionNameProperty.getStatus() == HTTPStatus.OK)
                  {
                     enableVersionView(true);
                     continue;
                  }

               }
               else
               {
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_OPEN)))
                           .setEnable(false);
               }

            }
            catch (Exception exc)
            {
            }

            enableVersionView(false);
         }

      }

   }

   protected void disableAll()
   {
      super.disableAll();
      // ((XWindow)UnoRuntime.queryInterface(
      // XWindow.class, xControlContainer.getControl(BTN_OPEN))).setEnable(false);
   }

   protected void enableAll()
   {
      super.enableAll();
   }

   private class PathChanged
      extends ItemListener
   {

      public void itemStateChanged(ItemEvent arg0)
      {
         XTextComponent xComboText =
                  (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer
                           .getControl(COMBO_PATH));
         String path = xComboText.getText();

         String serverPrefix = config.getServerPrefix();

         if (!path.startsWith(serverPrefix))
         {
            LOG.info("Can't connect remote WebDav server!!!");
            return;
         }

         path = path.substring(serverPrefix.length());
         if ("".equals(path))
         {
            path = "/";
         }

         currentPath = path;
         doPropFind();
      }
   }

   private class OpenClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {
         doSelectItem();
      }

   }

   private class VersionsClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {

         try
         {
            int selectedPos = getSelectedItemPos();

            if (selectedPos < 0)
            {
               return;
            }

            ResponseDoc response = responses.get(selectedPos);
            String href = TextUtils.UnEscape(response.getHref(), '%');

            if (!href.startsWith(config.getServerPrefix()))
            {
               showMessageBox("Can't load version list.");
               return;
            }

            String remoteHref = href.substring(config.getServerPrefix().length());

            prepareTmpPath(currentPath);

            ViewVersions opVersions = new ViewVersions(config, xComponentContext, xFrame, xToolkit, remoteHref);
            boolean needClose = opVersions.createDialogEx();

            if (needClose)
            {
               xDialog.endExecute();
            }

         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
         }

      }

   }

}
