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

* On the fly scan -> uses sourceanalyzer command in the background. Issues detected are written to the custom views in the plugin with brief content (sourceanalyzer is mostly used to log issues summary in devops pipelines, the same utility is used for on the fly scan).
* Scan to PDF -> More detailed scan, scan is made and issues along with abstract and reccomendation tips to fix these issues are written to a .pdf file on developer's desktop. (.fpr file is also written to the desktop). But make sure that Fortify SCA/bin is put among Windows PATH variables because ReportGenerator utility is used to create the pdf report.

A scan can be triggered in 2 ways:

## Quick Scan (On the fly Scan)

1- From the top menu bar There is a new 'Fortify' menu item that will open a sub menu in which scans can be triggered (Both on the fly and pdf output scans are triggered here)

![Trigger Scan Way 1](https://user-images.githubusercontent.com/16647815/158249539-6a84fcd8-2165-4390-b359-5adbe3885f16.png)

![Sub Menu of Top Fortify Menu Item](https://user-images.githubusercontent.com/16647815/158249834-90817afe-295b-4b8d-9787-e3c5eedf6288.png)

Ctrl + 4 is also a keyboard shortcut to trigger on the fly scan
Ctrl + 5 is also a keyboard shortcut to trigger a pdf outputted scan (detailed)

2- An on the fly scan can also be triggered using a shortcut button (with Fortify icon) on the toolbar located just below the top menu bar:

![Toolbar Fortify On the fly Scan Button](https://user-images.githubusercontent.com/16647815/158250483-91c51de2-9263-4339-88cf-0e54d5657bf9.png)

On the fly Scan Results are outputted to a custom view that the plugin provides (When one of the on the fly scan button/links is triggered, this view is automatically opened) In case a manual operation is needed to open the issue output view, use the: Window -> Show View -> Other -> Fortify flow. There will be 3 custom views provided by this plugin.

![Fortify Plugin Custom Views](https://user-images.githubusercontent.com/16647815/158254803-c1a3f901-c7af-435e-ad47-ab61c5a09ae1.png)

When on the fly scan is triggered user is prompted to choose among one of the projects listed in the current workspace:

![image](https://user-images.githubusercontent.com/16647815/158255769-d77a9a6d-c104-43e5-b07b-0e1cc2d8679c.png)

After choosing the project, sourceanalyzer utility runs in the background as an O.S process, developer has to wait until the scan is finished (I plan to add a progress bar of the scan, but the plugin currently does not have it) Whenever the scan is complete, a message dialog is shown to tell the end user that the "scan is done". The issues are outputted to the Fortify on the fly view.

![Message Dialog of Scan Ended](https://user-images.githubusercontent.com/16647815/158256171-4385db3d-8773-4d2e-ac8a-c849e86f1e0d.png)

An example of how detected issues are listed is as follows:

![On the fly scan results](https://user-images.githubusercontent.com/16647815/158256530-26a8a700-a161-490d-8485-0a1664f26af5.png)

By clicking of any of the table column headers, column values can be sorted in natural order and reverse natural order (ascending and descending by string letter order).
So, to group issues by severity, developer can click the Severity column.

Columns in this table indicate:

ID: Fortify Issue ID
Severity: Issue Criticality
Location: Class and Line Number where the issue is detected (The issue may have a location trace -more than one line- the final line is listed here, detail location trace will
be given in another custom view called "Fortify Issue Trace" provided by this plugin.)
Reason: Category
Description: SubCategory - or more detail
Type: Analyzer tip

When an end user double clicks an item (row) from the Fortify on the fly view the following things happen:
* Fortify Issue Trace prints all the lines that causes the issue (From the very origin line to where this line causes the same side effects)
* If Fortify Issue Trace contains more than one line as a location trace any of the lines can be double clicked by the developer and the cursor is automatically located at the double clicked location (if the java file is not opened before, it is automatically opened by the plugin).
* Eclipse editor automatically opens the file and the line number the issue is detected and focuses the line cursor on that line.
* Third custom view of this plugin called "Fortify Taxonomy" shows the abstract and the recomendation tip to fix the issue.

![Double Click a Row In Issue Table](https://user-images.githubusercontent.com/16647815/158259486-f5c7c829-7927-40fb-8569-52c39f3221c9.png)

Abstract and Recommendation Tips in Fortify Taxonomy is shown as below:

![Fortify Taxonomy](https://user-images.githubusercontent.com/16647815/158259805-5f423b8b-957b-418e-bed5-e125d6804d45.png)

## Detailed Report Scan (with .fpr and .pdf Output)

If you choose the "Scan To PDF" Sub menu from the top bar Fortify Menu Link, then the same Dialog as "on the fly scan" is shown to the end user, to choose the project to be scanned, and finally .fpr file and the human readable report output is generated on the Desktop with Project Name. The pdf file already contains the issues with issue abstract
and recommendation to fix them. 
 
# Issue Ignore List
Not all the issues are applicable always. In some scenarios, although there is a security issue detected, because of the origin of the project, it is not a really security issue. In those scenarios, the developer might want to exclude the issue from the output list. Ignore Rule utility comes into the scene. "Ignore List Utility" is, as the name states, if you do not want to see that issue in your issue output report, it is simply omitted (Omitted only for "on the fly scan"). 

There are 4 ways to ignore an issue. All of them are triggered when you right click on any line on the Fortify on the fly custom view. A right mouse click menu appears:

![Ignore List Right Click Menu](https://user-images.githubusercontent.com/16647815/158262205-6793d519-b6ba-4a95-827e-c4bf881d1790.png)

The explanation of each item on this right click menu is as follows:

* Ignore Line Just Once: The line (with that unique ID) is deleted immediately but not added to a permanent file store. So, if a re-scan is made for the project, the issue appears again on the fly table as a row. If a developer wants to focus on only for some issues but not the others, he/she can use this flow for temporary.
* Ignore Category Just Once: Similar as above, but this time instead of ignoring and hiding only the chosen line, all the lines that match the clicked line's category and subcategory will be hided. e.g. If the clicked line contains Reason: System Information Leak and Subcategory: Internal, all lines that contain this category are hided immediately. But if a rescan is done on the same project these issues reappear again.
* Ignore Category for this Workspace Projects only: There are 2 permanent stores for this plugin. One is workspace wide, the other is O.S user wide. If the user chooses this flow, the category and subcategory (Reason and Description columns) is added to the Workspace wide db store as a line permanently and any other project scans in the same workspace and rescans on the workspace projects never output the issue to the end user, until it is removed.
* Ignore Category for every Java Project in this computer: Similar as above, when the category is added to user wide store, no other project scan on this computer (for the O.S user) outputs this issue until it is removed from ignored list. 

The issues that are put to ignore list (workspace wide and user wide) can be seen using the Top Menu Bar: Fortify Link -> Ignored Rules (or by hitting the Ctrl+6 hotkey) and using the same dialog these rules are reverted back to active position: Here are 2 pictures that show how the ignore list can be seen and rules can be reverted back to active position.

![How to Open Ignored Rules List](https://user-images.githubusercontent.com/16647815/158265006-e4602438-71d3-4a7a-88c3-63642ef56c67.png)

![Ignore List](https://user-images.githubusercontent.com/16647815/158265444-6972751f-7428-4cff-b0d9-894612ed485a.png)

(When a scan is made while some rules are in persistent ignore list store, they are never outputted
to the end user if rescans are made.) 
 
