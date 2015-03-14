/*
 * This work is licensed under the Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License.
 * To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.thecodestro.propertiesfoo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.String.format;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @author Brian
 * @version Alfa
 */
public class PropertiesFoo {

    private static final Properties prop = new Properties();
    private static final File propFile = new File("config.properties");
    public static Map<String, String> mapProp;
    // Initializes Streams as null in case they fail
    private static ObjectInputStream propHash = null;
    private static InputStream propertiesFileStream = null;
    private static ObjectOutputStream outputHash = null;
    private static FileOutputStream propertiesOutStream = null;
    private static FileWriter fwriter = null;
    private static BufferedWriter buffOut = null;



    /**
     * Loads the properties file and maps it to a HashMap using Properties file.
     *
     * @return boolean Whether it actually managed to read it or not.
     * @throws java.io.IOException
     */
    public static boolean loadPropFile() throws IOException {

        // wasSuccess assumes it failed. Prevent later errors.
        boolean wasSuccess = false;
        try {
            // This part uses properties file/file input stream
            propertiesFileStream = new FileInputStream(propFile);
            prop.load(propertiesFileStream);

            for (final String name : prop.stringPropertyNames()) {
                mapProp.put(name, prop.getProperty(name));
            }
            wasSuccess = true;
            System.console().printf(null, "Non-serialized Properties loaded");
        } catch (IOException ex) {
            ex.printStackTrace();
            wasSuccess = false;
        } finally {
            if (propertiesFileStream != null) {
                try {
                    propertiesFileStream.close();
                    wasSuccess = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    wasSuccess = false;
                    System.console().printf(null, "Error loading non-serialized Properties");
                }
            }
        }
        return wasSuccess;
    }

    /**
     * Loads property hash, basically a serialized object.
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static boolean loadPropHash() throws IOException, ClassNotFoundException {
        boolean wasSuccess = false;
        try {
            // Second method using ObjectInputStream. Must use ObjectOutputStream to save.
            propHash = new ObjectInputStream(new FileInputStream(propFile));
            Object readHash;
            readHash = propHash.readObject();
            if (readHash != null && readHash instanceof HashMap) {
                mapProp.putAll((HashMap) readHash);
            }
            wasSuccess = true;
            System.console().printf(null, "Loaded serialized Properties");
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            wasSuccess = false;
        } finally {
            if (propHash != null) {
                try {
                    propHash.close();
                    wasSuccess = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    wasSuccess = false;
                    System.console().printf(null, "Error loading serialized properties");
                }
            }
        }
        return wasSuccess;
    }

    /**
     * Iterates through the hash and saves to file as strings.
     *
     * @param toBeSaved
     * @return
     * @throws IOException
     */
    public static boolean savePropFile(HashMap<String, String> toBeSaved) throws IOException {
        boolean wasSuccess = false;
        int count = 0;
        int max = toBeSaved.size();
        Iterator<Entry<String, String>> hashItr = toBeSaved.entrySet().iterator();

        try {
            fwriter = new FileWriter(propFile);
            buffOut = new BufferedWriter(fwriter);
            while (hashItr.hasNext() && count < max) {
                Map.Entry<String, String> pairs = hashItr.next();
                buffOut.write(pairs.getValue() + "\n");
                count++;
            }
            wasSuccess = true;
            System.console().printf(null, "Properties set to the file (not serialized).");
        } catch (IOException ex) {
            ex.printStackTrace();
            wasSuccess = false;
        } finally {
            if (buffOut != null) {
                try {
                    buffOut.close();
                    System.console().printf(null, "Non-serial Properties file closed and saved.");
                    wasSuccess = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    wasSuccess = false;
                }
            }
        }
        return wasSuccess;
    }

    /**
     * Saves serialized HashMap to a file.
     *
     * @param toBeSaved
     * @return
     * @throws java.io.IOException
     */
    public static boolean savePropHash(HashMap<String, String> toBeSaved) throws IOException {
        boolean wasSuccess = false;
        try {
            propertiesOutStream = new FileOutputStream(propFile);
            outputHash = new ObjectOutputStream(propertiesOutStream);
            outputHash.writeObject(toBeSaved);
            System.console().printf(null, "Properties hash serialized.");
            wasSuccess = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            wasSuccess = false;
        } finally {
            if (outputHash != null && propertiesOutStream != null) {
                wasSuccess = true;
                outputHash.close();
                propertiesOutStream.close();
                System.console().printf(null, "Closing Serialized File");
            }
        }
        return wasSuccess;
    }

    public static boolean save1FileProp(String theProp, String theValue) throws IOException {
        boolean wasSuccess = false;
        // TODO: Save a single property to the file, not serialized.
        return wasSuccess;
    }

    public static boolean save1SerProp(String theProp, String theValue) throws IOException {
        boolean wasSuccess = false;
        // TODO: Save a single property to the file, serialized. Might require reloading/saving.
        return wasSuccess;
    }

    /**
     * Gets a property and returns it as a string. Returns an empty string if
     * the property does not exist.
     *
     * @param theProp
     * @return
     */
    public static String getProp(String theProp) {
        String theValue;
        if (mapProp.containsKey(theProp)) {
            theValue = mapProp.get(theProp);
        } else {
            theValue = "";
        }
        return theValue;
    }

    /**
     * Checks if the property exists, if so, sets it. Otherwise, returns error.
     *
     * @param theProp
     * @param theValue
     * @return
     */
    public static boolean setProp(String theProp, String theValue) {
        boolean wasSuccess;
        if (mapProp.containsKey(theProp)) {
            mapProp.put(theProp, theValue);
            wasSuccess = true;
        } else {
            String errorString = format("The property %s was not found", theProp);
            System.console().printf(null, errorString);
            wasSuccess = false;
        }
        return wasSuccess;
    }

}
