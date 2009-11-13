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

package org.exoplatform.applications.ooplugin.dialog;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class Component
{

   public static final int XTYPE_XBUTTON = 1;

   public static final int XTYPE_XCOMBOBOX = 2;

   public static final int XTYPE_XLISTBOX = 3;

   private String className = "";

   private String handler = "";

   private ArrayList<ComponentProperty> properties = new ArrayList<ComponentProperty>();

   public Component(String className, String handler)
   {
      this.className = className;
      this.handler = handler;
   }

   public String getClassName()
   {
      return className;
   }

   public String getHandler()
   {
      return handler;
   }

   public ArrayList<ComponentProperty> getProperties()
   {
      return properties;
   }

   public String getPropertyValue(String propertyName)
   {
      for (int i = 0; i < properties.size(); i++)
      {
         ComponentProperty property = properties.get(i);
         if (property.getName().equals(propertyName))
         {
            return property.getValue();
         }
      }
      return "";
   }

}
