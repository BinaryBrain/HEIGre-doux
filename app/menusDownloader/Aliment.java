package menusDownloader;

public class Aliment {
    private String name;
    private Type type;

    public Aliment(String n, Type t) {
        name = n;
        type = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String toString() {
        return type + ": " + name;
    }
}
