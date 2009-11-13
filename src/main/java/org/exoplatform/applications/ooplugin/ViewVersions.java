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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.exoplatform.applications.ooplugin.WebDavConstants.WebDavProp;
import org.exoplatform.applications.ooplugin.client.DavReport;
import org.exoplatform.applications.ooplugin.client.Multistatus;
import org.exoplatform.applications.ooplugin.client.ResponseDoc;
import org.exoplatform.applications.ooplugin.dialog.Component;
import org.exoplatform.applications.ooplugin.events.ActionListener;
import org.exoplatform.applications.ooplugin.props.ContentLengthProp;
import org.exoplatform.applications.ooplugin.props.CreationDateProp;
import org.exoplatform.applications.ooplugin.props.CreatorDisplayNameProp;
import org.exoplatform.applications.ooplugin.props.DisplayNameProp;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XToolkit;
import com.sun.star.frame.XFrame;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class ViewVersions
   extends PlugInDialog
{

   private static final Log LOG = ExoLogger.getLogger(ViewVersions.class);

   private static final String DIALOG_NAME = "_ViewVersionsDialog";

   public static final String LST_VERSIONS = "lstVersions";

   public static final String LBL_TABLEHEAD = "lblTableHead";

   public static final String BTN_OPEN = "btnOpen";

   public static final int NAME_LEN = 14;

   public static final int SIZE_LEN = 24;

   public static final int CREATED_LEN = 45;

   private Thread launchThread;

   private String resourcePath = "/";

   private ArrayList<ResponseDoc> responses = new ArrayList<ResponseDoc>();

   private boolean isOpened = false;

   public ViewVersions(WebDavConfig config, XComponentContext xComponentContext, XFrame xFrame, XToolkit xToolkit,
            String resourcePath)
   {
      super(config, xComponentContext, xFrame, xToolkit);
      dialogName = DIALOG_NAME;

      this.resourcePath = resourcePath;

      addHandler(LST_VERSIONS, Component.XTYPE_XLISTBOX, new DoSelectFileClick());
      addHandler(BTN_OPEN, Component.XTYPE_XBUTTON, new DoSelectFileClick());

      launchThread = new LaunchThread();
      launchThread.start();
   }

   public boolean launchBeforeOpen()
   {
      try
      {
         XFixedText xLabelHead =
                  (XFixedText) UnoRuntime.queryInterface(XFixedText.class, xControlContainer.getControl(LBL_TABLEHEAD));

         String headerValue = "Version Name";
         while (headerValue.length() < NAME_LEN)
         {
            headerValue += " ";
         }

         headerValue += "Size";
         while (headerValue.length() < SIZE_LEN)
         {
            headerValue += " ";
         }

         headerValue += "Created";
         while (headerValue.length() < CREATED_LEN)
         {
            headerValue += " ";
         }

         headerValue += "Owner";

         xLabelHead.setText(headerValue);

      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception: " + exc.getMessage(), exc);
      }

      return true;
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
            doReport();
         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
         }
      }
   }

   public boolean createDialogEx() throws com.sun.star.uno.Exception
   {
      super.createDialog();
      return isOpened;
   }

   private boolean doReport() throws Exception
   {
      DavReport davReport = new DavReport(config.getContext());
      davReport.setResourcePath(resourcePath);

      davReport.setRequiredProperty(WebDavProp.DISPLAYNAME);
      davReport.setRequiredProperty(WebDavProp.GETCONTENTLENGTH);
      davReport.setRequiredProperty(WebDavProp.CREATIONDATE);
      davReport.setRequiredProperty(WebDavProp.CREATORDISPLAYNAME);

      davReport.setDepth(1);

      int status = davReport.execute();
      if (status != HTTPStatus.MULTISTATUS)
      {
         showMessageBox("Can't open version list. ErrorCode: " + status);
         return false;
      }

      Multistatus multistatus = davReport.getMultistatus();
      responses = multistatus.getResponses();

      Collections.sort(responses, new VersionComparer());

      XListBox xListBox =
               (XListBox) UnoRuntime.queryInterface(XListBox.class, xControlContainer.getControl(LST_VERSIONS));
      xListBox.removeItems((short) 0, xListBox.getItemCount());

      for (int i = responses.size() - 1; i >= 0; i--)
      {
         ResponseDoc curResponse = responses.get(i);
         xListBox.addItem(formatLine(curResponse), (short) 0);
      }

      return true;
   }

   class VersionComparer
      implements Comparator<ResponseDoc>
   {

      public int compare(ResponseDoc resp1, ResponseDoc resp2)
      {

         CreationDateProp creationDate1 = (CreationDateProp) resp1.getProperty(WebDavProp.CREATIONDATE);
         CreationDateProp creationDate2 = (CreationDateProp) resp2.getProperty(WebDavProp.CREATIONDATE);

         if (!creationDate2.getCreationDate().equals(creationDate1.getCreationDate()))
         {
            return creationDate2.getCreationDate().compareToIgnoreCase(creationDate1.getCreationDate());
         }

         DisplayNameProp displayName1 = (DisplayNameProp) resp1.getProperty(WebDavProp.DISPLAYNAME);
         DisplayNameProp displayName2 = (DisplayNameProp) resp2.getProperty(WebDavProp.DISPLAYNAME);

         return displayName2.getDisplayName().compareToIgnoreCase(displayName1.getDisplayName());

         // if () {
         //        
         // }
         //      
         // ResourceTypeProp rt1 =
         // (ResourceTypeProp)resp1.getProperty(Const.DavProp.RESOURCETYPE);
         // ResourceTypeProp rt2 =
         // (ResourceTypeProp)resp2.getProperty(Const.DavProp.RESOURCETYPE);
         //      
         // if (rt1.isCollection() && !rt2.isCollection()) {
         // return 0;
         // }
         //      
         // if (!rt1.isCollection() && rt2.isCollection()) {
         // return 1;
         // }
         //      
         // DisplayNameProp dn1 =
         // (DisplayNameProp)resp1.getProperty(Const.DavProp.DISPLAYNAME);
         // DisplayNameProp dn2 =
         // (DisplayNameProp)resp2.getProperty(Const.DavProp.DISPLAYNAME);
         //      
         // return dn1.getDisplayName().compareToIgnoreCase(dn2.getDisplayName());
      }
   }

   private String formatLine(ResponseDoc response)
   {
      DisplayNameProp displayNameProperty = (DisplayNameProp) response.getProperty(WebDavProp.DISPLAYNAME);
      ContentLengthProp contentLengthProperty = (ContentLengthProp) response.getProperty(WebDavProp.GETCONTENTLENGTH);
      CreationDateProp creationDateProperty = (CreationDateProp) response.getProperty(WebDavProp.CREATIONDATE);
      CreatorDisplayNameProp creatorDisplayName =
               (CreatorDisplayNameProp) response.getProperty(WebDavProp.CREATORDISPLAYNAME);

      String lineStr = displayNameProperty.getDisplayName();
      while (lineStr.length() < NAME_LEN)
      {
         lineStr += " ";
      }

      if (contentLengthProperty != null)
      {
         lineStr += contentLengthProperty.getContentLength();
      }

      while (lineStr.length() < SIZE_LEN)
      {
         lineStr += " ";
      }

      if (creationDateProperty != null)
      {
         lineStr += creationDateProperty.getCreationDate();
      }

      while (lineStr.length() < CREATED_LEN)
      {
         lineStr += " ";
      }

      if (creatorDisplayName != null)
      {
         lineStr += creatorDisplayName.getCreatorDisplayName();
      }

      return lineStr;
   }

   protected void doOpenVersion()
   {
      try
      {
         XListBox xListBox =
                  (XListBox) UnoRuntime.queryInterface(XListBox.class, xControlContainer.getControl(LST_VERSIONS));
         short selectedItem = xListBox.getSelectedItemPos();
         if (selectedItem < 0)
         {
            return;
         }

         ResponseDoc response = responses.get(selectedItem);
         // String href = TextUtils.UnEscape(response.getHref(), '%');

         String href = response.getHref();
         doOpenRemoteFile(href);
         isOpened = true;
         xDialog.endExecute();

      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception.", exc);
         showMessageBox("Can't open selected version.");
      }
   }

   private class DoSelectFileClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {
         doOpenVersion();
      }

   }

}
