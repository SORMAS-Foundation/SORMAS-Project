# Sormas Testing Suite


# Integration into Jenkins CI/CD

The integration of Katalon Studio in Jenkins is quite simple:

## Prequisites

System / OS packages needed to be installed:

- xvfb 
- firefox(-esr)


Jenkins plugins needed to be installed:

- katalon
- github
- git

## Jenkins configuration (FreeStyle element / mandatory)

### GitHub config

Repository URL: URL to Katalon Sormas tests

![alt text](images/J_config_scm.png "GitHub config")

### Build config

Download Katalon Studio Version: `e.g.: 7.2.6`

Command arguments: `-browserType="Firefox" -retry=0 -statusDelay=15 -testSuitePath="Test Suites/Login/LoginRoles" -apiKey=<your api key>`

Xvfb-run configuration (for Linux): `-s "-screen 0 1024x768x24"`

![alt text](images/J_config_build.png "Build config")

### Post build config

Archive: `Reports/**`

JUnit-Tests: `Reports/**/*.xml`

![alt text](images/J_config_post_build.png "PostBuild config")

## Getting the API-Key from Katalon Studio

In Katalon-Studio open the dialog Window->Command Palette

Select the option "Generate Command for Console Mode"

Copy the String from the field "Katalon API Key" and insert it into the build step from jenkins. 


## Katalon command line options (useful ones)
Source: https://docs.katalon.com/katalon-studio/docs/console-mode-execution.html#general-options

<table>
   <thead>
      <tr>
         <th>Katalonc Command Line Option</th>
         <th>Description</th>
         <th>Mandatory?</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <td>-statusDelay=&lt;seconds&gt;</td>
         <td>System updates execution status of the test suite after the delay period (in seconds) specified.</td>
         <td>N</td>
      </tr>
      <tr>
         <td>-projectPath=&lt;path&gt;</td>
         <td>Specify the project location (include .prj file). The absolute path must be used in this case.</td>
         <td>Y</td>
      </tr>
      <tr>
         <td>-testSuitePath=&lt;path&gt;</td>
         <td>Specify the test suite file (without extension .ts). The relative path (root being project folder) must be used in this case.</td>
         <td>Y</td>
      </tr>
      <tr>
         <td>-testSuiteCollectionPath=&lt;path&gt;</td>
         <td>
            <p>Specify the test suite file (without extension .tsc). The relative path (root being project folder) must be used in this case.</p>
            <p>Note: Available only in 4.4+</p>
         </td>
         <td>Y (<em>If -testSuitePath is not used. Otherwise it's optional</em>)</td>
      </tr>
      <tr>
         <td>-browserType=&lt;browser&gt;</td>
         <td>
            <p>Specify the browser type used for test suite execution.</p>
            <p>The following browsers are supported in Katalon:</p>
            <ul>
               <li>Firefox</li>
               <li>Chrome</li>
               <li>IE</li>
               <li>Edge</li>
               <li>Edge (Chromium)</li>
               <li>Safari</li>
               <li>Remote</li>
               <li>Android</li>
               <li>iOS</li>
               <li>Web Service</li>
            </ul>
         </td>
         <td>
            <p>Y</p>
            <p>
               <strong>Only Chrome, Firefox and Remote is available for use in Linux version.</strong>
            </p>
            <p>
               <strong><code>Web Service</code> is used for Web Service test execution.
            </strong></p><strong>
         </strong></td>
      </tr>
      <tr>
         <td>-retry=&lt;number of retry times&gt;</td>
         <td>Number of times running test cases in the test suite.</td>
         <td>N</td>
      </tr>
      <tr>
         <td>-retryFailedTestCases=&lt;true, false&gt;</td>
         <td>Retry failed test cases fail in test suite ( override setting in test suite file ). There are 2 options for retry: true if you want run fail test case and otherwise false</td>
         <td>N</td>
      </tr>
      <tr>
         <td>-reportFolder=&lt;path&gt;</td>
         <td>Specify the destination folder for saving report files. Can use absolute path or relative path (root being project folder).</td>
         <td>N</td>
      </tr>
      <tr>
         <td>-reportFileName=&lt;name&gt;</td>
         <td>Specify the name for report files (.html, .csv, .log). If not provide, system uses the name "report" (report.html, report.csv, report.log). This option is only taken into account when being used with "-reportFolder" option.</td>
         <td>N</td>
      </tr>
      <tr>
         <td>-executionProfile</td>
         <td>
            <p><strong>Starting from Katalon Studio version 5.4</strong></p>
            <p>Specify the&nbsp;<a href="/pages/viewpage.action?pageId=13697476">execution profile</a>&nbsp;to be executed with</p>
         </td>
         <td>N</td>
      </tr>
      <tr>
         <td>-g_XXX</td>
         <td>
            <p><strong>Starting from Katalon Studio version 5.9</strong></p>
            <p>Override Execution Profile variables.</p>
            <p>Example:</p>
            <p><code class="java plain"> -g_userName="admin"</code></p>
         </td>
         <td>N</td>
      </tr>
   </tbody>
</table>
