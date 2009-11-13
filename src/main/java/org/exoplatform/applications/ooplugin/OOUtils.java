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

import com.sun.star.beans.PropertyValue;
import com.sun.star.document.XDocumentInfo;
import com.sun.star.document.XDocumentInfoSupplier;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * 
 * @version $Id: $
 */

public class OOUtils
{

   private static final Log LOG = ExoLogger.getLogger(OOUtils.class);

   public static XComponent loadFromFile(XComponentContext xComponentContext, String url, String remoteUrl)
            throws Exception
   {

      PropertyValue[] loadProps = null;

      loadProps = new PropertyValue[1];
      PropertyValue asTemplate = new PropertyValue();
      loadProps[0] = asTemplate;

      // Create a blank writer document
      XMultiComponentFactory xMultiComponentFactory = xComponentContext.getServiceManager();
      Object oDesktop;

      oDesktop = xMultiComponentFactory.createInstanceWithContext("com.sun.star.frame.Desktop", xComponentContext);
      XComponentLoader oComponentLoader =
               (com.sun.star.frame.XComponentLoader) com.sun.star.uno.UnoRuntime.queryInterface(
                        com.sun.star.frame.XComponentLoader.class, oDesktop);

      String path =
               com.sun.star.uri.ExternalUriReferenceTranslator.create(xComponentContext).translateToInternal(
                        "file:///" + url.replace("\\", "/"));

      if (path.length() == 0 && url.length() != 0)
      {
         throw new RuntimeException();
      }

      XComponent xComponent = oComponentLoader.loadComponentFromURL(path, "_default", 0, loadProps);

      try
      {
         XDocumentInfoSupplier xDocumentInfoSupplier =
                  (XDocumentInfoSupplier) UnoRuntime.queryInterface(XDocumentInfoSupplier.class, xComponent);
         XDocumentInfo xDocumentInfo = xDocumentInfoSupplier.getDocumentInfo();

         xDocumentInfo.setUserFieldName((short) 0, "eXoRemoteFileName");
         xDocumentInfo.setUserFieldValue((short) 0, remoteUrl);

         // XStorable xStorable = (XStorable)UnoRuntime.queryInterface(
         // XStorable.class, xComponent);
         // xStorable.store();

      }
      catch (Exception exc)
      {
         LOG.info("Can't store info to opened file...");
      }

      return (xComponent);
   }

}
