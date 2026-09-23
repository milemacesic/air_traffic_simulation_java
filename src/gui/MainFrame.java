package gui;

import manage.CSV;
import manage.DataManager;
import manage.Inactivity;
import manage.JSON;
import model.Airport;
import model.Flight;
import model.Simulation;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import exceptions.InvalidAirportException;
import exceptions.InvalidFlightException;

public class MainFrame extends Frame {
	
	// Data Manager and Files
	private DataManager data;
	private CSV csv;
	private JSON json;
	
	// Thread counter
	private Inactivity inactivity;
	
	// Airport
	private TextField airportName;
	private TextField airportCode;
	private TextField airportX;
	private TextField airportY;
	private Button addAirport;
	private Label airportLabel;
	
	// Flight
	private TextField flightFrom;
	private TextField flightTo;
	private TextField flightDeparture;
	private TextField flightDuration;
	private Button addFlight;
	private Label flightLabel;
	
	// Lists
	private List airportList;
	private List flightList;
	
	// File
	private Button saveCSV;
	private Button loadCSV;
	private Button saveJSON;
	private Button loadJSON;
	
	// Map
	private MapPanel mapPanel;
	private Panel airportFilters;
	private Map<String, Checkbox> airportCheckboxes;
	
	// Phase C
	private java.util.List<Simulation> activeFlights;
	private Map<Flight, Integer> actualDepartureTimes;
	private boolean simulationRunning = false;
	private int simulationTime = 0; // In minutes
	private Button run;
	private Button pause;
	private Button reset;
	private Label simulationTimeLabel;
	private Thread simulationThread;
	
	// Map panel used to display the map
	private class MapPanel extends Panel {
		
		private Airport selectedAirport; // User selected airport (when clicked)
		private boolean blink = false; // Changes the colour of the selected airport square periodically
		
