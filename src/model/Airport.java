package model;

import exceptions.InvalidAirportException;

public class Airport {
	
	// What each airport contains:
	private String name; // Name of the airport
	private String code; // Airport code
	private int x; // X coordinate
	private int y; // Y coordinate
	
	// Constructor. The constructor checks for valid inputs
	public Airport(String name, String code, int x, int y) throws InvalidAirportException {
		if (code == null || code.trim().isEmpty()) throw new  InvalidAirportException("Error. Please enter a valid code of the airport.");
		code = code.toUpperCase().trim();
		
		if (name == null || name.trim().isEmpty()) throw new InvalidAirportException("Error. Please enter a valid name of the airport.");
		if (!code.matches("^[A-Z]{3}$")) throw new InvalidAirportException("Error. Please enter a valid code of the airport.");
		if (x < -180 || x > 180) throw new InvalidAirportException("Error. Please enter a valid X coordinate of the airport. It must be in range of -180 and 180.");
		if (y < -90 || y > 90) throw new InvalidAirportException("Error. Please enter a valid Y coordinate of the airport. It must be in range of -90 and 90.");
		
		this.name = name;
		this.code = code;
		this.x = x;
		this.y = y;
	}
	
	// Getters
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

}
