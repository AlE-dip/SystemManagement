package core.system;

public class Firmware {
    private String name;
    private String version;
    private String description;
    private String manufacturer;
    private String releaseDate;

    public Firmware() {
        this.name = "";
        this.version = "";
        this.description = "";
        this.manufacturer = "";
        this.releaseDate = "";
    }

    public Firmware(oshi.hardware.Firmware firmware) {
        this.name = firmware.getName();
        this.version = firmware.getVersion();
        this.description = firmware.getDescription();
        this.manufacturer = firmware.getManufacturer();
        this.releaseDate = firmware.getReleaseDate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
