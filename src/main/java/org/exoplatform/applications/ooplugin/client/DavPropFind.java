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
import org.exoplatform.services.rest.ExtHttpHeaders;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class DavPropFind
   extends MultistatusCommand
{

   private int depth = 0;

   public DavPropFind(WebDavContext context) throws Exception
   {
      super(context);
      commandName = WebDavConstants.DavCommand.PROPFIND;
      xmlName = WebDavConstants.StreamDocs.PROPFIND;

      client.setRequestHeader("connection", "TE");
      client.setRequestHeader("te", "trailers");
      client.setRequestHeader("content-type", "application/xml");
   }

   public void setDepth(int depth)
   {
      this.depth = depth;
   }

   @Override
   public int execute() throws Exception
   {
      client.setRequestHeader(ExtHttpHeaders.DEPTH, "" + depth);
      return super.execute();
   }

}
