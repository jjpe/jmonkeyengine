# Sonar
## The DIY method
Go to www.sonarsource.org, which is the website of Sonar.
On the right side you will see a 'Get Started' section,
which will tell you where to download it from.

http://docs.codehaus.org/display/SONAR/Installing+Sonar    
contains more information on installing it.

Then use one of the links under 'Installing Client' to get a client.
The client will analyze the code of your project using the server.
A fully browsable report will now be available on the server (localhost:9000 by default).

## A little more guidance
Download the zip and unzip it.
There should either be some buildfile or build script to create a sonar.war file, which 
can be hosted on a servlet container like jetty or tomcat, or there should be a run script,
which will do this hosting for you.

After the server is started (either by manually hosting the war file, or running some run script)
it can (by default) be found at http://localhost:9000.
This website is where you will see an overview of the results of all your projects that you analyzed.

To get a project analyzed, you need to use a Sonar Client.
The Ant Task client can be downloaded from http://docs.codehaus.org/display/SONAR/Installing+and+Configuring+Sonar+Ant+Task.
Installation and usage instructions can be found at http://docs.codehaus.org/display/SONAR/Analyzing+with+Sonar+Ant+Task.

You just copy the example ant code into an xml file and then run the sonar task using `ant -f file.xml sonar`.
Of course you do need to change the properties to fit your project, but that should be straight forward.
Add the following property if you changed the default location of sonar:    
`<property name="sonar.host.url" value="http://Blaaskaak:8080/sonar" />`



# Installation of jMonkey in eclipse
## Importing in Eclipse
Go to `File -> new -> Java Project`    
Untick `use default location` and browse to this repository's `engine` folder.    
Click `Finish`

## Fixing eclipse errors
The engine project actually contains several projects in its `src` folder.
These projects use the same package names, creating conflicts.
For this project we only look at the src/core folder, so let's fix the conflicts.

### Bullets (they are dangerous...)
Note that `src/bullet` and `src/jbullet` use the same package names.
I'm not sure why this is, but it results in conflicts in Eclipse.
So let's remove the `src/jbullet` folder from the `.project` file.

Right click on the project in the Package view.    
Go to `Properties -> Java Build Path`.    
Locate and select `<your-project-name>/src/jbullet` in the list of source folders.    
Click `remove`.   

### Android (robots are taking over... in Eclipse at least)
When referencing the `lib/android` library in Eclipse,
it hides the standard java packages.
This results in a problem for `com.jme3.asset.plugins.HttpZipLocator`.
It expects the normal `java.util.zip.ZipEntry`, 
but Eclipse assumes it wants the one from the android library.

As we are only using `src/core` for this project, we can solve this by removing the android sub-project and library:    
Right click the project in the Package view.    
Go to `Properties -> Java Build Path`.    
Locate and select `<your-project-name>/src/android` in the list of source folders.    
Click `remove`.    
Go to the `Libraries` tab.    
Locate and select `android`.    
Click `remove`.

We used the same process to add the android library and source folder again.
After removing, saving and adding it again, eclipse was able to function properly without the weird errors.

### Flippendo...eh... Fluendo!
In `src/jheora` there are a bunch of references to some `come.fluendo` package which is not included in the lib folder.
The `src/jheora` sub-project seems unused in the `build.xml` files, so I'm guessing this is deprecated or something.

Anyway, let's just remove it:    
Go to `Properties -> Java Build Path`.    
Locate and select `<your-project-name>/src/jheora` in the list of source folders.    
Click `remove`.    

# Installation of tools in Eclipse
## PMD
http://marketplace.eclipse.org/content/pmd-eclipse?mpc=true&mpc_state=
## FindBugs
http://marketplace.eclipse.org/content/findbugs-eclipse-plugin
## Sonar
http://marketplace.eclipse.org/content/sonar
## Metrics
http://marketplace.eclipse.org/content/eclipse-metrics
## EclEmma
http://marketplace.eclipse.org/content/eclemma-java-code-coverage

After installing EclEmma, a run launcher appears to the left of the standard debug launcher.
Just use that to run your testcases and watch the coverage appear.
## TPTP
Generates UML sequence diagrams from Java source code
