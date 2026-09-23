package model;

public class Simulation {
	
	// What each simulation contains:
	private Flight flight; // Flight that will be simulated
	private int departureTime; // Departing time of flight
	private double progress; // Position of the airplane while travelling
	
	// Constructor
	public Simulation(Flight flight, int departureTime) {
		this.flight = flight;
		this.departureTime = departureTime;
		progress = 0;
	}
	
	// Getters
	public Flight getFlight() {
		return flight;
	}
	
	public int getDepartureTime() {
		return departureTime;
	}
	
	public double getProgress() {
		return progress;
	}
	
	// Setter
	public void setProgress(double progress) {
		this.progress = progress;
	}

}
