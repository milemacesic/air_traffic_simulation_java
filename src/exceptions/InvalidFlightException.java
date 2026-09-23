package exceptions;

public class InvalidFlightException extends Exception {
	
	// Prints out the error message for invalid inputs on flights
	public InvalidFlightException(String message) {
		super(message);
	}

}
