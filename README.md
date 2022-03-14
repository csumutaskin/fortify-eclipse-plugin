# a Custom Eclipse Fortify Plugin

# Introduction

Fortify SCA is a tool that helps developers by making static security code analysis and detecting possible vulnerabilities that may cause problems in a live environment. This tool certainly increases the code quality of an enterprise and allows a software development team to fix security bugs before experiencing really big problems. The software I mentioned above is licenced by the [Micro Focus](https://www.microfocus.com/) Company and their enterprise solutions actually contain plugins for I.D.E.s as well. 

What I provided here is a custom Eclipe I.D.E plugin that facilitated "my way of implementation". This plugin itself is useless without **"a locally installed and licenced Fortify SCA on Windows O.S."** product. I -kind of- tried to convert the issues detected by the Fortify tool into a very simple check list, like we do when we go to shopping, nothing more. While implementing the solution, as I stated earlier, I tried to practice my rusty knowhow on SWT components and plugin development.

I repeat myself again: "You have to have a physical access to a computer on which Fortify SCA software is locally installed and licenced. Otherwise, this plugin is nothing more but garbage.

# Prerequeisites:
 * A Windows (10 or older but still useful) Operating System with Fortify SCA is installed (and a Windows O.S. User that is authorized to create folders and files).
 * Eclipse IDE (Plugin is an Eclipse Plugin)
 * This plugin runs an O.S. command "sourceanalyzer" of Fortify in the background (run 'sourceanalyzer -h' on command line to see if the utility exists) and to create pdf reports Fortify's "ReportGenerator" utility are used. So make sure that Fortify Installation path / bin (ReportGenerator.bat is located there) is set as a Windows PATH variable.
 * Internet connection for recommendations of the issues. 
 * I mostly develop backend applications in Java, so I parse the issues using the ".java" token especially when detecting the issue locations. This means current tests are done only for Java Backend Projects.
 
# How to install this plugin:

Copy the .jar file in target folder (I excluded target folder and .jar files from .gitignore for a quick installation of the plugin) into Eclipse IDE installation path -> dropins folder and start Eclipse. Fortify custom views and menu items will be visible afterwards.

# How to use this plugin:

This plugin is used to scan a java project base (not individual files in a project) in Eclipse. There are 2 types of scans. 

1- On the fly scan -> uses sourceanalyzer command in the background. Issues detected are written to the custom views in the plugin with brief content (sourceanalyzer is mostly used to log issues summary in devops pipelines, the same utility is used for on the fly scan).
2- Scan to PDF -> More detailed scan, scan is made and issues along with abstract and reccomendation tips to fix these issues are written to a .pdf file on developer's desktop. (.fpr file is also written to the desktop). But make sure that Fortify SCA/bin is put among Windows PATH variables because ReportGenerator utility is used to create the pdf report.

A scan can be triggered in 2 ways:

1- From the top menu bar There is a new 'Fortify' menu item that will open a sub menu in which scans can be triggered (Both on the fly and pdf output scans are triggered here)

![Trigger Scan Way 1](https://user-images.githubusercontent.com/16647815/158249539-6a84fcd8-2165-4390-b359-5adbe3885f16.png)

![Sub Menu of Top Fortify Menu Item](https://user-images.githubusercontent.com/16647815/158249834-90817afe-295b-4b8d-9787-e3c5eedf6288.png)

Ctrl + 4 is also a keyboard shortcut to trigger on the fly scan
Ctrl + 5 is also a keyboard shortcut to trigger a pdf outputted scan (detailed)

2- An on the fly scan can also be triggered using a shortcut button (with Fortify icon) on the toolbar located just below the top menu bar:

![Toolbar Fortify On the fly Scan Button](https://user-images.githubusercontent.com/16647815/158250483-91c51de2-9263-4339-88cf-0e54d5657bf9.png)




 
 
 
 
