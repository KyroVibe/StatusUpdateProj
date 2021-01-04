package n30b4rt.statusupdate.network;

public class File {

    private String fileName, type;
    private char[] data;

    public File(String fileName, String type, char[] data) {
        this.fileName = fileName;
        this.type = type;
        this.data = data;
    }

    public File(String fileName, String type, String data) {
        this.fileName = fileName;
        this.type = type;
        this.data = data.toCharArray();
    }

    public void setData(char[] data) {
        if (data == null)
            return; // Dude.
        this.data = data;
    }

    public void setData(String data) {
        if (data == null)
            return; // Again, dude.
        this.data = data.toCharArray();
    }

    public int getSize() { return data.length; }
    public String getFileName() { return fileName; }
    public String getType() { return type; }
    public char[] getData() { return data; }

}
