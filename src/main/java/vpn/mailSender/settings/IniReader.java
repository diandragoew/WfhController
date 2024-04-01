package vpn.mailSender.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

public class IniReader {


	public static Wini properties;

	static {
		String configPath = System.getProperty("config.path");
//        File rootDirectory = new File(IniReader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		File settingsFile = new File(configPath + File.separator + "properties.ini");
		try {
			properties = new Wini(settingsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
