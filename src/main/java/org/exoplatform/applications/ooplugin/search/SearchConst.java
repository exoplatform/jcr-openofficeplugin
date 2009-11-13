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

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class SearchConst
{

   public static final String XPATH_NAMESPACE = "XPATH:";

   public static final String XPATH_PREFIX = "X:";

   public static final String SQL_NAMESPACE = "SQL:";

   public static final String SQL_PREFIX = "S:";

   // <exo:sql xmlns:exo="http://exoplatform.com/jcr"/>
   public static final String SQL_SUPPORT = "sql";

   // <exo:xpath xmlns:exo="http://exoplatform.com/jcr"/>
   public static final String XPATH_SUPPORT = "xpath";

   public static final String NOT_TAG = "not";

   public static final String AND_TAG = "and";

   public static final String OR_TAG = "or";

   public static final String LIKE_TAG = "like";

   public static final String EQ_TAG = "eq";

   public static final String LITERAL_TAG = "literal";

}
