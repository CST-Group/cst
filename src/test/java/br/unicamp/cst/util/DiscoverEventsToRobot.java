/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * **********************************************************************************************/
package br.unicamp.cst.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

/**
 *
 * @author rgudwin
 */
public class DiscoverEventsToRobot implements NativeKeyListener, NativeMouseInputListener, NativeMouseWheelListener {
    
    String robotName;
    boolean printinfo=false;
    boolean printinstruction=true;
    private static final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    
    /**
     * This method can be used to collect mouse events in order to test GUI interfaces.
     * In order to use it, a user must create an instance of this class passing the
     * name of a Robot variable, which will later reproduce the behavior collected from 
     * the user. After that, the user must put the test to run, and click on the desired
     * points in the GUI. The code to be inserted for the robot is printed as an output.
     * See an example in the MemoryInspectorTest code. 
     * 
     * @param robotname the Robot name to reproduce the mouse movements
     */    
    public DiscoverEventsToRobot(String robotname)  {
        robotName = robotname;
        logger.setLevel(Level.OFF);

	GlobalScreen.setEventDispatcher(new SwingDispatchService());
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            Logger.getLogger(DiscoverEventsToRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
        GlobalScreen.addNativeMouseListener(this);
	GlobalScreen.addNativeMouseMotionListener(this);
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseWheelListener(this);
    }
    
    /**
     * This method enables printing the information about captured events.
     * It is originally disabled. Can be enabled using this method
     * @param value Enable of disable the printing of information about the captured events
     */
    public void setPrintInfo(boolean value) {
        printinfo = value;
    }
    
    /**
     * This method enables printing the instructions related to each captured event.
     * It is originally enabled. Can be disabled using this method. 
     * @param value Enable or disable the printing of instructions
     */
    public void setPrintInstruction(boolean value) {
        printinstruction = value;
    }
    
    private void mouseMove(int x,int y) {
        System.out.println(robotName+".mouseMove("+x+","+y+");");
    }
    
    private void mouseWheel(int amount) {
        System.out.println(robotName+".mouseWheel("+amount+");");
    }
    
    private void mousePress() {
        System.out.println(robotName+".mousePress(InputEvent.BUTTON1_DOWN_MASK);");
    }
    
    private void mouseRelease() {
        System.out.println(robotName+".mouseRelease(InputEvent.BUTTON1_DOWN_MASK);");
    }
    
    private void keyPress(int keycode) {
        System.out.println(robotName+".keyPress("+keycode+");");
    }
    
    private void keyRelease(int keycode) {
        System.out.println(robotName+".keyPress("+keycode+");");
    }
            
    private void delay(int milisec) {
        System.out.println(robotName+".delay("+milisec+");");
    }

    /**
     * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyPressed(org.jnativehook.keyboard.NativeKeyEvent)
     */
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (printinstruction) keyPress(e.getKeyCode());
        if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyReleased(org.jnativehook.keyboard.NativeKeyEvent)
     */
    public void nativeKeyReleased(NativeKeyEvent e) {
	if (printinstruction) keyRelease(e.getKeyCode());
        if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyTyped(org.jnativehook.keyboard.NativeKeyEvent)
     */
    public void nativeKeyTyped(NativeKeyEvent e) {
    	if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.mouse.NativeMouseListener#nativeMouseClicked(org.jnativehook.mouse.NativeMouseEvent)
     */
    public void nativeMouseClicked(NativeMouseEvent e) {
        if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.mouse.NativeMouseListener#nativeMousePressed(org.jnativehook.mouse.NativeMouseEvent)
     */
    public void nativeMousePressed(NativeMouseEvent e) {
        if (printinstruction) {
            mouseMove(e.getX(),e.getY());
            mousePress();
        }
        if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.mouse.NativeMouseListener#nativeMouseReleased(org.jnativehook.mouse.NativeMouseEvent)
     */
    public void nativeMouseReleased(NativeMouseEvent e) {
        if (printinstruction) {
            mouseMove(e.getX(),e.getY());
            mouseRelease();
        }
        if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.mouse.NativeMouseMotionListener#nativeMouseMoved(org.jnativehook.mouse.NativeMouseEvent)
     */
    public void nativeMouseMoved(NativeMouseEvent e) {
        if (printinstruction) mouseMove(e.getX(),e.getY());
        if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.mouse.NativeMouseMotionListener#nativeMouseDragged(org.jnativehook.mouse.NativeMouseEvent)
     */
    public void nativeMouseDragged(NativeMouseEvent e) {
        if (printinstruction) mouseMove(e.getX(),e.getY());
        if (printinfo) displayEventInfo(e);
    }

    /**
     * @see org.jnativehook.mouse.NativeMouseWheelListener#nativeMouseWheelMoved(org.jnativehook.mouse.NativeMouseWheelEvent)
     */
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
    	if (printinstruction) mouseWheel(e.getScrollAmount());
        if (printinfo) displayEventInfo(e);
    }

    /**
     * Write information about the <code>NativeInputEvent</code> to the text
     * window.
     *
     * @param e the native input event to display.
     */
    private void displayEventInfo(final NativeInputEvent e) {
        System.out.println(e.paramString());
	}

}