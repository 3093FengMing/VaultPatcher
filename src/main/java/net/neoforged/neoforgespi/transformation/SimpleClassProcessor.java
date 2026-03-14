package net.neoforged.neoforgespi.transformation;

import org.objectweb.asm.tree.ClassNode;

import java.util.Set;

public abstract class SimpleClassProcessor implements ClassProcessor {
    public abstract void transform(ClassNode input, SimpleTransformationContext context);

    public abstract Set<Target> targets();

    public static class Target {
        private final String className;

        public Target(String className) {
            this.className = className;
        }

        public String className() {
            return className;
        }
    }
}
