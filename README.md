# Day-Planner-Android-App

The Day Planner Android App is crafted to empower users with an efficient and user-friendly solution for organizing and planning diverse activities, from work-related tasks to free-time and travel plans. The application prioritizes a seamless user experience, incorporating features that streamline activity management.

## Project Motivation

This app is developed to simplify the organization of daily activities, catering to user preferences and providing a platform for effective time management. Features like multimedia support, travel activity visualization, and flexible notification settings aim to enhance the overall user experience.

## Key Dependencies & Platforms

- **[Android Studio](https://developer.android.com/studio):** The primary development environment for Android apps, utilizing Kotlin and Java programming languages for enhanced readability and maintainability.

- **[SQLite Database](https://www.sqlite.org/index.html):** A lightweight relational database management system used for efficient data storage and seamless integration with Android applications.

- **[Google Maps API](https://developers.google.com/maps/documentation/android-sdk/overview):** Integrated to provide a precise location visualization for travel activities, enhancing the user experience with an interactive map view.

- **[Material Design Guidelines](https://m3.material.io/):** Followed for designing graphical elements and UI components, ensuring a cohesive and visually appealing user interface in line with modern design standards.

- **Emulator Testing** The app undergoes testing on various emulators representing devices with diverse screen densities and sizes, ensuring compatibility and optimal performance across different Android devices, including phones and tablets.

## Key Features

- **Activity Details:** Comprehensive information for each activity, including name, time, description, and location.

- **Multimedia Support:** Free-form activities can include images, allowing users to capture or upload visuals.

- **Travel Activity Visualization:** Integration with Google Maps API for precise location visualization.

- **Calendar View:** Calendar-based display for easy tracking, with a chronological list for quick reference.

- **Notifications/Reminders:** Customizable notifications for upcoming activities based on user-defined settings.

- **Settings Page:** Language selection, notification preferences, and localization support for enhanced user customization.

- **Asynchronous Operations:** Implementation of ExecutorService for smooth handling of background tasks.

## Running the Day Planner Android App

Follow these steps to run the Day Planner Android App locally on your development machine using Android Studio:

### Prerequisites

1. **Install Android Studio:** If you don't have Android Studio installed, download and install it from the official website.

2. **Set Up an Emulator or Connect a Physical Device:** You can either configure an Android Emulator through Android Studio or connect a physical Android device to your computer. Ensure that the device or emulator has the necessary API level compatible with the app.

### Clone the Repository

Open a terminal and clone the Day Planner Android App repository:

```bash
    git clone https://github.com/SlavkoMuzdeka/Day-Planner-Android-App
```

### Open Project in Android Studio

1. Launch Android Studio.
2. Click on "Open an existing Android Studio project."
3. Navigate to the directory where you cloned the repository and select the root folder of the Day Planner Android App.

### Configure Emulator (Skip if using Physical Device)

1. Click on the "AVD Manager" icon in the toolbar.
2. Create a new Virtual Device with specifications suitable for testing the app. Ensure it meets the screen density and size requirements mentioned in the project details.
3. Start the emulator.

### Build and Run the App

1. In Android Studio, locate the "Run" button (green play icon) in the toolbar.
2. Choose the target device (either the configured emulator or your connected physical device).
3. Click "Run" to build and install the app on the selected device.

### Interact with the App

1. Once the app is installed, it will launch on the selected device.
2. Explore the app's features, create activities, and test different functionalities.

### Testing on Different Emulators

For comprehensive testing on various screen densities and sizes, repeat the process on different emulators configured with distinct specifications. Ensure that the app maintains optimal functionality across a range of Android devices.

Now you have successfully set up and run the Day Planner Android App on your local environment. Explore the app's features, and feel free to provide feedback or contribute to the development process.

## Future Prospects

- Enabling collaborative planning for group activities.

- Implementing advanced search and filter options for efficient activity tracking.

- Continuous improvement of UI/UX based on user feedback.

- Exploring integration with external calendars for seamless synchronization.
