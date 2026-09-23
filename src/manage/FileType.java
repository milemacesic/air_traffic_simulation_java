package manage;

import java.io.File;
import java.io.IOException;

// Interface that sets up the methods for working with CSV and JSON files
public interface FileType {
	
	// Saves the file
	public void save(DataManager data, File file) throws IOException;
	
	// Loads the file
	public void load(DataManager data, File file) throws IOException;

}
