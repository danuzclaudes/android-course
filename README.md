This is the repository for COMP 790 - Mobile Computing System

We will learn Mobile Applications, Mobile OS, Mobile Networks, and Embedded Sensor Systems based on Android and Arduino development.

[Course Link] (http://mobile.web.unc.edu)

The code for assignments and labs are:

####hw1-Eight-Queens
8-Queens Puzzle Game: `java/edu/unc/chongrui/assignment1/`

1. place 8 chess queens on an 8 x 8 chess board so that no two queens threaten each other

2. detects and reports wrong moves;

3. If the person gives up in the middle of the game, the program automatically finishes the puzzle, from where the person left the game;

4. Extra: show total number of valid solutions when the “give up” button is pressed and an option to choose which solution we want to see.

####lab2
Building Apps: Layouts, Buttons, Images, and Event Handling.


####hw2-hw2-RealTime-SensorPlotter
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

