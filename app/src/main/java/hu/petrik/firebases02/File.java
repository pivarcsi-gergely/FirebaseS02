package hu.petrik.firebases02;

public class File {
    private String name;
    private String uri;

    public File(String name, String URI) {
        this.name = name;
        this.uri = URI;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
