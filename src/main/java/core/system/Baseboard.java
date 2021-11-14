package core.system;

public class Baseboard {
    private String version;
    private String model;
    private String manufacturer;
    private String serialNumber;

    public Baseboard() {
        this.version = "";
        this.model = "";
        this.manufacturer = "";
        this.serialNumber = "";
    }

    public Baseboard(oshi.hardware.Baseboard baseboard) {
        this.version = baseboard.getVersion();
        this.model = baseboard.getModel();
        this.manufacturer = baseboard.getManufacturer();
        this.serialNumber = baseboard.getSerialNumber();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
