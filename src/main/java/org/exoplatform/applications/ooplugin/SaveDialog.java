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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.exoplatform.applications.ooplugin.WebDavConstants.WebDavProp;
import org.exoplatform.applications.ooplugin.client.ResponseDoc;
import org.exoplatform.applications.ooplugin.config.FilterListLoader;
import org.exoplatform.applications.ooplugin.config.FilterType;
import org.exoplatform.applications.ooplugin.dialog.Component;
import org.exoplatform.applications.ooplugin.dialog.DialogException;
import org.exoplatform.applications.ooplugin.events.ActionListener;
import org.exoplatform.applications.ooplugin.props.DisplayNameProp;
import org.exoplatform.applications.ooplugin.utils.TextUtils;
import org.exoplatform.applications.ooplugin.utils.WebDavUtils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XComboBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyValue;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.frame.XModuleManager;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * Created by The eXo Platform SAS Author.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class SaveDialog
   extends BrowseDialog
{

   private static final Log LOG = ExoLogger.getLogger(SaveDialog.class);

   public static final String DIALOG_NAME = "_SaveDialog";

   private Thread launchThread;

   private Thread enableSaveThread;

   private XMultiComponentFactory xMultiComponentFactory;

   private XStorable xStorable;

   private XDocumentInfo xDocumentInfo;

   private XModel xModel;

   private String localFilePath = "";

   private String currentModelName = "";

   private static final String EDT_NAME = "edtName";

   private static final String COMBO_TYPE = "edtType";

   private static final String BTN_SAVE = "btnSave";

   private boolean isSaveAs = false;

   private String oldFileName = "";

   private FilterListLoader filterLoader;

   public SaveDialog(WebDavConfig config, XComponentContext xComponentContext, XFrame xFrame, XToolkit xToolkit,
            boolean isSaveAs)
   {
      super(config, xComponentContext, xFrame, xToolkit);

      filterLoader = new FilterListLoader();

      dialogName = DIALOG_NAME;

      this.isSaveAs = isSaveAs;

      addHandler(BTN_SAVE, Component.XTYPE_XBUTTON, new SaveClick());

      launchThread = new LaunchThread();
      launchThread.start();
   }

   @Override
   protected void disableAll()
   {
      super.disableAll();
   }

   @Override
   protected void enableAll()
   {
      super.enableAll();
   }

   private class SaveClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {
         try
         {
            doSaveFile();
         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception: " + exc.getMessage(), exc);
         }
      }

   }

   protected String getEditFileName()
   {
      XTextComponent xComboText =
               (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl(EDT_NAME));
      return xComboText.getText();
   }

   protected void setEditFileName(String fileName)
   {
      XTextComponent xComboText =
               (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl(EDT_NAME));
      xComboText.setText(fileName);
   }

   protected void doSelectItem()
   {
      int selectedPos = getSelectedItemPos();

      if (selectedPos < 0)
      {
         return;
      }

      ResponseDoc response = responses.get(selectedPos);

      if (isCollection(response))
      {
         doPropFindResponse(response);
      }
      else
      {
         try
         {

            DisplayNameProp displayNameProperty = (DisplayNameProp) response.getProperty(WebDavProp.DISPLAYNAME);

            setEditFileName(displayNameProperty.getDisplayName());
         }
         catch (Exception exc)
         {
            LOG.info("Can't open remote file... " + exc.getMessage(), exc);
         }
      }

   }

   // 0 - fileName
   // 1 - filterName
   public String[] getTypedFileInfo() throws DialogException
   {
      String fileName = getEditFileName();
      return getTypedFileInfo(fileName);
   }

   public String[] getTypedFileInfo(String fileName) throws DialogException
   {
      XComboBox xComboType =
               (XComboBox) UnoRuntime.queryInterface(XComboBox.class, xControlContainer.getControl(COMBO_TYPE));

      String comboText = getEditFilterValue();

      int index = -1;

      for (int i = 0; i < xComboType.getItemCount(); i++)
      {
         String comboValue = xComboType.getItem((short) i);
         if (comboValue.equals(comboText))
         {
            index = i;
            break;
         }
      }

      if (index < 0)
      {
         throw new DialogException("Can't use this type of file!!!");
      }

      ArrayList<FilterType> filters = filterLoader.getFilterTypes(currentModelName);
      FilterType filter = filters.get(index);

      String[] fileInfo = new String[2];

      if (!fileName.endsWith("." + filter.getFileExtension()))
      {
         fileName += ("." + filter.getFileExtension());
      }

      fileInfo[0] = fileName;
      fileInfo[1] = filter.getApiName();

      return fileInfo;
   }

   protected void doSaveFile() throws Exception
   {
      String[] fileInfo = getTypedFileInfo();

      String fileName = fileInfo[0];
      String filterName = fileInfo[1];

      if ("".equals(fileName))
      {
         showMessageBox("File name required!");
         return;
      }

      if (!oldFileName.equals(fileName))
      {

         // try search file in responses...

         boolean finded = false;

         for (int i = 0; i < responses.size(); i++)
         {
            ResponseDoc response = responses.get(i);

            DisplayNameProp displayNameProperty = (DisplayNameProp) response.getProperty(WebDavProp.DISPLAYNAME);
            if (displayNameProperty.getDisplayName().equals(fileName))
            {
               finded = true;
               break;
            }
         }

         if (finded)
         {
            short confirmResult = confirmMessageBox("Do you want to rewrite file " + fileName);

            // if Cancel
            if (confirmResult == 0)
            {
               xDialog.endExecute();
               return;
            }

            // if No
            if (confirmResult == 3)
            {
               return;
            }
         }

      }

      String storeToPath =
               LocalFileSystem.getDocumentsPath() + File.separatorChar + LocalFileSystem.STORAGEDIR
                        + File.separatorChar + config.getWorkSpace() + currentPath;
      storeToPath = storeToPath.replace("\\", "/");

      prepareTmpPath(storeToPath);

      if ("/".equals(currentPath))
      {
         storeToPath += fileName;
      }
      else
      {
         storeToPath += "/" + fileName;
      }
      storeToPath = storeToPath.replace("\\", "/");

      storeLocal(storeToPath, filterName);

      String repoFilePath = currentPath;
      if (repoFilePath.endsWith("/"))
      {
         repoFilePath += fileName;
      }
      else
      {
         repoFilePath += "/" + fileName;
      }

      if (doSave(storeToPath, repoFilePath))
      {
         xDialog.endExecute();
      }
   }

   protected void setSessionPath(String path) throws IndexOutOfBoundsException
   {
      showMessageBox("Set session path: " + path);

      xDocumentInfo.setUserFieldName((short) 0, "eXoRemoteFileName");
      xDocumentInfo.setUserFieldValue((short) 0, path);
   }

   protected String getSessionPath() throws IndexOutOfBoundsException
   {
      if (xDocumentInfo.getUserFieldName((short) 0).compareTo("eXoRemoteFileName") == 0
               && xDocumentInfo.getUserFieldValue((short) 0).length() > 0)
      {

         return xDocumentInfo.getUserFieldValue((short) 0);
      }
      return null;
   }

   public String getEditFilterValue()
   {
      XTextComponent xComboText =
               (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer
                        .getControl(COMBO_TYPE));
      return xComboText.getText();
   }

   public void setEditFilterValue(String typeValue)
   {
      XTextComponent xComboText =
               (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer
                        .getControl(COMBO_TYPE));
      xComboText.setText(typeValue);
   }

   public void storeLocal(String url, String filter)
   {
      try
      {
         String path =
                  com.sun.star.uri.ExternalUriReferenceTranslator.create(xComponentContext).translateToInternal(
                           "file:///" + url.replace("\\", "/"));
         if (path.length() == 0)
         {
            throw new RuntimeException();
         }

         PropertyValue[] loadProps = new PropertyValue[2];
         PropertyValue asTemplate = new PropertyValue();
         loadProps[0] = asTemplate;

         PropertyValue documentType = new PropertyValue();
         documentType.Name = "FilterName";
         documentType.Value = filter;
         loadProps[1] = documentType;

         xStorable.storeAsURL(path, loadProps);
      }
      catch (com.sun.star.io.IOException e)
      {
         showMessageBox("Can't save file locally!!!!!!!!");
         LOG.info("Exception" + e.getMessage(), e);
      }
   }

   @Override
   public boolean launchBeforeOpen()
   {
      if (!super.launchBeforeOpen())
      {
         return false;
      }

      try
      {
         initDefaults();
      }
      catch (Exception pexc)
      {
         showMessageBox("Can't save document!");
         return false;
      }

      try
      {
         XComboBox xComboType =
                  (XComboBox) UnoRuntime.queryInterface(XComboBox.class, xControlContainer.getControl(COMBO_TYPE));

         ArrayList<FilterType> filters = filterLoader.getFilterTypes(currentModelName);
         if (filters.size() != 0)
         {
            for (int i = filters.size() - 1; i >= 0; i--)
            {
               FilterType currentFilter = filters.get(i);
               String filterStr = currentFilter.getLocalizedName() + " [." + currentFilter.getFileExtension() + "]";
               xComboType.addItem(filterStr, (short) 0);
               if (i == 0)
               {
                  setEditFilterValue(filterStr);
               }
            }
         }

         if ("".equals(localFilePath))
         {
            return true;
         }

         if (isSaveAs)
         {
            if (getSessionPath() != null)
            {
               String sessPath = getSessionPath();
               String onlyPath = sessPath.substring(0, sessPath.lastIndexOf("/"));
               currentPath = onlyPath;
               String onlyName = sessPath.substring(sessPath.lastIndexOf("/") + 1);
               setEditFileName(onlyName);
               oldFileName = onlyName;
            }
            else
            {
               String onlyName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);
               setEditFileName(onlyName);
               oldFileName = onlyName;
            }
            return true;
         }

         String onlyName = localFilePath.substring(localFilePath.lastIndexOf("/") + 1);
         setEditFileName(onlyName);

         String repositoryFolderName =
                  LocalFileSystem.getDocumentsPath() + File.separatorChar + LocalFileSystem.STORAGEDIR
                           + File.separatorChar + config.getWorkSpace();
         repositoryFolderName = repositoryFolderName.replace("\\", "/");

         if (getSessionPath() != null)
         {
            String remotePath = getSessionPath();
            xStorable.store();
            doSave(localFilePath, remotePath);
            return false;
         }

         if (localFilePath.startsWith(repositoryFolderName))
         {
            xStorable.store();
            String remotePath = localFilePath.substring(repositoryFolderName.length());
            doSave(localFilePath, remotePath);
            return false;
         }

         return true;
      }
      catch (Exception exc)
      {
         LOG.info("Unhandled ecxeption. " + exc.getMessage(), exc);
      }

      return false;
   }

   protected void initDefaults() throws Exception
   {
      xMultiComponentFactory = xComponentContext.getServiceManager();

      Object descTop =
               xMultiComponentFactory.createInstanceWithContext("com.sun.star.frame.Desktop", xComponentContext);

      XDesktop xDescTop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, descTop);
      XComponent xComponent = xDescTop.getCurrentComponent();

      Object moduleManager =
               xMultiComponentFactory.createInstanceWithContext("com.sun.star.frame.ModuleManager", xComponentContext);
      XModuleManager xMM = (XModuleManager) UnoRuntime.queryInterface(XModuleManager.class, moduleManager);
      currentModelName = xMM.identify(xComponent);

      xStorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, xComponent);

      XDocumentInfoSupplier xDocumentInfoSupplier =
               (XDocumentInfoSupplier) UnoRuntime.queryInterface(XDocumentInfoSupplier.class, xComponent);

      xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();
      xModel = (XModel) UnoRuntime.queryInterface(XModel.class, xComponent);

      localFilePath = getDocumentFileName();
   }

   public String getDocumentFileName() throws UnsupportedEncodingException
   {
      return URLDecoder.decode(xModel.getURL().replaceFirst("file:///", ""), "UTF-8");
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

            enableSaveThread = new EnableSaveThread();
            enableSaveThread.start();

            doPropFind();

         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
         }
      }
   }

   private class EnableSaveThread
      extends Thread
   {
      public void run()
      {
         try
         {
            while (true)
            {
               Thread.sleep(100);

               if ("".equals(getEditFileName()))
               {
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_SAVE)))
                           .setEnable(false);
               }
               else
               {
                  ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_SAVE)))
                           .setEnable(true);
               }

            }
         }
         catch (Exception exc)
         {
         }
      }
   }

   protected boolean doSave(String localPath, String remotePath)
   {
      try
      {
         File inFile = new File(localPath);

         remotePath = remotePath.replace("//", "/");

         if (remotePath.contains("?version"))
         {
            remotePath = remotePath.substring(0, remotePath.lastIndexOf("?"));
         }

         HTTPConnection connection = WebDavUtils.getAuthConnection(config);

         String type = localPath.substring(localPath.lastIndexOf('.'));
         String mimetype = TextUtils.getMimeType(type);

         NVPair mimeHeader = new NVPair(WebDavProp.CONTENTTYPE, mimetype);

         NVPair[] headers = new NVPair[]
         {mimeHeader};

         HTTPResponse response =
                  connection.Put(WebDavUtils.getFullPath(config) + TextUtils.EncodePath(remotePath), WebDavUtils
                           .getBytes(inFile), headers);

         int status = response.getStatusCode();
         if (status == HTTPStatus.CREATED)
         {
            showMessageBox("File " + config.getContext().getServerPrefix() + remotePath + " succesfully saved!");
            return true;
         }

         showMessageBox("Can't store file. Error code: " + status);
      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception. " + exc.getMessage(), exc);
         showMessageBox("Can't store file. Error: " + exc.getMessage());
      }
      return false;
   }

}
