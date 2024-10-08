# Android Touch HSV Color Picker

This project was developed during my role as a junior programmer at Barunastra Roboboat ITS. It utilizes the OpenCV Android Library's [BlobDetector](https://github.com/W4RH4WK/BlobDetector/tree/master) to implement HSV (Hue, Saturation, Value) color picking in real-time. Initially built using Eclipse IDE, the project has now been transitioned to work with Android Studio IDE.

## Project Structure

The essential folders and files are structured as follows:

### Android Studio IDE
    .
    ├── android-hsv-pick/android-studio-project/app                                                               
    │   ├── src                                                            # Main directory for source files.
    │   │   ├── main                                                       # Contains primary code and resources.
    │   │   │   ├── AndroidManifest.xml                                    # Configures the app's name, icon, and permissions.
    │   │   │   ├── res                                                    # Resources folder for layouts and UI elements.
    │   │   │   │   ├── layout                                             # Contains XML files defining the user interface.
    │   │   │   │   │   ├── biner_view.xml                                 # Layout for binary threshold display.
    │   │   │   │   │   ├── camera_view.xml                                # Layout for camera preview.
    │   │   │   │   │   ├── content_main.xml                               # Layout defining the main activity's content.
    │   │   │   ├── java/com/mbsbahru/hsv_touch_picker/                    # Java source code directory.
    │   │   │   │   │   ├── BaseLoaderCallback.java                        # Provides callback for OpenCV initialization.
    │   │   │   │   │   ├── BlobDetector.java                              # Implements the core blob detection logic using OpenCV.
    │   │   │   │   │   ├── InstallCallbackInterface.java                  # Callback for package installation management.
    │   │   │   │   │   ├── LoaderCallbackInterface.java                   # Handles callback after OpenCV library is loaded.
    │   │   │   │   │   ├── SeekAdapter.java                               # Adapter for SeekBar used in HSV adjustment.
    │   │   │   │   │   ├── SeekBarHsvSeg.java                             # Custom SeekBar for segmenting HSV values.
    └── ...

### Eclipse IDE
    .
    ├── android-hsv-pick/eclipse-ide-project
    │   ├── AndroidManifest.xml                                            # Configures the app's name, icon, and permissions.
    │   ├── res                                                            # Resources folder for layouts and UI elements.
    │   │   ├── layout                                                     # Contains XML files defining the user interface.
    │   │   │   ├── biner.xml                                              # Binary thresholding layout.
    │   │   │   ├── camera_view.xml                                        # Camera preview layout.
    │   ├── src/mbs/hsvcolorpick/                                          # Java source code directory.
    │   │   │   ├── BlobDetector.java                                      # Implements the blob detection logic.
    │   │   │   ├── MisiView.java                                          # Main activity controlling camera and detection.
    │   │   │   ├── SeekAdapter.java                                       # Adapter for SeekBar used in HSV adjustment.
    │   │   │   ├── SeekBarVal.java                                        # Custom SeekBar for adjusting HSV values.

## Android Studio Setup

Download Android Studio IDE [here](https://developer.android.com/studio?gad_source=1&gclid=CjwKCAjw26KxBhBDEiwAu6KXt9xJpCalnDTE7JICAHzDQWsQN_PKbyNYdl6o0rNav8LPQDlxV7bteRoCXh4QAvD_BwE&gclsrc=aw.ds). This project can be built using Java for the back-end and XML for the front-end.

## Dependencies

This application requires the following libraries to run in Android Studio:
- **OpenCV 4.1.0**: A library for computer vision functionalities. Download [here](https://sourceforge.net/projects/opencvlibrary/files/4.1.0/opencv-4.1.0-android-sdk.zip/download).

## Installations

### Building and Developing the Project Files

1. Download or clone the repository.
2. Open the `android-studio-project` of the `android-hsv-pick` project in Android Studio and click "Trust Project."
3. In the Menu Bar, select:\
   **Build** → **Rebuild Project**.
4. If the build is successful:
   - On your Android device, enable USB debugging:\
     **Settings** → **About phone** → tap **Build number** 7 times → allow developer mode → return to **Settings** → **Systems** → **Developer options** → turn on **USB debugging**.
   - Connect your Android device to your computer via USB cable.
   - In Android Studio, press the *Run* button (or Ctrl + R).
5. If the build fails:
   - Download the [OpenCV for Android dependency](https://sourceforge.net/projects/opencvlibrary/files/4.1.0/opencv-4.1.0-android-sdk.zip/download) and extract it.
   - Import the dependency in Android Studio:\
     **File** → **New** → **Import Module** → select the extracted `OpenCV-android-sdk`.
   - Link the dependency in **Project Structure** under **Dependencies**, selecting the `opencv` module.
   - Ensure your `build.gradle` is set to use the correct OpenCV module.

6. Once setup is complete, modify the app code in the [Java](https://github.com/mbsbahru/android-hsv-pick/tree/master/android-studio-project/app/src/main/java/com/mbsbahru/hsv_touch_picker) and [XML](https://github.com/mbsbahru/android-hsv-pick/tree/master/android-studio-project/app/src/main/res/layout) directories.

### Installing the APK File

To install the app directly:
- Download the generated `.apk` file [here](https://drive.google.com/file/d/1NhF9o5D2DPzwKQo5v9N8kOdDGCv6tQhN/view?usp=sharing) on your Android device.
- Use the built-in Android package installer to install it.
- Grant camera permissions in **Settings** → **Apps** → **'AndroidHSV-TouchPicker'** → **Permissions** → allow {Camera}.
- Open the app.

## How to Use the App

Simply touch any object in the camera view, and the HSV sliders will adjust according to the color detected. You can also manually adjust the HSV threshold values by dragging the sliders.

![Android HSV Picker](https://github.com/mbsbahru/android-hsv-pick/blob/master/figures/Android_Color_Picker.jpeg)
