/*
 * Copyright (C) JasonPercus Systems, Inc - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by JasonPercus, December 2021
 */
package mousemanager;



import com.google.gson.GsonBuilder;
import com.jasonpercus.plugincreator.exceptions.ErrorContextException;
import com.jasonpercus.plugincreator.models.Context;
import com.jasonpercus.plugincreator.models.Target;
import com.jasonpercus.plugincreator.models.events.KeyDown;
import com.jasonpercus.plugincreator.models.events.KeyUp;
import com.jasonpercus.util.File;



/**
 * Allows control of mouse tiles
 * @author JasonPercus
 * @version 1.0
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public class MyManager extends com.jasonpercus.plugincreator.EventManager {

    
    
//ATTRIBUTS
    /**
     * This object allows mouse control
     */
    private java.awt.Robot robot;
    
    /**
     * Map contexts with the date the action is pressed
     */
    private java.util.HashMap<Context, java.util.Date> PRESSED;

    
    
//ON
    /**
     * When the EventManager has been created
     */
    @Override
    public void onCreate() {
        this.PRESSED = new java.util.HashMap<>();
        try {
            this.robot = new java.awt.Robot();
        } catch (java.awt.AWTException ex) {
            log(ex);
        }
    }
    
    
    
//EVENTS
    /**
     * When the user presses a key, the plugin will receive the keyDown event
     * @param event Corresponds to the Stream Deck event
     * @param context Corresponds to the context (or ID) of the action
     * @param builder Allows to deserialize the received json
     */
    @Override
    public void keyDown(KeyDown event, Context context, GsonBuilder builder) {
        if(event.action.equals("position") || event.action.equals("picker")){
            synchronized(PRESSED){
                java.util.Date start = PRESSED.get(context);
                if(start == null){
                    PRESSED.put(context, new java.util.Date());
                }else{
                    PRESSED.replace(context, new java.util.Date());
                }
            }
        }
    }
    
    /**
     * When the user releases a key, the plugin will receive the keyUp event
     * @param event Corresponds to the Stream Deck event
     * @param context Corresponds to the context (or ID) of the action
     * @param builder Allows to deserialize the received json
     */
    @Override
    public void keyUp(KeyUp event, Context context, GsonBuilder builder) {
        new Thread(() -> {
            Settings settings = builder.create().fromJson(event.payload.settings, Settings.class);
            switch(event.action){
                case "press":
                    try{
                        int buttonPressed = getMaskButton(getButton(settings));
                        waitBefore(settings);
                        robot.mousePress(buttonPressed);
                        waitAfter(settings);
                    }catch(Exception e){
                        log(e);
                    }
                    break;
                case "release":
                    try{
                        int buttonReleased = getMaskButton(getButton(settings));
                        waitBefore(settings);
                        robot.mouseRelease(buttonReleased);
                        waitAfter(settings);
                    }catch(Exception e){
                        log(e);
                    }
                    break;
                case "move":
                    try{
                        int x = getXValue(settings);
                        int y = getYValue(settings);
                        waitBefore(settings);
                        robot.mouseMove(x, y);
                        waitAfter(settings);
                    }catch(Exception e){
                        log(e);
                    }
                    break;
                case "click":
                    try{
                        int buttonClicked = getMaskButton(getButton(settings));
                        waitBefore(settings);
                        robot.mousePress(buttonClicked);
                        robot.mouseRelease(buttonClicked);
                        waitAfter(settings);
                    }catch(Exception e){
                        log(e);
                    }
                    break;
                case "wheel":
                    try{
                        int unit = getUnitRotationValue(settings);
                        waitBefore(settings);
                        robot.mouseWheel(unit);
                        waitAfter(settings);
                    }catch(Exception e){
                        log(e);
                    }
                    break;
                case "position":
                    if (isLongClick(context)) {
                        try {
                            setImage(context, new File("images/position.png"), Target.BOTH);
                        } catch (ErrorContextException | NullPointerException | java.io.FileNotFoundException ex) {
                            log(ex);
                        }
                    } else {
                        java.awt.Point b = java.awt.MouseInfo.getPointerInfo().getLocation();
                        setImage(context, (java.awt.Graphics2D graphic) -> {
                            graphic.setBackground(java.awt.Color.BLACK);
                            graphic.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 12));
                            graphic.drawString("x: " + on4Digits((int) b.getX()), 12, 30);
                            graphic.drawString("y: " + on4Digits((int) b.getY()), 12, 52);
                        }, Target.BOTH);
                    }
                    break;
                case "picker":
                    if (isLongClick(context)) {
                        try {
                            setImage(context, new File("images/picker.png"), Target.BOTH);
                        } catch (ErrorContextException | NullPointerException | java.io.FileNotFoundException ex) {
                            log(ex);
                        }
                    } else {
                        java.awt.Point c = java.awt.MouseInfo.getPointerInfo().getLocation();
                        int x = (int) c.getX();
                        int y = (int) c.getY();
                        java.awt.Color color = robot.getPixelColor(x, y);
                        java.awt.Color textColor;
                        int add = color.getRed() + color.getGreen() + color.getBlue();
                        if(add < 384)
                            textColor = java.awt.Color.WHITE;
                        else
                            textColor = java.awt.Color.BLACK;
                        setImage(context, (java.awt.Graphics2D graphic) -> {
                            graphic.setColor(color);
                            graphic.fillRect(0, 0, 72, 72);
                            graphic.setColor(textColor);
                            graphic.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 14));
                            graphic.drawString(("#" + on2Digits(Integer.toHexString(color.getRed())) + on2Digits(Integer.toHexString(color.getGreen())) + on2Digits(Integer.toHexString(color.getBlue()))).toUpperCase(), 10, 20);
                            graphic.drawLine(0, 27, 72, 27);
                            graphic.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 12));
                            graphic.drawString("R: "+color.getRed(), 16, 43);
                            graphic.drawString("G: "+color.getGreen(), 16, 53);
                            graphic.drawString("B: "+color.getBlue(), 16, 63);
                        }, Target.BOTH);
                    }
                    break;
            }
        }).start();
    }
    
    
    
