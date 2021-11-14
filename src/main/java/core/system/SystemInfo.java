package core.system;

import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;

public class SystemInfo {
    private String model;
    private String manufacturer;
    private String serialNumber;
    private String hardwareUUID;
    private Firmware firmware;
    private Baseboard baseboard;
    private Display display;
    private FileSystem fileSystem;
    private Memory memory;
    private OperatingSystem operatingSystem;
    private Processor processor;
    private Network network;
    private oshi.SystemInfo systemInfo;


    public SystemInfo() {
        model = "";
        manufacturer = "";
        serialNumber = "";
        hardwareUUID = "";
        firmware = null;
        baseboard = null;
        display = null;
        fileSystem = null;
        memory = null;
        operatingSystem = null;
        processor = null;
        network = null;
    }

    public SystemInfo(oshi.SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        oshi.software.os.OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();

        model = computerSystem.getModel();
        manufacturer = computerSystem.getManufacturer();
        serialNumber = computerSystem.getSerialNumber();
        hardwareUUID = computerSystem.getHardwareUUID();
        firmware = new Firmware(computerSystem.getFirmware());
        baseboard = new Baseboard(computerSystem.getBaseboard());
        display = new Display(hardwareAbstractionLayer.getDisplays());
        fileSystem = new FileSystem(operatingSystem.getFileSystem());
        memory = new Memory(hardwareAbstractionLayer.getMemory());
        this.operatingSystem = new OperatingSystem(operatingSystem);
        processor = new Processor(hardwareAbstractionLayer.getProcessor());
        network = new Network(operatingSystem.getNetworkParams(), hardwareAbstractionLayer.getNetworkIFs());
    }

    public void refresh() {
        HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
        oshi.software.os.OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();

        fileSystem.refresh(operatingSystem.getFileSystem());
        memory.refresh(hardwareAbstractionLayer.getMemory());
        this.operatingSystem.refresh(operatingSystem);
        processor.refresh(hardwareAbstractionLayer.getProcessor());
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

    public String getHardwareUUID() {
        return hardwareUUID;
    }

    public void setHardwareUUID(String hardwareUUID) {
        this.hardwareUUID = hardwareUUID;
    }

    public Firmware getFirmware() {
        return firmware;
    }

    public void setFirmware(Firmware firmware) {
        this.firmware = firmware;
    }

    public Baseboard getBaseboard() {
        return baseboard;
    }

    public void setBaseboard(Baseboard baseboard) {
        this.baseboard = baseboard;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
