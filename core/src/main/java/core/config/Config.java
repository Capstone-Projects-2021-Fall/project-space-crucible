package core.config;

import java.io.*;
import java.util.Properties;

public class Config {
    public static Properties properties = new Properties();
    public static File file = new File("boot.config");

    public void saveText(String title, String value){
        try{
            properties.setProperty(title, value);
            properties.store(new FileOutputStream("boot.config"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getText(String title) throws IOException {
        if(file.exists()) {
            properties.load(new FileInputStream("boot.config"));
            return properties.getProperty(title);
        }
        return null;
    }
}
