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

import org.exoplatform.applications.ooplugin.client.DavSearch;
import org.exoplatform.applications.ooplugin.client.Multistatus;
import org.exoplatform.applications.ooplugin.client.ResponseDoc;
import org.exoplatform.applications.ooplugin.dialog.Component;
import org.exoplatform.applications.ooplugin.events.ActionListener;
import org.exoplatform.applications.ooplugin.search.SQLQuery;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XListBox;
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

public class SearchDialog
   extends BrowseDialog
{

   private static final Log LOG = ExoLogger.getLogger(SearchDialog.class);

   public static final String DIALOG_NAME = "_SearchDialog";

   public static final String EDT_TEXT = "edtText";

   public static final String BTN_SEARCH = "btnSearch";

   public static final String BTN_OPEN = "btnOpen";

   private String searchContent = "";

   private Thread searchThread;

   public SearchDialog(WebDavConfig config, XComponentContext xComponentContext, XFrame xFrame, XToolkit xToolkit)
   {
      super(config, xComponentContext, xFrame, xToolkit);
      dialogName = DIALOG_NAME;

      addHandler(LST_ITEMS, Component.XTYPE_XLISTBOX, new ListItemsClick());
      addHandler(BTN_SEARCH, Component.XTYPE_XBUTTON, new SearchClick());
      addHandler(BTN_OPEN, Component.XTYPE_XBUTTON, new OpenClick());
      isNeedAddHandlers = false;
   }

   protected void disableAll()
   {
      super.disableAll();
      ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_SEARCH))).setEnable(true);
   }

   protected void enableAll()
   {
      super.enableAll();
      ((XWindow) UnoRuntime.queryInterface(XWindow.class, xControlContainer.getControl(BTN_SEARCH))).setEnable(true);
   }

   private class SearchClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {

         try
         {
            disableAll();

            XTextComponent xEdtText =
                     (XTextComponent) UnoRuntime.queryInterface(XTextComponent.class, xControlContainer
                              .getControl(EDT_TEXT));
            searchContent = xEdtText.getText();

            searchThread = new SearchThread();
            searchThread.start();

         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception" + exc.getMessage(), exc);
         }

      }

   }

   private class SearchThread
      extends Thread
   {
      public void run()
      {
         try
         {
            DavSearch davSearch = new DavSearch(config.getContext());
            davSearch.setResourcePath("/");

            SQLQuery sqlQuery = new SQLQuery();
            sqlQuery.setQuery("select * from nt:base where contains(*, '" + searchContent + "')");

            davSearch.setQuery(sqlQuery);

            int status = davSearch.execute();

            if (status != HTTPStatus.MULTISTATUS)
            {
               showMessageBox("Search error! Code: " + status);
               return;
            }

            Multistatus multistatus = davSearch.getMultistatus();
            responses = multistatus.getResponses();

            fillItemsList();
            if (responses.size() == 0)
            {
               showMessageBox("No files found!");
            }

            enableAll();
         }
         catch (java.lang.Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
         }

      }
   }

   private boolean tryOpenSelected() throws Exception
   {
      XListBox xListBox = (XListBox) UnoRuntime.queryInterface(XListBox.class, xControlContainer.getControl(LST_ITEMS));
      int selectedPos = xListBox.getSelectedItemPos();

      if (selectedPos < 0)
      {
         return false;
      }

      ResponseDoc response = responses.get(selectedPos);

      if (isCollection(response))
      {
         return false;
      }

      // String href = TextUtils.UnEscape(response.getHref(), '%');
      String href = response.getHref();
      doOpenRemoteFile(href);
      return true;
   }

   private class ListItemsClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {
         try
         {
            tryOpenSelected();
         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
         }

      }

   }

   private class OpenClick
      extends ActionListener
   {

      public void actionPerformed(ActionEvent arg0)
      {
         try
         {
            tryOpenSelected();
         }
         catch (Exception exc)
         {
            LOG.info("Unhandled exception. " + exc.getMessage(), exc);
         }

      }

   }

}
