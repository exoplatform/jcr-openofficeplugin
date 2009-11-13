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

import org.w3c.dom.Node;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class FilterType
   extends XmlConfig
{

   public static final String XML_LOCALIZEDNAME = "localized-name";

   public static final String XML_APINAME = "api-name";

   public static final String XML_FILEEXTENSION = "file-extension";

   public static final String XML_MIMETYPE = "mime-type";

   private String documentName;

   private String localizedName;

   private String apiName;

   private String fileExtension;

   private String mimeType;

   public FilterType(Node filterNode, String documentName)
   {
      this.documentName = documentName;

      localizedName = getChildNode(filterNode, XML_LOCALIZEDNAME).getTextContent();
      apiName = getChildNode(filterNode, XML_APINAME).getTextContent();
      fileExtension = getChildNode(filterNode, XML_FILEEXTENSION).getTextContent();
      mimeType = getChildNode(filterNode, XML_MIMETYPE).getTextContent();
   }

   public String getDocumentName()
   {
      return documentName;
   }

   public String getLocalizedName()
   {
      return localizedName;
   }

   public String getApiName()
   {
      return apiName;
   }

   public String getFileExtension()
   {
      return fileExtension;
   }

   public String getMimeType()
   {
      return mimeType;
   }

}
