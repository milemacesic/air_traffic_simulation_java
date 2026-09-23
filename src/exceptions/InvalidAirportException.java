package exceptions;

public class InvalidAirportException extends Exception {
	
	// Prints out the error message for invalid inputs when reading an input for an airport
	public InvalidAirportException(String message) {
		super(message);
	}

}
