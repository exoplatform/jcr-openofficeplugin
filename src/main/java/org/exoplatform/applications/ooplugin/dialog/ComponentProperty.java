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

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class ComponentProperty
{

   public static final String TYPE_INTEGER = "integer";

   public static final String TYPE_SHORT = "short";

   public static final String TYPE_STRING = "string";

   public static final String TYPE_BOOLEAN = "boolean";

   public static final String TYPE_IMAGE = "image";

   public static final String TYPE_FONTDESCRIPTOR = "fontdescriptor";

   private String name = "";

   private String type = "";

   private String value = "";

   public ComponentProperty(String name, String type, String value)
   {
      this.name = name;
      this.type = type;
      this.value = value;
   }

   public String getName()
   {
      return name;
   }

   public String getType()
   {
      return type;
   }

   public String getValue()
   {
      return value;
   }

   public boolean isType(String type)
   {
      if (this.type.equals(type))
      {
         return true;
      }
      return false;
   }

}
