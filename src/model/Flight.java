package model;

import exceptions.InvalidFlightException;

public class Flight {
	
	// What each flight contains:
	private String from; // Airport code
	private String to; // Airport code
	private int departure; // Minutes since 00:00
	private int duration; // In minutes
	
	// Constructor. The constructor checks for valid inputs
	public Flight(String from, String to, String departure, int duration) throws InvalidFlightException {
		if (from == null || from.trim().isEmpty()) throw new InvalidFlightException("Error. Please enter a valid code for the airport that the flight is departing from.");
		if (to == null || to.trim().isEmpty()) throw new InvalidFlightException("Error. Please enter a valid code for the airport that the flight is arriving at.");
		from = from.toUpperCase().trim();
		to = to.toUpperCase().trim();
		
		if (!from.matches("^[A-Z]{3}$")) throw new InvalidFlightException("Error. Please enter a valid code for the airport that the flight is departing from.");
		if (!to.matches("^[A-Z]{3}$")) throw new InvalidFlightException("Error. Please enter a valid code for the airport that the flight is arriving at.");
		if (from.equals(to)) throw new InvalidFlightException("Error. A flight can not depart and arrive at the same airport.");
		if (duration <= 0) throw new InvalidFlightException("Error. Please enter a valid duration of the flight.");
		
		this.from = from;
		this.to = to;
		this.departure = parseTime(departure);
		this.duration = duration;
	}
	
	// Parses the departing time (converts to minutes)
	private int parseTime(String departure) throws InvalidFlightException {
		if (departure == null || departure.trim().isEmpty() || !departure.trim().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) throw new InvalidFlightException("Error. Please enter a valid format for the departure time.");
		
		String[] parts = departure.split(":");
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		return hours * 60 + minutes;
	}
	
	// Getters
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	
	public int getDeparture() {
		return departure;
	}
	
	public int getDuration() {
		return duration;
	}
	
	// Returns the departure time in valid format
	public String getDepartureTime() {
		int hours = departure / 60;
		int minutes = departure % 60;
		return String.format("%02d:%02d", hours, minutes);
	}

}
