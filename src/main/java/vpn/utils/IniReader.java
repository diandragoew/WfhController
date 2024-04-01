package vpn.utils;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

/**
 * This class is used to extract values from properties.ini
 */
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
