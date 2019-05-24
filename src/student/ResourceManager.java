package student;

import java.util.ResourceBundle;

public class ResourceManager {

	public static String getConnectionString() {
		return ResourceBundle.getBundle("params").getString("connectionString");
	}
	
}
