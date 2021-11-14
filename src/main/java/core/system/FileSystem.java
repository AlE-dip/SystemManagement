package core.system;

import oshi.software.os.OSFileStore;

import java.util.ArrayList;

public class FileSystem {
    private ArrayList<FileStore> fileStores;

    public FileSystem() {
        this.fileStores = null;
    }

    public FileSystem(oshi.software.os.FileSystem fileSystem) {
        this.fileStores = new ArrayList<>();
        for(OSFileStore fileStore: fileSystem.getFileStores()){
            this.fileStores.add(new FileStore(fileStore));
        }
    }

    public void refresh(oshi.software.os.FileSystem fileSystem){
        this.fileStores.clear();
        for(OSFileStore fileStore: fileSystem.getFileStores()){
            this.fileStores.add(new FileStore(fileStore));
        }
    }

    public ArrayList<FileStore> getFileStores() {
        return fileStores;
    }

    public void setFileStores(ArrayList<FileStore> fileStores) {
        this.fileStores = fileStores;
    }
}
