package Utilities;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;


public class PropertyFactory {

    private Properties masterProp = null;
    private String propertyFileName = null;
    String fileloc = "";

    public PropertyFactory() {
    }

    public PropertyFactory(String property) throws IOException {
        this.setProperty(property);
        loadProperties(property);
    }

    public void loadProperties(String property) throws IOException {

        masterProp = new Properties();
        fileloc = property + ".properties";
        System.out.println(fileloc);
        //Changing way of loading file from FileInputStream to getClassloader().getResourceAsStream()
        InputStream fis = getClass().getClassLoader().getResourceAsStream(fileloc);
        if(fis == null) {
            fis = new FileInputStream(new File(fileloc));
        }
        masterProp.load(fis);
    }

    public String getProperty(String propValue) {

        return this.masterProp.getProperty(propValue);
    }

    void loadMasterProperties(String property) {
        masterProp = new Properties();

        // Set config file path here..
        String fileLoc = property + ".properties";
        System.out.println("Property file path : " + fileloc);
        try {
            File f = new File(fileLoc);
            FileInputStream fis = new FileInputStream(f);
            masterProp.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProperty() {
        return propertyFileName;
    }

    public void setProperty(String propertyFileName) {
        this.propertyFileName = propertyFileName;
    }

    public void setPropertyValue(ArrayList<String[]> output) throws IOException {
        URL path = getClass().getClassLoader().getResource(fileloc);
        OutputStream out=new FileOutputStream(path.getPath());

        for(String[] array:output)
        {
            this.masterProp.setProperty(array[0], array[1]);
            System.out.println(this.masterProp.setProperty(array[0], array[1]));
        }
        this.masterProp.store(out, "");
        out.close();
    }

    public void setProperty(String key,String value) {

        this.masterProp.setProperty(key, value);
    }
}
