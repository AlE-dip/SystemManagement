package core.system;

import oshi.util.EdidUtil;
import java.util.List;

public class Display {
    private String display;

    public Display() {
        display = "";
    }

    public Display(List<oshi.hardware.Display> displays) {
        StringBuilder stringBuilder = new StringBuilder();
        if (displays.isEmpty()) {
            stringBuilder.append("None detected.");
        } else {
            int i = 0;
            for (oshi.hardware.Display display : displays) {
                byte[] edid = display.getEdid();
                byte[][] desc = EdidUtil.getDescriptors(edid);
                String name = "Display " + i;
                for (byte[] b : desc) {
                    if (EdidUtil.getDescriptorType(b) == 0xfc) {
                        name = EdidUtil.getDescriptorText(b);
                    }
                }
                if (i++ > 0) {
                    stringBuilder.append('\n');
                }
                stringBuilder.append(name).append(": ");
                int hSize = EdidUtil.getHcm(edid);
                int vSize = EdidUtil.getVcm(edid);
                stringBuilder.append(String.format("%d x %d cm (%.1f x %.1f in)", hSize, vSize, hSize / 2.54, vSize / 2.54));
            }
        }
        display = stringBuilder.toString();
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