//METHODES PRIVATES
    /**
     * Determines whether it is a long click or not
     * @param context Corresponds to the searched context
     * @return Returns true if it does, otherwise false
     */
    private boolean isLongClick(Context context){
        long startClickPosition;
        long endClickPosition = new java.util.Date().getTime();
        synchronized (PRESSED) {
            java.util.Date scp = PRESSED.get(context);
            if (scp == null) {
                startClickPosition = -1;
            } else {
                startClickPosition = scp.getTime();
            }
            PRESSED.remove(context);
        }
        return (startClickPosition > -1 && (endClickPosition - startClickPosition) >= 2000);
    }
    
    /**
     * Returns the number, displayed as 4 characters
     * @param number Corresponds to the number to display
     * @return Returns the number, displayed as 4 characters
     */
    private String on4Digits(int number){
        String n = ""+number;
        int diff = 4 - n.length();
        if(diff<1) return n;
        else{
            String chain = "";
            for(int i=0;i<diff;i++)
                chain += " ";
            return chain += n;
        }
    }
    
    /**
     * Returns the value, displayed as 2 characters
     * @param value Corresponds to the value to display
     * @return Returns the value, displayed as 2 characters
     */
    private String on2Digits(String value){
        if(value.length() == 1) return "0"+value;
        else return value;
    }
    
    /**
     * Returns the value waitBefore
     * @param settings Corresponds to the settings received
     */
    private void waitBefore(Settings settings){
        if (getWaitBefore(settings)) {
            try {
                Thread.sleep(getWaitBeforeValue(settings));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Returns the value waitAfter
     * @param settings Corresponds to the settings received
     */
    private void waitAfter(Settings settings){
        if (getWaitAfter(settings)) {
            try {
                Thread.sleep(getWaitAfterValue(settings));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Returns the mask of the button to use
     * @param button Corresponds to the button number
     * @return Returns the mask of the button to use
     */
    private int getMaskButton(int button) {
        switch(button){
            case 1:
                return java.awt.event.InputEvent.BUTTON1_MASK;
            case 2:
                return java.awt.event.InputEvent.BUTTON2_MASK;
            case 3:
                return java.awt.event.InputEvent.BUTTON3_MASK;
        }
        return java.awt.event.InputEvent.BUTTON1_MASK;
    }
    
    /**
     * Returns the button number
     * @param settings Corresponds to the settings received
     * @return Returns the button number
     */
    private int getButton(Settings settings){
        return Integer.parseInt(settings.buttonValue);
    }
    
    /**
     * Determines if there is a wait time before
     * @param settings Corresponds to the settings received
     * @return Returns true if it does, otherwise false
     */
    private boolean getWaitBefore(Settings settings){
        if(settings.waitBefore == null) return false;
        boolean b = Boolean.parseBoolean(settings.waitBefore);
        return b;
    }
    
    /**
     * Returns the waiting time
     * @param settings Corresponds to the settings received
     * @return Returns the waiting time
     */
    private int getWaitBeforeValue(Settings settings){
        if(settings.waitBeforeValue == null) return 0;
        try{
            int i = Integer.parseInt(settings.waitBeforeValue);
            return i;
        }catch(Exception e){
            return 0;
        }
    }
    
    /**
     * Determines if there is a wait time after
     * @param settings Corresponds to the settings received
     * @return Returns true if it does, otherwise false
     */
    private boolean getWaitAfter(Settings settings){
        if(settings.waitAfter == null) return false;
        boolean b = Boolean.parseBoolean(settings.waitAfter);
        return b;
    }
    
    /**
     * Returns the waiting time
     * @param settings Corresponds to the settings received
     * @return Returns the waiting time
     */
    private int getWaitAfterValue(Settings settings){
        if(settings.waitAfterValue == null) return 0;
        try{
            int i = Integer.parseInt(settings.waitAfterValue);
            return i;
        }catch(Exception e){
            return 0;
        }
    }
    
    /**
     * Returns position x
     * @param settings Corresponds to the settings received
     * @return Returns position x
     */
    private int getXValue(Settings settings){
        return Integer.parseInt(settings.xValue);
    }
    
    /**
     * Returns position y
     * @param settings Corresponds to the settings received
     * @return Returns position y
     */
    private int getYValue(Settings settings){
        return Integer.parseInt(settings.yValue);
    }
    
    /**
     * Returns the size of the spinner unit of the wheel
     * @param settings Corresponds to the settings received
     * @return Returns the size of the spinner unit of the wheel
     */
    private int getUnitRotationValue(Settings settings){
        return Integer.parseInt(settings.unitRotationValue);
    }
    
    
    
//CLASS
    /**
     * This class represents the data received from the inspector
     * @author JasonPercus
     * @version 1.0
     */
    private class Settings {
        
        /**
         * Corresponds to the number of the selected button
         */
        public String buttonValue;
        
        /**
         * Determines if there is a wait time before
         */
        public String waitBefore;
        
        /**
         * Returns the time value to wait before
         */
        public String waitBeforeValue;
        
        /**
         * Determines if there is a wait time after
         */
        public String waitAfter;
        
        /**
         * Returns the time value to wait after
         */
        public String waitAfterValue;
        
        /**
         * Returns the x position that the mouse should have
         */
        public String xValue;
        
        /**
         * Returns the y position that the mouse should have
         */
        public String yValue;
        
        /**
         * Returns the spinning unit of the wheel
         */
        public String unitRotationValue;
        
    }
    
    
    
}