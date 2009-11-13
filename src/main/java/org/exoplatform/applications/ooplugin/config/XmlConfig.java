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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class XmlConfig
{

   protected Document getDocumentFromResource(String resourceName) throws Exception
   {
      InputStream inputStream = getClass().getResourceAsStream(resourceName);
      return getDocumentFromInputStream(inputStream);
   }

   protected Document getDocumentFromInputStream(InputStream in) throws Exception
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(in);
      return document;
   }

   protected Node getChildNode(Node node, String childName)
   {
      NodeList nodes = node.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++)
      {
         Node curNode = nodes.item(i);
         if (curNode.getLocalName() != null && curNode.getLocalName().equals(childName))
         {
            return curNode;
         }
      }
      return null;
   }

}
