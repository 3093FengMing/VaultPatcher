package net.neoforged.neoforgespi.transformation;

public class ProcessorName {
    private final String namespace;
    private final String path;

    public ProcessorName(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String namespace() {
        return namespace;
    }

    public String path() {
        return path;
    }
}
