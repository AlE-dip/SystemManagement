package core;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// First, we create the ClipboardListener to listen on the machine new things copied
// Our listener will extends Thread to run in background
public class ClipboardListener implements ClipboardOwner {
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private ClipboardChange clipboardChange;
    public boolean run;

    public ClipboardListener(ClipboardChange clipboardChange) {
        run = true;
        this.clipboardChange = clipboardChange;
        Transferable transferable = clipboard.getContents(this);
        clipboard.setContents(transferable, this);
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        if(!run) return;
        try {
            Thread.sleep(200);
        } catch (Exception e) {
        }
        Transferable contents = c.getContents(this);
        try {
            c.setContents(contents, this);
        } catch (IllegalStateException e){
            clipboardChange.onClose();
        }
        clipboardChange.onChange(contents);
    }

    public interface ClipboardChange {
        public void onChange(Transferable transferable);
        public void onClose();
    }

}