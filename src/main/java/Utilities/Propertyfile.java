package Utilities;


import java.io.IOException;


public class Propertyfile {

    private static PropertyFactory propertyFactory;

    public static PropertyFactory getDBProperty() {
        try {
            if(propertyFactory==null) {
                propertyFactory = new PropertyFactory();
                propertyFactory.loadProperties("Connection");
            }
            return propertyFactory;
        }
        catch (IOException e) {
            return null;
        }
    }



}
