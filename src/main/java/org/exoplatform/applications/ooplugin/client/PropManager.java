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

import org.exoplatform.applications.ooplugin.WebDavConstants;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.w3c.dom.Node;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class PropManager
{

   private static Log LOG = ExoLogger.getLogger(PropManager.class);

   protected static String[][] availableProperties =
            {
                     {WebDavConstants.WebDavProp.DISPLAYNAME,
                              "org.exoplatform.applications.ooplugin.props.DisplayNameProp"},
                     {WebDavConstants.WebDavProp.RESOURCETYPE,
                              "org.exoplatform.applications.ooplugin.props.ResourceTypeProp"},
                     {WebDavConstants.WebDavProp.GETCONTENTLENGTH,
                              "org.exoplatform.applications.ooplugin.props.ContentLengthProp"},
                     {WebDavConstants.WebDavProp.GETLASTMODIFIED,
                              "org.exoplatform.applications.ooplugin.props.LastModifiedProp"},
                     {WebDavConstants.WebDavProp.VERSIONNAME,
                              "org.exoplatform.applications.ooplugin.props.VersionNameProp"},
                     {WebDavConstants.WebDavProp.CREATORDISPLAYNAME,
                              "org.exoplatform.applications.ooplugin.props.CreatorDisplayNameProp"},
                     {WebDavConstants.WebDavProp.CREATIONDATE,
                              "org.exoplatform.applications.ooplugin.props.CreationDateProp"}};

   public static PropApi getPropertyByNode(Node propertyNode, String httpStatus)
   {
      try
      {
         String nodeName = propertyNode.getLocalName();
         if (!propertyNode.getNamespaceURI().equals(WebDavConstants.Dav.NAMESPACE))
         {
            nodeName = propertyNode.getNodeName();
         }

         PropApi curProp = null;

         for (int i = 0; i < availableProperties.length; i++)
         {
            if (nodeName.equals(availableProperties[i][0]))
            {
               curProp = (PropApi) Class.forName(availableProperties[i][1]).newInstance();
               break;
            }
         }

         if (curProp == null)
         {
            curProp = new CommonProp(nodeName);
         }
         curProp.setStatus(httpStatus);
         curProp.init(propertyNode);
         return curProp;
      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception. ", exc);
      }
      return null;
   }

}
