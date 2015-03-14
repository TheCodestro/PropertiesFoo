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
import java.io.OutputStream;
import java.lang.reflect.Field;
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

    public static Properties prop = new Properties();
    public static Map<String, String> mapProp;
    // Initializes Streams as null in case they fail
    private static ObjectInputStream propHash = null;
    private static InputStream propertiesFileStream = null;
    private static ObjectOutputStream outputHash = null;
    private static FileOutputStream propertiesOutStream = null;
    private static FileWriter fwriter = null;
    private static BufferedWriter buffOut = null;
    
    private static final File propFile = new File("config.properties");
    /**
     * Loads the properties file and maps it to a HashMap using Properties file.
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

            for (final String name: prop.stringPropertyNames())
                mapProp.put(name, prop.getProperty(name));
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
            if(readHash != null && readHash instanceof HashMap) {
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
    /**
     * Returns boolean whether property was correctly read. This basically is so
     * it doesn't have to check each time a property wants to be done whether it
     * is still null or whatnot, as the values are public to the package.
     *
     * @param theProperty this is the property to be read
     * @return boolean if successful
     * @throws IOException
     */
    public static boolean readProperty(String theProperty) throws IOException {

        InputStream propertiesFile = null;
        boolean successfulOrNot;
        try {
            propertiesFile = new FileInputStream("config.properties");
            // Load the file
            prop.load(propertiesFile);
            String theValue = prop.getProperty(theProperty);
            validProperties.put(theProperty, theValue);
            // propertyValue = prop.getProperty(theProperty);
            successfulOrNot = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            successfulOrNot = false;
        } finally {
            if (propertiesFile != null) {
                try {
                    propertiesFile.close();
                    successfulOrNot = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    successfulOrNot = false;
                }
            }
        }
        return successfulOrNot;
    }

    /**
     * Save the value of a given property. Not used as of 14-Feb-2015 but in
     * just in case I later add visual config.
     *
     * @param args String in form "key=val" that is in config.properties
     * @return returns True if successful, false otherwise.
     * @throws java.io.IOException
     */
    public static boolean saveProperty(String args) throws IOException {
        OutputStream output = null;
        boolean successfulOrNot = false;
        try {
            output = new FileOutputStream("config.properties");
            String[] splitArgs;
            splitArgs = args.split("=", 2);
            prop.setProperty(splitArgs[0], splitArgs[1]);
            prop.store(output, null);
            successfulOrNot = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            successfulOrNot = false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                    successfulOrNot = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    successfulOrNot = false;
                }
            }
            return successfulOrNot;
        }
    }

    /**
     * Calls read property then returns the value of the property called. This
     * is separate because in some later parts of the code it is not necessary
     * to check if reading was successful, as it would not be running at that
     * point (or shouldn't be anyway). Still will throw exception if reading
     * doesn't work. May be removed, not sure this is needed since the prop
     * variable is imported in other areas and seems to work fine.
     *
     * @param theProperty
     * @return string theValue of theProperty
     * @throws java.io.IOException
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     */
    public static String getAndReturn(String theProperty) throws IOException, NoSuchFieldException, IllegalAccessException {
        readProperty(theProperty);
        PropertiesFoo thisClass = new PropertiesFoo();
        Field f1;
        f1 = thisClass.getClass().getField(theProperty);
        String theValue;
        theValue = (String) f1.get(thisClass);
        return theValue;
    }
}
