package manage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import exceptions.InvalidAirportException;
import exceptions.InvalidFlightException;
import model.Airport;
import model.Flight;

public class CSV implements FileType {
	
	// Save a CSV file
	@Override
	public void save(DataManager data, File file) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(file));
		
		writer.println("# AIRPORTS");
		writer.println("CODE,NAME,X,Y");
		for (Airport airport : data.getAirports()) {
			writer.println(airport.getCode() + "," + airport.getName() + "," + airport.getX() + "," + airport.getY());
		}
		
		writer.println("");
		writer.println("# FLIGHTS");
		writer.println("FROM,TO,DEPARTURE,DURATION");
		for (Flight flight : data.getFlights()) {
			writer.println(flight.getFrom() + "," + flight.getTo() + "," + flight.getDepartureTime() + "," + flight.getDuration());
		}
		
		writer.close();
	}
	
	// Load a CSV file
	@Override
	public void load(DataManager data, File file) throws IOException {
		String line;
		boolean airports = false;
		boolean flights = false;
		data.clear();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			
			if (line.equals("# AIRPORTS")) {
				airports = true;
				flights = false;
				continue;
			}
			
			if (line.equals("# FLIGHTS")) {
				airports = false;
				flights = true;
				continue;
			}
			
			if (line.equals("CODE,NAME,X,Y") || line.equals("FROM,TO,DEPARTURE,DURATION")) {
				continue;
			}
			
			if (line.trim().isEmpty()) {
				continue;
			}
			
			String[] parts = line.split(",");
			
			if (airports) {
				if (parts.length != 4) {
					throw new IOException("Error. Invalid airport input.");
				}
				
				String code = parts[0];
				String name = parts[1];
				int x, y;
				
				try {
					x = Integer.parseInt(parts[2]);
					y = Integer.parseInt(parts[3]);
				}
				catch (NumberFormatException e) {
					throw new IOException("Error. Invalid airport coordinates.");
				}
				
				try {
					data.addAirport(name, code, x, y);
				} catch (InvalidAirportException e) {
					throw new IOException(e.getMessage());
				}
			}
			
			if (flights) {
				if (parts.length != 4) {
					throw new IOException("Error. Invalid flight input.");
				}
				
				String from = parts[0];
				String to = parts[1];
				String departure = parts[2];
				int duration;
				
				try {
					duration = Integer.parseInt(parts[3]);
				}
				catch (NumberFormatException e) {
					throw new IOException("Error. Invalid flight duration.");
				}
				
				try {
					data.addFlight(from, to, departure, duration);
				}
				catch (InvalidFlightException e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		reader.close();
	}
	
}
