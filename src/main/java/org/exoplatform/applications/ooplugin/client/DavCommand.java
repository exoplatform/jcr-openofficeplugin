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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.services.rest.ExtHttpHeaders;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSOutput;

/**
 * Created by The eXo Platform SAS Author.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public abstract class DavCommand
{

   public static final String AUTH_BASIC = "Basic";

   protected String commandName;

   protected WebDavContext context = null;

   protected String resourcePath;

   private long rangeStart = -1;

   private long rangeEnd = -1;

   protected HttpClient client;

   protected byte[] requestDataBytes = null;

   protected InputStream inStream = null;

   protected byte[] responseDataBytes = null;

   private boolean enableXml = true;

   public DavCommand(WebDavContext context) throws Exception
   {
      this.context = context;
      client = new HttpClient(context.getHost(), context.getPort());
   }

   public void setResourcePath(String resourcePath)
   {
      this.resourcePath = resourcePath;
   }

   public void setRange(int rangeStart)
   {
      this.rangeStart = rangeStart;
   }

   public void setRange(int rangeStart, int rangeEnd)
   {
      this.rangeStart = rangeStart;
      this.rangeEnd = rangeEnd;
   }

   public void setRequestDataBuffer(byte[] requestDataBytes)
   {
      this.requestDataBytes = requestDataBytes;
   }

   public void setRequestInputStream(InputStream inStream, long streamLength) throws Exception
   {
      this.inStream = inStream;
      client.setRequestHeader(ExtHttpHeaders.CONTENTLENGTH, "" + streamLength);
   }

   public void setXmlEnabled(boolean enableXml)
   {
      this.enableXml = enableXml;
   }

   public void setLockToken(String lockToken) throws Exception
   {
      client.setRequestHeader(ExtHttpHeaders.LOCKTOKEN, "<" + lockToken + ">");
   }

   public String getResponseHeader(String headerName)
   {
      return client.getResponseHeader(headerName);
   }

   public ArrayList<String> getResponseHeadersNames()
   {
      return client.getResponseHeadersNames();
   }

   public byte[] getResponseDataBuffer()
   {
      return client.getResponseBytes();
   }

   private static Document getDomDocument() throws Exception
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.newDocument();
   }

   public int execute() throws Exception
   {
      if (enableXml)
      {
         Document xmlDocument = getDomDocument();
         Element rootElement = toXml(xmlDocument);
         if (rootElement != null)
         {
            serializeElement(rootElement);
         }
      }

      if (inStream != null)
      {
         client.setRequestStream(inStream);
      }
      else
      {
         if (requestDataBytes != null)
         {
            client.setRequestBody(requestDataBytes);
         }
      }

      client.setHttpCommand(commandName);

      String path = context.getServletPath();
      if (resourcePath != null)
      {
         path += resourcePath;
      }

      client.setRequestPath(path);

      if (context.getUserId() != null)
      {
         String userId = context.getUserId();
         String userPass = context.getUserPass();

         byte[] encoded = Base64.encodeBase64(new String(userId + ":" + userPass).getBytes());
         String encodedAuth = new String(encoded);
         client.setRequestHeader(ExtHttpHeaders.AUTHORIZATION, AUTH_BASIC + " " + encodedAuth);
      }

      if (rangeStart >= 0)
      {
         String rangeHeader = "bytes=" + rangeStart + "-";
         if (rangeEnd >= 0)
         {
            rangeHeader += rangeEnd;
         }
         client.setRequestHeader(ExtHttpHeaders.RANGE, rangeHeader);
      }

      client.conect();
      int status = client.execute();
      finalExecute();
      return status;
   }

   public void finalExecute()
   {
   }

   public Element toXml(Document xmlDocument)
   {
      return null;
   }

   private void serializeElement(Element element) throws Exception
   {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();

      DOMSource source = new DOMSource(element.getOwnerDocument());

      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      StreamResult resultStream = new StreamResult(outStream);

      transformer.transform(source, resultStream);
      requestDataBytes = outStream.toByteArray();
   }

   static class Output
      implements LSOutput
   {

      OutputStream bs;

      Writer cs;

      String sId;

      String enc;

      public Output()
      {
         bs = null;
         cs = null;
         sId = null;
         enc = "UTF-8";
      }

      public OutputStream getByteStream()
      {
         return bs;
      }

      public void setByteStream(OutputStream byteStream)
      {
         bs = byteStream;
      }

      public Writer getCharacterStream()
      {
         return cs;
      }

      public void setCharacterStream(Writer characterStream)
      {
         cs = characterStream;
      }

      public String getSystemId()
      {
         return sId;
      }

      public void setSystemId(String systemId)
      {
         sId = systemId;
      }

      public String getEncoding()
      {
         return enc;
      }

      public void setEncoding(String encoding)
      {
         enc = encoding;
      }
   }

}
