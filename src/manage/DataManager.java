package manage;

import java.util.ArrayList;
import java.util.List;

import exceptions.InvalidAirportException;
import exceptions.InvalidFlightException;
import model.Airport;
import model.Flight;

public class DataManager {
	
	// List of airports and flights
	private List<Airport> airports;
	private List<Flight> flights;
	
	// Constructor
	public DataManager() {
		airports = new ArrayList<>();
		flights = new ArrayList<>();
	}
	
	// Adds an airport at the end of the airport array
	public void addAirport(String name, String code, int x, int y) throws InvalidAirportException {
		if (code != null) code = code.trim();
		for (Airport airport : airports) {
			if (airport.getCode().equalsIgnoreCase(code)) throw new InvalidAirportException("Error. An airport with this code already exists.");
		}
		Airport airport = new Airport(name, code, x, y);
		airports.add(airport);
	}
	
	// Adds a flight at the end of the flight array
	public void addFlight(String from, String to, String departure, int duration) throws InvalidFlightException {
		if (findAirport(from) == null) throw new InvalidFlightException("Error. Departure airport does not exist.");
		if (findAirport(to) == null) throw new InvalidFlightException("Error. Arrival airport does not exist.");
		
		Flight flight = new Flight(from, to, departure, duration);
		flights.add(flight);
	}
	
	// Used to find the airport based on the airport code
	public Airport findAirport(String code) {
		for (Airport airport : airports) {
			if (airport.getCode().equalsIgnoreCase(code)) return airport;
		}
		return null;
	}
	
	// Clears the arrays
	public void clear() {
		airports.clear();
		flights.clear();
	}
	
	// Getters
	public List<Airport> getAirports() {
		return airports;
	}
	
	public List<Flight> getFlights() {
		return flights;
	}

}
