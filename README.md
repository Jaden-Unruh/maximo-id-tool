<<<<<<< HEAD
# Maximo ID Tool
A tool that references a reassessment maximo mapping file to write new maximo ids to an inventory file using the id numbers

## Setup
Java SE 17 is required to run this program. If you don't have Java 17 or a newer version installed, you can download an installer for Temurin/OpenJDK 17 from [here](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.8_7.msi). This is an open-source version of java. Once downloaded, you can run the installer by double-clicking, it will open a window guiding you through the installation. Leaving everything as the defaults and just clicking through the pages should work perfectly.

The `.JAR` for the program is located in the parent directory of the github (see [In the Github](/#in-the-github) for more).

Once Temurin/Java 17 and the program jar are installed, double click the jar to run.

##GUI and How to Use
After double-clicking the `.jar` file, a window titled "Maximo ID tool" will open. It will have 2 prompts, as described below:
1. `Select inventory file (ASPxGridView*.xlsx)`
	* Click on the select button to open a file prompt, navigate to and select the Inventory spreadsheet (name starting with `ASPxGridViewInventory`). Note that this must be an `*.xlsx` file, rather than `*.xlsb` or any other spreadsheet filetype - see [Troubleshooting](/#troubleshooting) for more. The contents of this spreadsheet should be as described under [Files->Inventory](/#inventory).
2. `Select reassessment maximo mapping file (*.xlsx)`
	* Click on the select button to open a file prompt, navigate to and select the reassessment maximo mapping file. Again, this must be an `*.xlsx` file. The contents of this spreadsheet should be as described under [Files->Reassessment Maximo Mapping](/#reassessment-maximo-mapping)

The other contents of the window are the `Close` and `Run` buttons, which are fairly self-explanatory. There is also an info text box, which won't be visible when the program is first run, but as the tool is used will populate with relevant information about what's going on.

## Files
The program requires two spreadsheets to run, as follows:
### Inventory
The first page of this spreadsheet should be a list of inventory items where the 19th column (column S) is the 7-digit id number for the inventory item, and the 20th column (column T) is empty and ready to be populated by the maximo ID. The data should start on the second row of the spreadsheet, where the first is headers. There may be other pages in the spreadsheet file, and any data not in columns 19 and 20 is irrelevant and will be ignored by the program.
### Reassessment Maximo Mapping
This spreadsheet file must have a sheet named exactly `Assets`, case-sensitive. Any other sheets will be ignored. On the `Assets` sheet, the second column (column B) should be temporary asset numbers - 7 digits followed by `NEW`, i.e. `2669627NEW`. The third column (column C) should have the corresponding maximo asset numbers, i.e. AB418012. Data should start on the second row, where the first is headers. Any data not in columns 2 and 3 is irrelevant and will be ignored by the program.

## Troubleshooting
> Nothing's happening when I double click the `.JAR` file

Ensure you've installed Java as specified under [Setup](/#Setup). If you believe you have, try checking your java version:
1. Press Win+R, type `cmd` and press enter - this will open a command prompt window
2. Type `java -version` and press enter
3. If you've installed java as specified, the first line under your typing should read `openjdk version "17.0.8" 2023-07-18`[^2]. If, instead, it says `'java' is not recognized as an internal...` then java is not installed.

[^2]: If you had a version of java other than the one specified in Setup, this may show a different version, but should be similar. However, you probably wouldn't be in this troubleshooting step if this is the case.

---
> I only have spreadsheets of type `*.xlsb` or `*.csv` (or any other spreadsheet type) and the program won't open them

Open the spreadsheets in Microsoft Excel and select 'File -> Save As -> This PC' and choosing 'Excel Workbook (.xlsx)' from the drop-down. A full list of filetypes that Excel supports (and thus can be converted to .xlsx) can be found [here](https://learn.microsoft.com/en-us/deployoffice/compat/office-file-format-reference#file-formats-that-are-supported-in-excel).

---
> `Run` isn't doing anything

Ensure that you've selected two `*.xlsx` files. Spreadsheets of a different type will not work.

---
> I'm getting an error message popping up when I run the file

If you're getting an error message and you can't figure out what it's saying or how to fix it, reach out to me. If you click `More Info` on the error popup and copy the big text box, that text (a full stack trace on the error) can help me figure out what's going on.

My first guess for where an error could arise is if your files don't match the format described in [Files](/#files).

## Details of what it does
First, the program will open the selected spreadsheet files and copy them into [XSSFWorkbook](https://poi.apache.org/apidocs/5.0/) objects (from [Apache POI](https://poi.apache.org/)). It will then 'cache' the data from the reassessment maximo mapping file into a [HashMap](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html) where keys are given by the temporary asset number (with `NEW` removed), and the values are the corresponding maximo ids. If there are any rows where the temporary asset number does not match the regular expression `\d{7}NEW`, the row will be skipped and a comment will be written indicating this.

Once the map is created, the program will iterate over each list of the inventory file, comparing the listed id to the map and writing the corresponding maximo id. If any temporary id is not present in the map (i.e. it wasn't in the reassessment maximo mapping file) the program will leave a comment indicating this.

## Changing the Code
The `.JAR` file is compiled and compressed, meaning all the code is not human-readable. You can decompress and recompress the file using a tool like WinRAR to change certain parts, like the GUI text (located in `Maximo ID Tool.jar\us\akana\tools\maximoIds\messages.properties`, a pure text map file), but all of the code itself is not editable.

Instead, I've copied the program files to the github repository, where you can download them and open them in an IDE (I use [Eclipse](https://eclipseide.org/)). See [In the GitHub](/#in-the-github) for more.

## In the GitHub
This `README.md` is located directly within the parent directory, so is `Maximo ID Tool.jar`, the entire program all bundled together in a file you can run.

I did detailed documentation in the code using javadocs - this could be useful to anyone trying to edit or change my code, I've compiled all of that into `doc`, `doc\index.html` is the entry page there.

=======
# Maximo ID Tool
A tool that references a reassessment maximo mapping file to write new maximo ids to an inventory file using the id numbers

## Setup
Java SE 17 is required to run this program. If you don't have Java 17 or a newer version installed, you can download an installer for Temurin/OpenJDK 17 from [here](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.8_7.msi). This is an open-source version of java. Once downloaded, you can run the installer by double-clicking, it will open a window guiding you through the installation. Leaving everything as the defaults and just clicking through the pages should work perfectly.

The `.JAR` for the program is located in the parent directory of the github (see [In the Github](/#in-the-github) for more).

Once Temurin/Java 17 and the program jar are installed, double click the jar to run.

##GUI and How to Use
After double-clicking the `.jar` file, a window titled "Maximo ID tool" will open. It will have 2 prompts, as described below:
1. `Select inventory file (ASPxGridView*.xlsx)`
	* Click on the select button to open a file prompt, navigate to and select the Inventory spreadsheet (name starting with `ASPxGridViewInventory`). Note that this must be an `*.xlsx` file, rather than `*.xlsb` or any other spreadsheet filetype - see [Troubleshooting](/#troubleshooting) for more. The contents of this spreadsheet should be as described under [Files->Inventory](/#inventory).
2. `Select reassessment maximo mapping file (*.xlsx)`
	* Click on the select button to open a file prompt, navigate to and select the reassessment maximo mapping file. Again, this must be an `*.xlsx` file. The contents of this spreadsheet should be as described under [Files->Reassessment Maximo Mapping](/#reassessment-maximo-mapping)

The other contents of the window are the `Close` and `Run` buttons, which are fairly self-explanatory. There is also an info text box, which won't be visible when the program is first run, but as the tool is used will populate with relevant information about what's going on.

## Files
The program requires two spreadsheets to run, as follows:
### Inventory
The first page of this spreadsheet should be a list of inventory items where the 19th column (column S) is the 7-digit id number for the inventory item, and the 20th column (column T) is empty and ready to be populated by the maximo ID. The data should start on the second row of the spreadsheet, where the first is headers. There may be other pages in the spreadsheet file, and any data not in columns 19 and 20 is irrelevant and will be ignored by the program.
### Reassessment Maximo Mapping
This spreadsheet file must have a sheet named exactly `Assets`, case-sensitive. Any other sheets will be ignored. On the `Assets` sheet, the second column (column B) should be temporary asset numbers - 7 digits followed by `NEW`, i.e. `2669627NEW`. The third column (column C) should have the corresponding maximo asset numbers, i.e. AB418012. Data should start on the second row, where the first is headers. Any data not in columns 2 and 3 is irrelevant and will be ignored by the program.

## Troubleshooting
> Nothing's happening when I double click the `.JAR` file

Ensure you've installed Java as specified under [Setup](/#Setup). If you believe you have, try checking your java version:
1. Press Win+R, type `cmd` and press enter - this will open a command prompt window
2. Type `java -version` and press enter
3. If you've installed java as specified, the first line under your typing should read `openjdk version "17.0.8" 2023-07-18`[^2]. If, instead, it says `'java' is not recognized as an internal...` then java is not installed.

[^2]: If you had a version of java other than the one specified in Setup, this may show a different version, but should be similar. However, you probably wouldn't be in this troubleshooting step if this is the case.

---
> I only have spreadsheets of type `*.xlsb` or `*.csv` (or any other spreadsheet type) and the program won't open them

Open the spreadsheets in Microsoft Excel and select 'File -> Save As -> This PC' and choosing 'Excel Workbook (.xlsx)' from the drop-down. A full list of filetypes that Excel supports (and thus can be converted to .xlsx) can be found [here](https://learn.microsoft.com/en-us/deployoffice/compat/office-file-format-reference#file-formats-that-are-supported-in-excel).

---
> `Run` isn't doing anything

Ensure that you've selected two `*.xlsx` files. Spreadsheets of a different type will not work.

---
> I'm getting an error message popping up when I run the file

If you're getting an error message and you can't figure out what it's saying or how to fix it, reach out to me. If you click `More Info` on the error popup and copy the big text box, that text (a full stack trace on the error) can help me figure out what's going on.

My first guess for where an error could arise is if your files don't match the format described in [Files](/#files).

## Details of what it does
First, the program will open the selected spreadsheet files and copy them into [XSSFWorkbook](https://poi.apache.org/apidocs/5.0/) objects (from [Apache POI](https://poi.apache.org/)). It will then 'cache' the data from the reassessment maximo mapping file into a [HashMap](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html) where keys are given by the temporary asset number (with `NEW` removed), and the values are the corresponding maximo ids. If there are any rows where the temporary asset number does not match the regular expression `\d{7}NEW`, the row will be skipped and a comment will be written indicating this.

Once the map is created, the program will iterate over each list of the inventory file, comparing the listed id to the map and writing the corresponding maximo id. If any temporary id is not present in the map (i.e. it wasn't in the reassessment maximo mapping file) the program will leave a comment indicating this.

## Changing the Code
The `.JAR` file is compiled and compressed, meaning all the code is not human-readable. You can decompress and recompress the file using a tool like WinRAR to change certain parts, like the GUI text (located in `Maximo ID Tool.jar\us\akana\tools\maximoIds\messages.properties`, a pure text map file), but all of the code itself is not editable.

Instead, I've copied the program files to the github repository, where you can download them and open them in an IDE (I use [Eclipse](https://eclipseide.org/)). See [In the GitHub](/#in-the-github) for more.

## In the GitHub
This `README.md` is located directly within the parent directory, so is `Maximo ID Tool.jar`, the entire program all bundled together in a file you can run.

I did detailed documentation in the code using javadocs - this could be useful to anyone trying to edit or change my code, I've compiled all of that into `doc`, `doc\index.html` is the entry page there.

>>>>>>> Add everything
Everything else in the github is stuff that gets bundled into the `.jar` when it's compiled. `pom.xml` contains all sorts of computer-readable information about the program and how it's built and run. All of the java code is located in the three `.java` files in `src\main\java\us\akana\tools\maximoIds`.