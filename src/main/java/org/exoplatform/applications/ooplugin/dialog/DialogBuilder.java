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

import org.exoplatform.applications.ooplugin.PlugInDialog;
import org.exoplatform.applications.ooplugin.Resources;
import org.exoplatform.applications.ooplugin.config.XmlConfig;
import org.exoplatform.applications.ooplugin.events.ActionListener;
import org.exoplatform.applications.ooplugin.events.ItemListener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.star.awt.FontDescriptor;
import com.sun.star.awt.FontWeight;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XComboBox;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XItemListener;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaly Guly</a>
 * @version $Id: $
 */

public class DialogBuilder
   extends XmlConfig
{

   public static final String DIALOG_CONFIG = "/config/dialogconfig.xml";

   public static final String XML_DIALOGCONFIG = "dialogconfig";

   public static final String XML_DIALOG = "dialog";

   public static final String XML_NAME = "name";

   public static final String XML_COMPONENTS = "components";

   public static final String XML_COMPONENT = "component";

   public static final String XML_CLASS = "class";

   public static final String XML_HANDLER = "handler";

   public static final String XML_PROPERTIES = "properties";

   public static final String XML_PROPERTY = "property";

   public static final String XML_TYPE = "type";

   public static final String XML_VALUE = "value";

   private ArrayList<DialogModel> dialogs = new ArrayList<DialogModel>();

   private static final Log LOG = ExoLogger.getLogger(DialogBuilder.class);

   private PlugInDialog plugInDialog;

   private XComponentContext xComponentContext;

   private XFrame xFrame;

   private XToolkit xToolkit;

   public DialogBuilder(PlugInDialog plugInDialog, XFrame xFrame, XToolkit xToolkit)
   {
      this.plugInDialog = plugInDialog;
      this.xComponentContext = plugInDialog.getConponentContext();
      this.xFrame = xFrame;
      this.xToolkit = xToolkit;
   }

   public Object createDialog(String dialogName, ArrayList<EventHandler> eventHandlers) throws Exception
   {
      DialogModel xmlDialog = null;
      for (int i = 0; i < dialogs.size(); i++)
      {
         DialogModel curDialog = dialogs.get(i);
         if (dialogName.equals(curDialog.getDialogName()))
         {
            xmlDialog = curDialog;
            break;
         }
      }

      if (xmlDialog == null)
      {
         return null;
      }

      XMultiComponentFactory xMultiComponentFactory = xComponentContext.getServiceManager();
      Object dialogModel =
               xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialogModel",
                        xComponentContext);
      XPropertySet xPSetDialog = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, dialogModel);

      ArrayList<ComponentProperty> properties = xmlDialog.getProperties();
      for (int i = 0; i < properties.size(); i++)
      {
         ComponentProperty property = properties.get(i);
         setProperty(xPSetDialog, property);
      }

      XMultiServiceFactory xMultiServiceFactory =
               (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, dialogModel);

      XNameContainer xNameCont = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class, dialogModel);

      ArrayList<Component> components = xmlDialog.getComponents();
      for (int i = 0; i < components.size(); i++)
      {
         Component component = components.get(i);

         Object componentModel = xMultiServiceFactory.createInstance(component.getClassName());
         XPropertySet propertySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, componentModel);

         ArrayList<ComponentProperty> compProperties = component.getProperties();
         for (int propi = 0; propi < compProperties.size(); propi++)
         {
            ComponentProperty property = compProperties.get(propi);
            setProperty(propertySet, property);
         }

         xNameCont.insertByName(component.getPropertyValue("Name"), componentModel);
      }

      Object dialog =
               xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.UnoControlDialog", xComponentContext);
      XControl xControl = (XControl) UnoRuntime.queryInterface(XControl.class, dialog);
      XControlModel xControlModel = (XControlModel) UnoRuntime.queryInterface(XControlModel.class, dialogModel);
      xControl.setModel(xControlModel);

      XControlContainer xControlCont = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, dialog);
      plugInDialog.setControlContainer(xControlCont);

      for (int i = 0; i < eventHandlers.size(); i++)
      {
         EventHandler eventHandler = eventHandlers.get(i);

         Object listener = eventHandler.getListener();
         Object compObject = xControlCont.getControl(eventHandler.getComponentName());

         switch (eventHandler.getComponentType())
         {

            case Component.XTYPE_XBUTTON :
               XButton xButtonObject = (XButton) UnoRuntime.queryInterface(XButton.class, compObject);

               if (listener instanceof ActionListener)
               {
                  xButtonObject.addActionListener((XActionListener) listener);
               }

               break;

            case Component.XTYPE_XCOMBOBOX :
               XComboBox xComboBox = (XComboBox) UnoRuntime.queryInterface(XComboBox.class, compObject);

               if (listener instanceof ActionListener)
               {
                  xComboBox.addActionListener((XActionListener) listener);
               }
               else if (listener instanceof ItemListener)
               {
                  xComboBox.addItemListener((XItemListener) listener);
               }

               break;

            case Component.XTYPE_XLISTBOX :
               XListBox xListBox = (XListBox) UnoRuntime.queryInterface(XListBox.class, compObject);
               if (listener instanceof XActionListener)
               {
                  xListBox.addActionListener((XActionListener) listener);
               }

               break;
         }
      }

      Object toolkit =
               xMultiComponentFactory.createInstanceWithContext("com.sun.star.awt.ExtToolkit", xComponentContext);
      xToolkit = (XToolkit) UnoRuntime.queryInterface(XToolkit.class, toolkit);
      XWindow xWindow = (XWindow) UnoRuntime.queryInterface(XWindow.class, xControl);
      xWindow.setVisible(false);
      xControl.createPeer(xToolkit, null);
      return dialog;
   }

   private void setProperty(XPropertySet propertySet, ComponentProperty property) throws Exception
   {
      if (property.isType(ComponentProperty.TYPE_STRING))
      {
         propertySet.setPropertyValue(property.getName(), property.getValue());
         return;
      }

      if (property.isType(ComponentProperty.TYPE_INTEGER))
      {
         Integer intValue = new Integer(property.getValue());
         propertySet.setPropertyValue(property.getName(), intValue);
         return;
      }

      if (property.isType(ComponentProperty.TYPE_SHORT))
      {
         Short shortValue = new Short(property.getValue());
         propertySet.setPropertyValue(property.getName(), shortValue);
         return;
      }

      if (property.isType(ComponentProperty.TYPE_BOOLEAN))
      {
         Boolean booleanValue = new Boolean(property.getValue());
         propertySet.setPropertyValue(property.getName(), booleanValue);
         return;
      }

      if (property.isType(ComponentProperty.TYPE_IMAGE))
      {
         String imagePath = "file:///" + Resources.getImage(property.getValue());
         propertySet.setPropertyValue(property.getName(), imagePath);
         return;
      }

      if (property.isType(ComponentProperty.TYPE_FONTDESCRIPTOR))
      {
         String fontValue = property.getValue();
         String[] fontsValues = fontValue.split(":");

         FontDescriptor fontDescriptor = new FontDescriptor();
         fontDescriptor.Name = fontsValues[0];
         fontDescriptor.CharacterWidth = new Float(fontsValues[1]);
         fontDescriptor.WordLineMode = true;

         if (fontsValues.length > 2)
         {
            fontDescriptor.Weight = FontWeight.BOLD;
         }

         propertySet.setPropertyValue(property.getName(), fontDescriptor);
      }
   }

   public void init()
   {
      try
      {
         Document document = getDocumentFromResource(DIALOG_CONFIG);

         Node dialogConfigNode = getChildNode(document, XML_DIALOGCONFIG);

         NodeList nodes = dialogConfigNode.getChildNodes();
         for (int i = 0; i < nodes.getLength(); i++)
         {
            Node curNode = nodes.item(i);
            if (XML_DIALOG.equals(curNode.getLocalName()))
            {
               parseDialog(curNode);
            }
         }

      }
      catch (Exception exc)
      {
         LOG.info("Unhandled exception: " + exc.getMessage(), exc);
      }
   }

   private void parseDialog(Node dialogNode)
   {
      Node nameNode = getChildNode(dialogNode, XML_NAME);

      DialogModel dialog = new DialogModel(nameNode.getTextContent());

      Node propertiesNode = getChildNode(dialogNode, XML_PROPERTIES);
      NodeList properties = propertiesNode.getChildNodes();
      for (int i = 0; i < properties.getLength(); i++)
      {
         Node curNode = properties.item(i);
         if (XML_PROPERTY.equals(curNode.getLocalName()))
         {
            NamedNodeMap nnm = curNode.getAttributes();

            String propertyName = nnm.getNamedItem(XML_NAME).getTextContent();
            String propertyType = nnm.getNamedItem(XML_TYPE).getTextContent();
            String propertyValue = nnm.getNamedItem(XML_VALUE).getTextContent();

            ComponentProperty property = new ComponentProperty(propertyName, propertyType, propertyValue);
            dialog.getProperties().add(property);
         }
      }

      Node componentsNode = getChildNode(dialogNode, XML_COMPONENTS);
      NodeList components = componentsNode.getChildNodes();
      for (int i = 0; i < components.getLength(); i++)
      {
         Node curNode = components.item(i);
         if (XML_COMPONENT.equals(curNode.getLocalName()))
         {
            Component component = parseComponent(curNode);
            dialog.getComponents().add(component);
         }
      }

      dialogs.add(dialog);
   }

   private Component parseComponent(Node componentNode)
   {
      Node classNode = getChildNode(componentNode, XML_CLASS);

      String handler = "";
      Node handlerNode = getChildNode(componentNode, XML_HANDLER);
      if (handlerNode != null)
      {
         handler = handlerNode.getTextContent();
      }

      Component component = new Component(classNode.getTextContent(), handler);

      NodeList properties = componentNode.getChildNodes();
      for (int i = 0; i < properties.getLength(); i++)
      {
         Node curNode = properties.item(i);
         if (XML_PROPERTY.equals(curNode.getLocalName()))
         {

            NamedNodeMap nnm = curNode.getAttributes();

            String propertyName = nnm.getNamedItem(XML_NAME).getTextContent();
            String propertyType = nnm.getNamedItem(XML_TYPE).getTextContent();
            String propertyValue = nnm.getNamedItem(XML_VALUE).getTextContent();

            ComponentProperty property = new ComponentProperty(propertyName, propertyType, propertyValue);
            component.getProperties().add(property);
         }
      }

      return component;
   }

}
