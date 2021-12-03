package core;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class KeyboardListener implements NativeKeyListener {

	private KeyboardPress keyboardPress;

	public KeyboardListener(KeyboardPress keyboardPress) {
		GlobalScreen.addNativeKeyListener(this);
		this.keyboardPress = keyboardPress;
	}

	public void unregister() throws NativeHookException {
		GlobalScreen.removeNativeKeyListener(this);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
		keyboardPress.onPress(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()));
	}

	public interface KeyboardPress{
		public void onPress(String data);
	}

}
