readme.txt


eXo WebDav Open Office Add-On


1. Intro

eXo WebDav Open Office Add-On is a library written on Java, that allows to use WebDav service from 
any other application, an example Open Office Applications (add-on for OpenOffice.org Writer, Calc, 
Draw, Base, Impress, Math) with the user interface based on this library. 

Library allows :
 - Load .doc, .xls, .odt, .ods, .odp, .odj and other types of files from JCR repository into 
 		Open Office; 
 - Edit and Save files onto repository; 
 - Making full-text repository search;  
 - View and Edit some version of file. 



2. Installation

First of all, you need to Open Office (v. 2.0.2 or upper) and JRE (v. 5.0 or upper) installed 
on your system.

If this conditions are true, you can install Open Office add-on.

a.) Launch any Open Office application.
b.) Open "Tools / Extension Manager" menu.
c.) Choose "My Extensions", if you have previos version of exo-oo-addon choose it and click "Remove".
d.) Click "Add" button and choose path to your eXo Addon *.zip file click "open".
e.) Restart your application.

3. Configuration

Before opening any documents, you need to configure your connection information. You can do this by
using a "Settings" dialog window. Use a "Test connection" button to make sure You have entered the 
right settings.


* Please, be attentive when You enter username and password. They can vary, depends on Your server 
settings. Default login and pass for eXo JCR standalone based server is admin/admin, 
eXo ECM based server - exoadmin/exo@ecm, etc. Also note, that the different application servers 
use a various default port numbers - 8080 for Tomcat and JBoss, 9000 for JOnAS...


4. Using

When configuration is done, you can start browsing your documents. Click "Open" from the 
"Remote Documents" menu, and you can browse repository using browse window.
For opening any file, click twice on it at file list, or select the file and press 
"Open" button on the form.

If you have opened file from the repository, and want to save any changes, you can use both 
"Save" and "Save As" buttons. The difference is the "Save" puts document at the same place, 
and "Save As" allows you to save file into another folder.


For browsing document versions history use a "Versions" button. 
You can also make a full-text search at repository. Press the "Search" menu button, put keywords 
to search in search text field and press "Search" button. Results will appear in the file list. 


5. Uninstallation

For uninstallation type in your console "unopkg add exo-oo-addon-1.7.zip" and run.

a.) Open "Tools / Extension Manager" menu.
b.) Choose "My Extensions", choose exo-oo-addon-* and click "Remove".
c.) Restart your application.
