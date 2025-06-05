/*
 *
 */
package gameengine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 
 * @author Paul Kappmeyer
 * 
 */

public final class KeyboardInputManager implements KeyListener {

	private static boolean[] keys = new boolean[1024];

	public static void setKey(int keyCode, boolean value) {
		if(keyCode >= 0 && keyCode < keys.length && keys[keyCode]) {
			keys[keyCode] = value;
		}
	}

	public static boolean isKeyDown(int keyCode){
		if(keyCode >= 0 && keyCode < keys.length && keys[keyCode]) {
			return true;
		}
		else return false;
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		if(keyCode >= 0 && keyCode < keys.length) keys[keyCode] = true;
	}
	@Override
	public void keyReleased(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		if(keyCode >= 0 && keyCode < keys.length) keys[keyCode] = false;
	}
	@Override
	public void keyTyped(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		if(keyCode >= 0 && keyCode < keys.length) keys[keyCode] = false;
	}	
	
}
