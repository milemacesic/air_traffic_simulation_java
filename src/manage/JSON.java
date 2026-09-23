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

public class JSON implements FileType {
	
	// Save a JSON file
	@Override
	public void save(DataManager data, File file) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(file));
		
		writer.println("{");
		writer.println("\"airports\":[");
		for (int i = 0; i < data.getAirports().size(); i++) {
			Airport airport = data.getAirports().get(i);
			
			writer.print("{");
			writer.print("\"code\":\"" + airport.getCode() + "\",");
			writer.print("\"name\":\"" + airport.getName() + "\",");
			writer.print("\"x\":" + airport.getX() + ",");
			writer.print("\"y\":" + airport.getY() + "}");
			
			if (i != data.getAirports().size() - 1) {
				writer.print(",");
			}
			else {
				writer.println();
			}
		}
		
		writer.println("],");
		writer.println("\"flights\":[");
		for (int i = 0; i < data.getFlights().size(); i++) {
			Flight flight = data.getFlights().get(i);
			
			writer.print("{");
			writer.print("\"from\":\"" + flight.getFrom() + "\",");
			writer.print("\"to\":\"" + flight.getTo() + "\",");
			writer.print("\"departure\":\"" + flight.getDepartureTime() + "\",");
			writer.print("\"duration\":" + flight.getDuration() + "}");
			
			if (i != data.getFlights().size() - 1) {
				writer.print(",");
			}
			else {
				writer.println();
			}
		}
		
		writer.println("]");
		writer.println("}");
		writer.close();
	}
	
	// Load a JSON file
	@Override
	public void load(DataManager data, File file) throws IOException {
		String line;
		boolean airports = false;
		boolean flights = false;
		data.clear();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			
			if (line.equals("\"airports\":[")) {
				airports = true;
				flights = false;
				continue;
			}
			
			if (line.equals("\"flights\":[")) {
				airports = false;
				flights = true;
				continue;
			}
			
			if (!line.startsWith("{") || line.equals("{")) {
				continue;
			}
			
			line = line.replace("{", "");
			line = line.replace("}", "");
			line = line.replace(",", ", ");
			String[] parts = line.split(", ");
			
			if (airports) {
				String code = "";
				String name = "";
				int x = 0;
				int y = 0;
				
				for (String part : parts) {
					String[] divide = part.split(":", 2);
					if (divide.length != 2) continue;
					
					String key = divide[0].replace("\"", "").trim();
					String value = divide[1].replace("\"", "").trim();
					
					try {
						if (key.equals("code")) code = value;
						else if (key.equals("name")) name = value;
						else if (key.equals("x")) x = Integer.parseInt(value);
						else if (key.equals("y")) y = Integer.parseInt(value);
					}
					catch (NumberFormatException e) {
						throw new IOException("Error. Invalid airport coordinates.");
					}
				}
				
				try {
					data.addAirport(name, code, x, y);
				}
				catch (InvalidAirportException e) {
					throw new IOException(e.getMessage());
				}
			}
			
			if (flights) {
				String from = "";
				String to = "";
				String departure = "";
				int duration = 0;
				
				for (String part : parts) {
					String[] divide = part.split(":", 2);
					if (divide.length != 2) continue;
					
					String key = divide[0].replace("\"", "").trim();
					String value = divide[1].replace("\"", "").trim();
					
					if (key.equals("from")) from = value;
					else if (key.equals("to")) to = value;
					else if (key.equals("departure")) departure = value;
					else if (key.equals("duration")) {
						try {
							duration = Integer.parseInt(value);
						}
						catch (NumberFormatException e) {
							throw new IOException("Error. Invalid duration input.");
						}
					}
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
