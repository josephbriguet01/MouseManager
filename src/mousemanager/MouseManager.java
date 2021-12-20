/*
 * Copyright (C) JasonPercus Systems, Inc - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by JasonPercus, December 2021
 */
package mousemanager;



/**
 * This class represents the main class of the project
 * @author JasonPercus
 * @version 1.0
 */
public final class MouseManager extends com.jasonpercus.plugincreator.PluginCreator {

    

//OVERRIDED
    /**
     * Returns the author of the plugin [Required]
     * @return Returns the author of the plugin
     */
    @Override
    public String author() {
        return "JasonPercus";
    }

    /**
     * Return a general description of what the plugin does. This string is displayed to the user in the Stream Deck store [Required]
     * @return Return a general description of what the plugin does. This string is displayed to the user in the Stream Deck store
     */
    @Override
    public String description() {
        return "This plugin allows you to take control of the mouse";
    }

    /**
     * Return the name of the plugin. This string is displayed to the user in the Stream Deck store [Required]
     * @return Return the name of the plugin. This string is displayed to the user in the Stream Deck store
     */
    @Override
    public String name() {
        return "MouseManager";
    }

    /**
     * Return the version of the plugin which can only contain digits and periods. This is used for the software update mechanism [Required]
     * @return Return the version of the plugin which can only contain digits and periods. This is used for the software update mechanism
     */
    @Override
    public String version() {
        return "1.0";
    }
    
    /**
     * Returns the name of the folder where the plugin will be stored [Required]
     * @return Returns the name of the folder where the plugin will be stored
     */
    @Override
    public String folderName() {
        return "com.jasonpercus.mousemanager";
    }

    /**
     * Allows you to install the plugin at its final destination
     * @return Returns an input stream pointing to a zip file (in the classpath) containing the plugin that must be deployed. Warning: This zip file must especially not contain the executable
     */
    @Override
    public java.io.InputStream install() {
        return MouseManager.class.getResourceAsStream("resources/"+folderName()+".sdPlugin.zip");
    }
    
    
    
//MAIN
    /**
     * Corresponds to the plugin startup method
     * @param args Corresponds to the arguments provided by Stream Deck
     */
    public static void main(String[] args) {
        //Plugin Init
        com.jasonpercus.plugincreator.PluginCreator.register(args);
    }
    
    
    
}