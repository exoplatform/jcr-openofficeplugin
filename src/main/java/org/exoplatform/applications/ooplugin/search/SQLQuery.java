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

package org.exoplatform.applications.ooplugin.search;

import org.exoplatform.applications.ooplugin.WebDavConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class SQLQuery
   implements DavQuery
{

   private String query = "";

   public SQLQuery()
   {
   }

   public SQLQuery(String query)
   {
      this.query = query;
   }

   public void setQuery(String query)
   {
      this.query = query;
   }

   public Element toXml(Document xmlDocument)
   {
      Element sqlElement =
               xmlDocument.createElementNS(WebDavConstants.Dav.NAMESPACE, WebDavConstants.Dav.PREFIX + "sql");
      sqlElement.setTextContent(query);
      return sqlElement;
   }

}
