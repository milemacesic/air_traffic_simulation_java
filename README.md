# Air Traffic Simulation

A Java desktop application for managing airports and flights and simulating air traffic between airports.

This project was developed as part of a university Object-Oriented Programming course. It focuses on object-oriented programming, file handling, GUI development with Java AWT, and basic simulation logic.

## Features

* Add and manage airports
* Add and manage flights
* Validate airport and flight data
* Display airports on a visual map
* Select airports directly from the map
* Filter airports using checkboxes
* Save and load data using:

  * CSV
  * JSON
* Run, pause, and reset the flight simulation
* Display the current simulation time
* Visualize active flights on the map
* Prevent flights from the same airport from departing too close together
* Inactivity detection with a warning dialog

## Technologies

* **Java**
* **Java AWT**
* **Object-Oriented Programming**
* **CSV**
* **JSON**
* **Eclipse IDE**

## Project Structure

```text
src/
├── gui/
│   └── MainFrame.java
│
├── manage/
│   ├── CSV.java
│   ├── DataManager.java
│   ├── FileType.java
│   ├── Inactivity.java
│   └── JSON.java
│
└── model/
    ├── Airport.java
    ├── Flight.java
    └── Simulation.java
```

## Main Classes

### `Airport`

Represents an airport and contains information such as:

* Airport name
* Three-letter airport code
* X coordinate
* Y coordinate

### `Flight`

Represents a flight between two airports and contains:

* Departure airport
* Destination airport
* Planned departure time
* Flight duration

### `Simulation`

Contains information about an active flight during the simulation and is used to calculate its position between the departure and destination airports.

### `DataManager`

Handles the application's airport and flight data.

### `CSV` and `JSON`

Provide functionality for saving and loading the project data in CSV and JSON formats.

### `MainFrame`

Contains the graphical user interface and connects the different parts of the application.

### `Inactivity`

Monitors user activity and displays a warning after a period of inactivity.

## Simulation

The simulation represents a 24-hour period.

When the simulation is started:

1. Flights are ordered according to their planned departure times.
2. Actual departure times are calculated while maintaining the required spacing between flights from the same airport.
3. The simulation clock advances automatically.
4. Active flights are displayed on the map.
5. Flights move from their departure airport toward their destination.
6. The simulation ends after reaching the end of the 24-hour period.

The simulation can be **Run**, **Paused**, or **Reset** using the controls in the application.

## Data Validation

The application performs validation when creating airports and flights.

For example:

* Airport codes must contain three letters.
* Airport coordinates must be within the allowed geographic ranges.
* Flight departure times must use a valid time format.
* Required fields cannot be empty.
* Invalid airport and flight data produce appropriate error messages.

## Running the Project

### Using Eclipse

1. Clone the repository:

```bash
git clone https://github.com/yourusername/air-traffic-simulation.git
```

2. Open Eclipse.
3. Select **File → Import**.
4. Choose **Existing Projects into Workspace**.
5. Select the cloned project folder.
6. Finish the import.
7. Run the main class containing the application's `main` method.

## Purpose

The main goal of this project was to practice:

* Object-oriented programming
* Java classes and inheritance
* Exception handling
* File input/output
* Data serialization
* GUI programming with AWT
* Event handling
* Threads
* Basic simulation logic
* Working with Git and GitHub

## Author

**Mile Maćešić**

GitHub: https://github.com/milemacesic  
