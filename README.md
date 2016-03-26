This is the repository for Mobile Computing System course.

We will learn Mobile Applications, Mobile OS, Mobile Networks, and Embedded Sensor Systems based on Android and Arduino development.

The code for assignments and labs are:

####hw1-Eight-Queens
8-Queens Puzzle Game: `java/edu/unc/chongrui/assignment1/`

1. place 8 chess queens on an 8 x 8 chess board so that no two queens threaten each other

2. detects and reports wrong moves;

3. If the person gives up in the middle of the game, the program automatically finishes the puzzle, from where the person left the game;

4. Extra: show total number of valid solutions when the “give up” button is pressed and an option to choose which solution we want to see.

####lab2
Building Apps: Layouts, Buttons, Images, and Event Handling.


####hw2-RealTime-SensorPlotter
Plot sensor values, their mean, and their variance over time.

+ First Activity: `app/src/main/java/edu/unc/chongrui/assignment2/viewer/DashboardActivity.java`
  - The user will be able to choose which sensor he wants to view.
  - show at least two types of sensors
    - Accelerometer
    - Proximity
  - display the status of sensors
    - whether present or not
    - max range
    - resolution
    - min delay
  - when a user selects a sensor, the user will be taken to next activity

+ Second Activity: `app/src/main/java/edu/unc/chongrui/assignment2/viewer/DataPlottingActivity.java`
  - Once the sensor type is chosen, the user will be taken to a second activity that presents sensor data in two formats:
    - (a) raw values, means, and standard deviations over time, and
    - (b) an animated image based on the current sensor reading.
  - The custom view plots the following:
    - 10 sensor values
    - a running mean of last 10 values
    - a running standard deviation of last 10 values on same graph
  - Each sensor value, with its mean and stddev, is wrapped into a `ChartData` object; a deque is maintained to have at most 10 these objects.
  - Every time a new sensor data arrives, the `ChartData` object will be created and current mean/stddev inside the queue will be updated by it.
  - When `onDraw()` is invoked, it will traverse the queue and plot each object.
  - The <b>one-pass algorithm for running mean and std-dev</b>
    - http://introcs.cs.princeton.edu/java/97data/OnePass.java.html

+ Object-Oriented Design Patterns
  - `model.SensorData`
    - Parent class for all types of sensor data
    - A `SensorData` object would contain timestamp of event, type of sensor, and the sensor value.
    - For Environmental Sensors, there would be only one sensor value as `event.values[0]`.
    - For Motion Sensors, the values would be computed as `Sqrt(x*2 + y*2 + z*2)` from raw values on three dimensions.
  - `model.SensorDataFactory`
    - Apply <b>Factory Design Pattern</b> to create SensorData objects by sensor types.

+ Structure of the Model
    ```
    model.SensorData --- model.SensorDataFactory (a factory which creates SensorData object by sensor type)
    / | \
    model.AccelerometerData
    model.LightData
    model.ProximityData
    ```

+ Structure of the Viewer
    ```
    viewer.SensorParentActivity (abstract; registers SensorManager and Sensors; )
    /                         \
    viewer.DashboardActivity, viewer.DataPlottingActivity (implements onSensorChanged)
                                |
                                CustomChartView (invoked by Intent, plot each point)
    
    ````

####hw3-GoogleLocationService-MediaPlayer
+ Set up Google Maps API key
  - TODO: Before you run your application, you need a Google Maps API key.
    + Follow the directions here:
    + https://developers.google.com/maps/documentation/android/start#get-key
  - TODO: Generate the page `google_maps_api.xml` automatically by creating the project using Google Maps Activity
  - Once you have your key (it starts with "AIza"), replace the "google_maps_key" string in the file.
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">Your Public Key Here</string>    TODO: Before you run your application, you need a Google Maps API key.
  - Example: `<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">Your Public Key Here</string>`

