package net.neoforged.neoforgespi.transformation;

public interface ClassProcessorProvider {
    interface Collector {
        void add(ClassProcessor processor);
    }

    class Context {}

    void createProcessors(Context context, Collector collector);
}
