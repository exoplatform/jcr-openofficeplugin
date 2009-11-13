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

package org.exoplatform.applications.ooplugin.config;

import java.util.ArrayList;

import org.exoplatform.applications.ooplugin.XmlUtil;
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

public class FilterListLoader
   extends XmlConfig
{

   private static final Log LOG = ExoLogger.getLogger(FilterListLoader.class);

   public static final String FILTERLIST_CONFIG = "/config/filterlist.xml";

   public static final String XML_FILTERLIST = "filter-list";

   public static final String XML_FILTERGROUP = "filter-group";

   public static final String XML_DOCUMENTNAME = "document-name";

   public static final String XML_FILTERS = "filters";

   private ArrayList<FilterType> loadedFilters = new ArrayList<FilterType>();

   public FilterListLoader()
   {
      try
      {
         Document document = getDocumentFromResource(FILTERLIST_CONFIG);
         Node rootNode = getChildNode(document, XML_FILTERLIST);

         NodeList fileTypes = rootNode.getChildNodes();
         for (int i = 0; i < fileTypes.getLength(); i++)
         {
            Node fileType = fileTypes.item(i);

            if ((fileType.getLocalName() == null) || !XML_FILTERGROUP.equals(fileType.getLocalName()))
            {
               continue;
            }

            readFilterGroup(fileType);
         }

      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception ", exc);
      }
   }

   protected void readFilterGroup(Node groopNode) throws Exception
   {
      Node documentNameNode = XmlUtil.getChildNode(groopNode, XML_DOCUMENTNAME);
      String documentName = documentNameNode.getTextContent();

      Node filtersNode = XmlUtil.getChildNode(groopNode, XML_FILTERS);
      NodeList filters = filtersNode.getChildNodes();
      for (int i = 0; i < filters.getLength(); i++)
      {
         Node filterNode = filters.item(i);

         if (filterNode.getLocalName() == null)
         {
            continue;
         }

         FilterType filter = new FilterType(filterNode, documentName);
         loadedFilters.add(filter);
      }
   }

   public ArrayList<FilterType> getFilterTypes(String groupName)
   {
      ArrayList<FilterType> types = new ArrayList<FilterType>();

      for (int i = 0; i < loadedFilters.size(); i++)
      {
         FilterType curType = loadedFilters.get(i);
         if (groupName.equals(curType.getDocumentName()))
         {
            types.add(curType);
         }
      }

      return types;
   }

   public ArrayList<FilterType> getAllFilters()
   {
      return loadedFilters;
   }

}
