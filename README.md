# Smart Pantry Manager

Android Studio project for the Smart Pantry Manager coursework.

## Open
1. Extract this ZIP.
2. In Android Studio choose **Open** and select the `SmartPantryManager` folder (the folder containing `settings.gradle`).
3. Allow Gradle sync to finish. Gradle 8.7 and Android Gradle Plugin 8.6.1 are configured.
4. Build > Clean Project, then Build > Rebuild Project.

## Configuration
- Java
- Groovy Gradle files
- Namespace/application ID: `za.ac.smartpantrymanager`
- Minimum SDK: 23
- Compile/target SDK: 36

The project source is consolidated into one Java package tree to avoid duplicate-class errors. Layout resources were XML-parsed for well-formedness and missing navigation controls referenced by Java were added. A successful Android Gradle build has not been run in this environment; test the project in Android Studio and report any remaining first error.
