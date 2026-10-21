# Smart Pantry Manager 

## Project Overview

Smart Pantry Manager is an Android mobile application developed in Java
to help users manage pantry ingredients and discover recipes using
ingredients they already have available.

The application aims to reduce food waste, simplify pantry management,
and encourage users to prepare meals without making unnecessary shopping
trips.

## Features

### Pantry Management

-   Add, view, edit, and delete pantry ingredients.
-   Record ingredient names, quantities, units, and expiry dates.

### Persistent Data Storage

-   Store pantry ingredients using SQLite.
-   Retain saved pantry information after the application is closed and
    reopened.

### Smart Recipe Suggestions

-   Suggest recipes based on ingredients available in the pantry.
-   Match recipes against available ingredient quantities.
-   Update suggestions when pantry ingredients or quantities change.
-   View recipe ingredients, quantities, and preparation instructions.

### Settings

-   Select a preferred measurement unit.
-   Configure the expiry-alert preference.
-   Save preferences for future sessions.

### User Interface

-   Navigate between pantry management, recipe suggestions, and
    settings.
-   View ingredient and recipe images, ingredient lists, and recipe
    details.

## Technologies Used

-   **Programming language:** Java
-   **Development environment:** Android Studio
-   **Platform:** Android
-   **Database:** SQLite
-   **User interface:** XML layouts and Android UI components
-   **Application components:** Activities, Intents, RecyclerView, and
    adapters
-   **Preferences:** SharedPreferences
-   **Version control:** Git
-   **Source code hosting:** GitHub
-   **Build system:** Gradle

## Project Structure

SmartPantryManager/
├── app/
│   └── src/main/
│       ├── java/za/ac/smartpantrymanager/
│       ├── res/
│       │   ├── drawable/
│       │   ├── layout/
│       │   └── values/
│       └── AndroidManifest.xml
├── gradle/wrapper/
├── gradlew
├── gradlew.bat
├── build.gradle
├── settings.gradle
├── gradle.properties
├── .gitignore
└── README.md


*Note: This is an example of the expected project structure. Actual
resource and source filenames may differ.*

## Requirements

-   Android Studio.
-   A compatible Java Development Kit (JDK), according to the project's
    Gradle configuration.
-   An Android SDK compatible with the project.
-   An Android emulator or a physical Android device with USB debugging
    enabled.

An internet connection may be required for the initial Gradle dependency
download.

## How to Download and Run the Application

### 1. Clone the repository

Open a terminal or PowerShell window and run:

``` bash
git clone https://github.com/LufunoNemugumoni/SmartPantryManager.git
```

### 2. Open the project

1.  Launch Android Studio.
2.  Select **Open**.
3.  Navigate to the downloaded `SmartPantryManager` folder.
4.  Select the project folder containing `settings.gradle`.
5.  Allow Android Studio to load the project.

### 3. Sync Gradle

Wait for Android Studio to finish loading the project. If prompted,
select **Sync Now** and allow the required dependencies to download.

If a build error occurs, check that the installed Android SDK and JDK
are compatible with the project's Gradle configuration.

### 4. Select a device

Choose an available Android emulator or connect an Android phone with
USB debugging enabled.

### 5. Build and run

1.  Wait for Gradle synchronization to finish.
2.  Select the `app` run configuration.
3.  Click the **Run ▶** button.
4.  Wait for the application to install and launch.

## Testing

The following functional checks have been performed during development:

-   Adding, editing, and deleting pantry ingredients.
-   Verifying that pantry data persists after reopening the application.
-   Displaying recipe suggestions when required ingredients and
    quantities are available.
-   Updating suggestions after ingredients or quantities change.
-   Viewing recipe ingredients and preparation instructions.
-   Saving settings and verifying that preferences persist.

## Purpose and Expected Benefits

Smart Pantry Manager combines local data storage, user interface design,
ingredient management, and rule-based recipe matching. Its goals are to:

-   Help users keep track of pantry ingredients.
-   Make better use of ingredients already available.
-   Reduce avoidable food waste.
-   Simplify meal planning.
-   Demonstrate practical Android application development skills.

## Author and Repository

-   **Project:** Smart Pantry Manager
-   **Programming language:** Java
-   **Repository:**
    https://github.com/LufunoNemugumoni/SmartPantryManager

## Academic Project

Developed as part of the Mobile App Development 700 practical
assignment.
