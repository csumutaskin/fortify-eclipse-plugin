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
 
# How to use this plugin:

Copy the .jar file in target folder into Eclipse IDE installation path -> dropins folder and start Eclipse. Fortify custom views and menu items will be visible afterwards.


 
 
 
 