		// Constructor
		public MapPanel() {
			addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					for (Airport airport : data.getAirports()) {
						Checkbox checkbox = airportCheckboxes.get(airport.getCode());
						if (checkbox != null && !checkbox.getState()) continue;
						
						int x = convertX(airport.getX());
						int y = convertY(airport.getY());
						
						if (e.getX() >= x - 6 && e.getX() <= x + 6 && e.getY() >= y - 6 && e.getY() <= y + 6) {
							if (selectedAirport == airport) selectedAirport = null;
							else selectedAirport = airport;
							updateInactivity();
							repaint();
							break;
						}
					}
				}
				
			});
			
			Thread blinkThread = new Thread(() -> {
				
				while (true) {
					if (selectedAirport != null) {
						blink = !blink;
						repaint();
					}
					
					try {
						Thread.sleep(500);
					}
					catch (InterruptedException e) {
						break;
					}
				}
				
			});
			
			blinkThread.start();
		}
		
		private int convertX(int x) {
			int width = getWidth();
			return (int) ((x + 180) / 360.0 * width);
		}
		
		private int convertY(int y) {
			int height = getHeight();
			return (int) ((90 - y) / 180.0 * height);
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			
			for (Airport airport : data.getAirports()) {
				Checkbox checkbox = airportCheckboxes.get(airport.getCode());
				if (checkbox != null && !checkbox.getState()) continue;
				
				int x = convertX(airport.getX());
				int y = convertY(airport.getY());
				
				if (airport == selectedAirport && blink) g.setColor(Color.RED);
				else g.setColor(Color.GRAY);
				
				g.fillRect(x - 5, y - 5, 10, 10);
				g.setColor(Color.BLACK);
				g.drawString(airport.getCode(), x + 8, y + 5);
			}
			
			for (Simulation simulation : activeFlights) {
				Flight flight = simulation.getFlight();
				Airport from = data.findAirport(flight.getFrom());
				Airport to = data.findAirport(flight.getTo());
				if (from == null || to == null) continue;
				
				int x1 = convertX(from.getX());
				int y1 = convertY(from.getY());
				int x2 = convertX(to.getX());
				int y2 = convertY(to.getY());
				
				double progress = simulation.getProgress();
				int x = (int) (x1 + (x2 - x1) * progress);
				int y = (int) (y1 + (y2 - y1) * progress);
				g.setColor(Color.BLUE);
				g.fillOval(x - 5, y - 5, 10, 10);
			}
		}
		
	}
	
	private void buttonActions() {		
		addAirport.addActionListener(a -> {
			try {
				String name = airportName.getText();
				String code = airportCode.getText();
				int x = Integer.parseInt(airportX.getText());
				int y = Integer.parseInt(airportY.getText());
				
				data.addAirport(name, code, x, y);
				refreshLists();
				
				airportName.setText("");
				airportCode.setText("");
				airportX.setText("");
				airportY.setText("");
				showMessage("Airport added successfully.");
			}
			catch (NumberFormatException e) {
				showMessage("Error. X and Y must be numbers.");
			}
			catch (InvalidAirportException e) {
				showMessage(e.getMessage());
			}
		});
		
		addFlight.addActionListener(a -> {
			try {
				String from = flightFrom.getText();
				String to = flightTo.getText();
				String departure = flightDeparture.getText();
				int duration = Integer.parseInt(flightDuration.getText());
				
				data.addFlight(from, to, departure, duration);
				refreshLists();
				
				flightFrom.setText("");
				flightTo.setText("");
				flightDeparture.setText("");
				flightDuration.setText("");
				showMessage("Flight added successfully.");
			}
			catch (NumberFormatException e) {
				showMessage("Error. Duration must be a number.");
			}
			catch (InvalidFlightException e) {
				showMessage(e.getMessage());
			}
		});
		
		saveCSV.addActionListener(a -> {
			FileDialog dialog = new FileDialog(MainFrame.this, "Save CSV", FileDialog.SAVE);
			
			dialog.setFile("data.csv");
			dialog.setVisible(true);
			if (dialog.getFile() == null) return;
			File file = new File(dialog.getDirectory(), dialog.getFile());
			
			try {
				csv.save(data, file);
				showMessage("CSV file saved successfully.");
			}
			catch (IOException e) {
				showMessage("An error has occurred while saving the CSV file.");
			}
		});
		
		loadCSV.addActionListener(a -> {
			FileDialog dialog = new FileDialog(MainFrame.this, "Load CSV", FileDialog.LOAD);
			
			dialog.setVisible(true);
			if (dialog.getFile() == null) return;
			File file = new File(dialog.getDirectory(), dialog.getFile());
			
			try {
				csv.load(data, file);
				refreshLists();
				showMessage("CSV file has been loaded successfully.");
			}
			catch (IOException e) {
				showMessage("An error has occurred while opening the CSV file: " + e.getMessage());
			}
		});
		
		saveJSON.addActionListener(a -> {
			FileDialog dialog = new FileDialog(MainFrame.this, "Save JSON", FileDialog.SAVE);
			
			dialog.setFile("data.json");
			dialog.setVisible(true);
			if (dialog.getFile() == null) return;
			File file = new File(dialog.getDirectory(), dialog.getFile());
			
			try {
				json.save(data, file);
				showMessage("JSON file saved successfully.");
			}
			catch (IOException e) {
				showMessage("An error has occurred while saving the JSON file.");
			}
		});
		
		loadJSON.addActionListener(a -> {
			FileDialog dialog = new FileDialog(MainFrame.this, "Load JSON", FileDialog.LOAD);
			
			dialog.setVisible(true);
			if (dialog.getFile() == null) return;
			File file = new File(dialog.getDirectory(), dialog.getFile());
			
			try {
				json.load(data, file);
				refreshLists();
				showMessage("JSON file has been loaded successfully.");
			}
			catch (IOException e) {
				showMessage("An error has occurred while opening the JSON file: " + e.getMessage());
			}
		});
		
		run.addActionListener(a -> {
			if (simulationRunning) return;
			if (data.getAirports().isEmpty()) {
				showMessage("There are no airports in the simulation.");
				return;
			}
			if (data.getFlights().isEmpty()) {
				showMessage("There are no flights in the simulation.");
				return;
			}
			prepareFlights();
			simulationRunning = true;
			updateInactivity();
			startSimulation();
		});
		
		pause.addActionListener(a -> {
			if (!simulationRunning) return;
			simulationRunning = false;
			updateInactivity();
		});
		
		reset.addActionListener(a -> {
			simulationRunning = false;
			simulationTime = 0;
			activeFlights.clear();
			actualDepartureTimes.clear();
			simulationTimeLabel.setText("Time: 00:00");
			updateInactivity();
			mapPanel.repaint();
		});
	}
	
	// Prepares the flight simulation before the simulation starts when the 'run' button is clicked
	private void prepareFlights() {
		actualDepartureTimes.clear();
		java.util.List<Flight> flights = new ArrayList<>(data.getFlights());
		Collections.sort(flights, (a, b) -> Integer.compare(a.getDeparture(), b.getDeparture()));
		Map<String, Integer> lastDeparture = new HashMap<>();
		
		for (Flight flight : flights) {
			int plannedTime = flight.getDeparture();
			int actualTime = plannedTime;
			String airportCode = flight.getFrom();
			if (lastDeparture.containsKey(airportCode)) {
				int previousTime = lastDeparture.get(airportCode);
				if (actualTime < previousTime + 10) actualTime = previousTime + 10;
			}
			actualDepartureTimes.put(flight, actualTime);
			lastDeparture.put(airportCode, actualTime);
		}
	}
	
	// Starts the simulation
	private void startSimulation() {
		if (simulationThread != null && simulationThread.isAlive()) return;
		
		simulationThread = new Thread(() -> {
			while (simulationRunning) {
				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {
					break;
				}
				
				if (!simulationRunning) continue;
				simulationTime += 2;
				updateFlights();
				simulationTimeLabel.setText("Time: " + formatTime(simulationTime));
				mapPanel.repaint();
				
				// 24 * 60 = 1440
				if (simulationTime >= 1440) {
					simulationTime = 1440;
					simulationRunning = false;
					updateInactivity();
					simulationTimeLabel.setText("Time: 00:00");
					showMessage("Simulation has finished. Press 'Reset' to start again.");
					break;
				}
			}
		});
		
		simulationThread.start();
	}
	
	private void updateFlights() {
		for (Flight flight : data.getFlights()) {
			Integer departure = actualDepartureTimes.get(flight);
			if (departure == null) continue;
			boolean alreadyActive = false;
			
			for (Simulation active : activeFlights) {
				if (active.getFlight() == flight) {
					alreadyActive = true;
					break;
				}
			}
			
			if (!alreadyActive && simulationTime >= departure && simulationTime < departure + flight.getDuration()) {
				activeFlights.add(new Simulation(flight, departure));
			}
		}
		
		for (int i = activeFlights.size() - 1; i >= 0; i--) {
			Simulation simulation = activeFlights.get(i);
			Flight flight = simulation.getFlight();
			int departure = simulation.getDepartureTime();
			int elapsed = simulationTime - departure;
			
			if (elapsed >= flight.getDuration()) {
				activeFlights.remove(i);
			}
			else if (elapsed >= 0) {
				double progress = (double) elapsed / flight.getDuration();
				simulation.setProgress(progress);
			}
		}
	}
	
	private String formatTime(int minutes) {
		int hours = (minutes / 60) % 24;
		int mins = minutes % 60;
		return String.format("%02d:%02d", hours, mins);
	}
	
	private void updateInactivity() {
		boolean airportSelected = mapPanel != null && mapPanel.selectedAirport != null;
		inactivity.setPaused(simulationRunning || airportSelected);
	}
	
	private void populateWindow() {
		// Title on the top
		setLayout(new BorderLayout(0, 20));
		Label title = new Label("AIR TRAFFIC SIMULATION", Label.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 25));
		add(title, BorderLayout.NORTH);
		
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new GridLayout(4, 1));
		
		// First row
		Panel inputPanel = new Panel();
		inputPanel.setLayout(new GridLayout(1, 2));

		// Airport
		Panel airportPanel = new Panel();
		airportPanel.setLayout(new BorderLayout());

		Label airportTitle = new Label("AIRPORTS", Label.CENTER);
		airportTitle.setFont(new Font("Arial", Font.BOLD, 20));

		Panel airportInput = new Panel();
		airportInput.setLayout(new GridLayout(5, 2));

		airportInput.add(new Label("Name: "));
		airportName = new TextField();
		airportInput.add(airportName);

		airportInput.add(new Label("Code: "));
		airportCode = new TextField();
		airportInput.add(airportCode);

		airportInput.add(new Label("X: "));
		airportX = new TextField();
		airportInput.add(airportX);

		airportInput.add(new Label("Y: "));
		airportY = new TextField();
		airportInput.add(airportY);

		airportInput.add(new Label(""));
		airportInput.add(new Label(""));

		Panel airportTop = new Panel();
		airportTop.setLayout(new BorderLayout());
		airportTop.add(airportTitle, BorderLayout.NORTH);

		Panel airportInputCenter = new Panel();
		airportInputCenter.setLayout(new FlowLayout());
		airportInputCenter.add(airportInput);
		airportTop.add(airportInputCenter, BorderLayout.CENTER);

		Panel airportButton = new Panel();
		airportButton.setLayout(new FlowLayout());
		addAirport = new Button("Add Airport");
		airportLabel = new Label("");
		airportButton.add(addAirport);
		airportButton.add(airportLabel);
		airportTop.add(airportButton, BorderLayout.SOUTH);

		airportPanel.add(airportTop, BorderLayout.CENTER);

		// Flight
		Panel flightPanel = new Panel();
		flightPanel.setLayout(new BorderLayout());

		Label flightTitle = new Label("FLIGHTS", Label.CENTER);
		flightTitle.setFont(new Font("Arial", Font.BOLD, 20));

		Panel flightInput = new Panel();
		flightInput.setLayout(new GridLayout(5, 2));

		flightInput.add(new Label("From: "));
		flightFrom = new TextField();
		flightInput.add(flightFrom);

		flightInput.add(new Label("To: "));
		flightTo = new TextField();
		flightInput.add(flightTo);

		flightInput.add(new Label("Departure: "));
		flightDeparture = new TextField();
		flightInput.add(flightDeparture);

		flightInput.add(new Label("Duration: "));
		flightDuration = new TextField();
		flightInput.add(flightDuration);

		flightInput.add(new Label(""));
		flightInput.add(new Label(""));

		Panel flightTop = new Panel();
		flightTop.setLayout(new BorderLayout());
		flightTop.add(flightTitle, BorderLayout.NORTH);

		Panel flightInputCenter = new Panel();
		flightInputCenter.setLayout(new FlowLayout());
		flightInputCenter.add(flightInput);
		flightTop.add(flightInputCenter, BorderLayout.CENTER);

		Panel flightButton = new Panel();
		flightButton.setLayout(new FlowLayout());
		addFlight = new Button("Add Flight");
		flightLabel = new Label("");
		flightButton.add(addFlight);
		flightButton.add(flightLabel);
		flightTop.add(flightButton, BorderLayout.SOUTH);

		flightPanel.add(flightTop, BorderLayout.CENTER);

		inputPanel.add(airportPanel);
		inputPanel.add(flightPanel);
		mainPanel.add(inputPanel);
		
		// Second row
		// Map
		mapPanel = new MapPanel();
		mainPanel.add(mapPanel);
		
		// Third row
		Panel filtersAndButtons = new Panel();
		filtersAndButtons.setLayout(new GridLayout(1, 2));
		
		// Filters
		airportCheckboxes = new HashMap<>();
		airportFilters = new Panel();
		airportFilters.setLayout(new GridLayout(0, 1));
		filtersAndButtons.add(airportFilters);
		
		// Simulation buttons
		Panel simulationPanel = new Panel();
		simulationPanel.setLayout(new FlowLayout());
		
		run = new Button("Run");
		pause = new Button("Pause");
		reset = new Button("Reset");
		simulationTimeLabel = new Label("Time: 00:00");
		
		simulationPanel.add(run);
		simulationPanel.add(pause);
		simulationPanel.add(reset);
		simulationPanel.add(simulationTimeLabel);
		
		filtersAndButtons.add(simulationPanel);
		mainPanel.add(filtersAndButtons);
		
		// Fourth row
		// Lists
		Panel listsPanel = new Panel();
		listsPanel.setLayout(new GridLayout(1, 2));

		airportList = new List();
		flightList = new List();

		listsPanel.add(airportList);
		listsPanel.add(flightList);

		mainPanel.add(listsPanel);
		add(mainPanel, BorderLayout.CENTER);

		// File buttons
		Panel bottomPanel = new Panel();
		bottomPanel.setLayout(new FlowLayout());

		saveCSV = new Button("Save CSV");
		loadCSV = new Button("Load CSV");
		saveJSON = new Button("Save JSON");
		loadJSON = new Button("Load JSON");

		bottomPanel.add(saveCSV);
		bottomPanel.add(loadCSV);
		bottomPanel.add(saveJSON);
		bottomPanel.add(loadJSON);

		add(bottomPanel, BorderLayout.SOUTH);
		buttonActions();
		refreshLists();
	}
	
	private void refreshLists() {
		airportList.removeAll();
		flightList.removeAll();
		
		for (Airport airport : data.getAirports()) {
			airportList.add(airport.getCode() + " - " + airport.getName() + " (" + airport.getX() + ", " + airport.getY() + ")");
		}
		for (Flight flight : data.getFlights()) {
			flightList.add(flight.getFrom() + " -> " + flight.getTo() + " | " + flight.getDepartureTime() + " | " + flight.getDuration() + " min");
		}
		
		airportFilters.removeAll();
		airportCheckboxes.clear();
		
		for (Airport airport : data.getAirports()) {
			Checkbox checkbox = new Checkbox(airport.getCode() + " - " + airport.getName() + " (" + airport.getX() + ", " + airport.getY() + ")", true);
			checkbox.addItemListener(e -> {
				mapPanel.repaint();
			});
			airportCheckboxes.put(airport.getCode(), checkbox);
			airportFilters.add(checkbox);
		}
		
		airportFilters.validate();
		mapPanel.repaint();
	}
	
	private void showMessage(String message) {
		Dialog dialog = new Dialog(this, "Message", true);
		dialog.setLayout(new BorderLayout());
		Label label = new Label(message, Label.CENTER);
		
		Button ok = new Button("OK");
		ok.addActionListener(a -> {
			dialog.dispose();
		});
		
		dialog.add(label, BorderLayout.CENTER);
		dialog.add(ok, BorderLayout.SOUTH);
		dialog.setSize(600, 150);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
	
	public MainFrame() {
		setSize(1000, 800);
		setTitle("Air Traffic Simulation");
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		data = new DataManager();
		csv = new CSV();
		json = new JSON();

		activeFlights = new ArrayList<>();
		actualDepartureTimes = new HashMap<>();
		
		populateWindow();
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		
		inactivity = new Inactivity(this);
		inactivity.start();
		setVisible(true);
	}

}
