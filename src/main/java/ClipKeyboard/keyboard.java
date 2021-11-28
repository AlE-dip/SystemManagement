package ClipKeyboard;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class keyboard implements NativeKeyListener {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GlobalScreen.addNativeKeyListener(new keyboard());
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
	}
	@Override
	public void nativeKeyPressed(NativeKeyEvent agr0) {
		System.out.println(NativeKeyEvent.getKeyText(agr0.getKeyCode()));
		
		
	}

}
