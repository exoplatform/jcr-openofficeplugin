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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.exoplatform.applications.ooplugin.WebDavConstants;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class DocumentManager
{

   private static Log LOG = ExoLogger.getLogger(DocumentManager.class);

   protected static String[][] availableDocuments =
   {
   {WebDavConstants.StreamDocs.MULTISTATUS, "org.exoplatform.applications.ooplugin.client.Multistatus"},};

   public static DocumentApi getResponseDocument(InputStream inStream)
   {
      if (inStream == null)
      {
         return null;
      }

      Document document = null;
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         document = builder.parse(inStream);

         NodeList nodeList = document.getChildNodes();
         for (int i = 0; i < nodeList.getLength(); i++)
         {
            Node curDocumentNode = nodeList.item(i);

            String localName = curDocumentNode.getLocalName();
            String nameSpace = curDocumentNode.getNamespaceURI();

            if (localName != null && WebDavConstants.Dav.NAMESPACE.equals(nameSpace))
            {

               for (int docI = 0; docI < availableDocuments.length; docI++)
               {
                  if (localName.equals(availableDocuments[docI][0]))
                  {
                     DocumentApi responseDoc = (DocumentApi) Class.forName(availableDocuments[docI][1]).newInstance();
                     responseDoc.initFromDocument(document);
                     return responseDoc;
                  }
               }

            }
         }

      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception. ", exc);
         exc.printStackTrace();
      }

      return null;
   }

}
