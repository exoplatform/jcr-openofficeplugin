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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowAttribute;
import com.sun.star.awt.WindowClass;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class AddonHandler
   extends WeakBase
   implements com.sun.star.lang.XServiceInfo, com.sun.star.frame.XDispatchProvider, com.sun.star.lang.XInitialization,
   com.sun.star.frame.XDispatch
{

   private static final Log LOG = ExoLogger.getLogger(AddonHandler.class);

   public static final String MENU_OPEN = "open";

   public static final String MENU_SAVE = "save";

   public static final String MENU_SAVEAS = "saveas";

   public static final String MENU_SEARCH = "search";

   public static final String MENU_SETTINGS = "settings";

   public static final String MENU_ABOUT = "about";

   private XComponentContext xComponentContext;

   private static XFrame xFrame;

   private static XToolkit xToolkit;

   private static final String m_implementationName = AddonHandler.class.getName();

   private static WebDavConfig config;

   private static final String[] m_serviceNames =
   {"com.sun.star.frame.ProtocolHandler"};

   public AddonHandler(XComponentContext xComponentContext)
   {
      try
      {
         this.xComponentContext = xComponentContext;
         if (config == null)
         {
            config = new WebDavConfig();
         }
      }
      catch (Throwable thr)
      {
         LOG.info("Undandled exception: " + thr.getMessage(), thr);
      }
   }

   public static XSingleComponentFactory __getComponentFactory(String sImplementationName)
   {
      XSingleComponentFactory xFactory = null;
      if (sImplementationName.equals(m_implementationName))
      {
         xFactory = Factory.createComponentFactory(AddonHandler.class, m_serviceNames);
      }
      return xFactory;
   }

   public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey)
   {
      try
      {
         return Factory.writeRegistryServiceInfo(m_implementationName, m_serviceNames, xRegistryKey);
      }
      catch (Throwable thr)
      {
         LOG.info("Undandled exception: " + thr.getMessage(), thr);
      }
      return false;
   }

   public String getImplementationName()
   {
      return m_implementationName;
   }

   public boolean supportsService(String sService)
   {
      int len = m_serviceNames.length;
      for (int i = 0; i < len; i++)
      {
         if (sService.equals(m_serviceNames[i]))
         {
            return true;
         }
      }
      return false;
   }

   public String[] getSupportedServiceNames()
   {
      return m_serviceNames;
   }

   public com.sun.star.frame.XDispatch queryDispatch(com.sun.star.util.URL aURL, String sTargetFrameName,
            int iSearchFlags)
   {
      if (aURL.Protocol.compareTo("org.exoplatform.applications.ooplugin:") == 0)
      {
         if (aURL.Path.compareTo(MENU_OPEN) == 0)
         {
            return this;
         }
         if (aURL.Path.compareTo(MENU_SAVE) == 0)
         {
            return this;
         }
         if (aURL.Path.compareTo(MENU_SAVEAS) == 0)
         {
            return this;
         }
         if (aURL.Path.compareTo(MENU_SEARCH) == 0)
         {
            return this;
         }
         if (aURL.Path.compareTo(MENU_SETTINGS) == 0)
         {
            return this;
         }
         if (aURL.Path.compareTo(MENU_ABOUT) == 0)
         {
            return this;
         }
      }
      return null;
   }

   public com.sun.star.frame.XDispatch[] queryDispatches(com.sun.star.frame.DispatchDescriptor[] seqDescriptors)
   {
      LOG.info("public com.sun.star.frame.XDispatch[] queryDispatches(");
      int nCount = seqDescriptors.length;
      com.sun.star.frame.XDispatch[] seqDispatcher = new com.sun.star.frame.XDispatch[seqDescriptors.length];
      for (int i = 0; i < nCount; ++i)
      {
         seqDispatcher[i] =
                  queryDispatch(seqDescriptors[i].FeatureURL, seqDescriptors[i].FrameName,
                           seqDescriptors[i].SearchFlags);
      }
      return seqDispatcher;
   }

   public void initialize(Object[] object) throws com.sun.star.uno.Exception
   {
      // log.info("public void initialize( Object[] object ) throws com.sun.star.uno.Exception...");
      if (object.length > 0)
      {
         xFrame = (XFrame) UnoRuntime.queryInterface(XFrame.class, object[0]);
      }

      xToolkit =
               (XToolkit) UnoRuntime.queryInterface(XToolkit.class, xComponentContext.getServiceManager()
                        .createInstanceWithContext("com.sun.star.awt.Toolkit", xComponentContext));
   }

   public void dispatch(com.sun.star.util.URL aURL, com.sun.star.beans.PropertyValue[] aArguments)
   {

      // log.info("public void dispatch(com.sun.star.util.URL aURL,");

      try
      {
         if (aURL.Protocol.compareTo("org.exoplatform.applications.ooplugin:") == 0)
         {
            if (aURL.Path.compareTo(MENU_OPEN) == 0)
            {
               OpenDialog openDialog = new OpenDialog(config, xComponentContext, xFrame, xToolkit);
               openDialog.createDialog();
               return;
            }
            if (aURL.Path.compareTo(MENU_SAVE) == 0)
            {
               SaveDialog saveDialog = new SaveDialog(config, xComponentContext, xFrame, xToolkit, false);
               saveDialog.createDialog();
               return;
            }
            if (aURL.Path.compareTo(MENU_SAVEAS) == 0)
            {
               SaveDialog saveDialog = new SaveDialog(config, xComponentContext, xFrame, xToolkit, true);
               saveDialog.createDialog();
               return;
            }
            if (aURL.Path.compareTo(MENU_SEARCH) == 0)
            {
               SearchDialog searchDialog = new SearchDialog(config, xComponentContext, xFrame, xToolkit);
               searchDialog.createDialog();
               return;
            }
            if (aURL.Path.compareTo(MENU_SETTINGS) == 0)
            {
               SettingsDialog settingsDialog = new SettingsDialog(config, xComponentContext, xFrame, xToolkit);
               settingsDialog.createDialog();
               return;
            }
            if (aURL.Path.compareTo(MENU_ABOUT) == 0)
            {
               AboutDialog aboutDialog = new AboutDialog(config, xComponentContext, xFrame, xToolkit);
               aboutDialog.createDialog();
               return;
            }

         }
      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception. " + exc.getMessage(), exc);
      }

   }

   public void addStatusListener(com.sun.star.frame.XStatusListener xControl, com.sun.star.util.URL aURL)
   {
      // Log.info("public void addStatusListener(com.sun.star.frame.XStatusListener xControl,");
   }

   public void removeStatusListener(com.sun.star.frame.XStatusListener xControl, com.sun.star.util.URL aURL)
   {
      // Log.info("public void removeStatusListener(com.sun.star.frame.XStatusListener xControl,");
   }

   public static void showMessageBox(String sTitle, String sMessage)
   {
      try
      {
         if (null != xFrame && null != xToolkit)
         {
            WindowDescriptor aDescriptor = new WindowDescriptor();
            aDescriptor.Type = WindowClass.MODALTOP;
            aDescriptor.WindowServiceName = new String("infobox");
            aDescriptor.ParentIndex = -1;
            aDescriptor.Parent =
                     (XWindowPeer) UnoRuntime.queryInterface(XWindowPeer.class, xFrame.getContainerWindow());
            aDescriptor.Bounds = new Rectangle(0, 0, 300, 200);
            aDescriptor.WindowAttributes =
                     WindowAttribute.BORDER | WindowAttribute.MOVEABLE | WindowAttribute.CLOSEABLE;

            XWindowPeer xPeer = xToolkit.createWindow(aDescriptor);
            if (null != xPeer)
            {
               XMessageBox xMsgBox = (XMessageBox) UnoRuntime.queryInterface(XMessageBox.class, xPeer);
               if (null != xMsgBox)
               {
                  xMsgBox.setCaptionText(sTitle);
                  xMsgBox.setMessageText(sMessage);
                  xMsgBox.execute();
               }
            }
         }
      }
      catch (com.sun.star.uno.Exception e)
      {
         LOG.info("Unhandled exception. " + e.getMessage(), e);
      }
   }

}
