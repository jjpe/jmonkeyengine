# Installation in eclipse
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

### Flippendo...eh... Fluendo!
In `src/jheora` there are a bunch of references to some `come.fluendo` package which is not included in the lib folder.
The `src/jheora` sub-project seems unused in the `build.xml` files, so I'm guessing this is deprecated or something.

Anyway, let's just remove it:    
Go to `Properties -> Java Build Path`.    
Locate and select `<your-project-name>/src/jheora` in the list of source folders.    
Click `remove`.    
