package me.fengming.vaultpatcher_asm.config;

import java.util.Objects;

public class TranslationInfo {
    protected final String targetClass;
    private final TargetClassInfo targetClassInfo;
    private final Pairs pairs;

    public TranslationInfo(String targetClass, TargetClassInfo targetClassInfo, Pairs pairs) {
        this.targetClass = targetClass;
        this.targetClassInfo = targetClassInfo;
        this.pairs = pairs;
    }

    public TranslationInfo(String targetClass) {
        this(targetClass, new TargetClassInfo(), new Pairs());
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    public TargetClassInfo getTargetClassInfo() {
        return targetClassInfo;
    }

    public Pairs getPairs() {
        return pairs;
    }

    @Override
    public String toString() {
        return "TranslationInfo{" +
                "targetClassInfo=" + targetClassInfo +
                ", pairs=" + pairs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationInfo that = (TranslationInfo) o;
        return Objects.equals(getTargetClassInfo(), that.getTargetClassInfo()) && Objects.equals(getPairs(), that.getPairs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargetClassInfo(), getPairs());
    }

    public static class Mutable extends TranslationInfo {
        private TargetClassInfo targetClassInfo;
        private Pairs pairs;

        public Mutable(String targetClass) {
            super(targetClass);
        }

        @Override
        public Pairs getPairs() {
            return this.pairs;
        }

        @Override
        public TargetClassInfo getTargetClassInfo() {
            return this.targetClassInfo;
        }

        public Mutable setPairs(Pairs pairs) {
            this.pairs = pairs;
            return this;
        }

        public Mutable setTargetClassInfo(TargetClassInfo targetClassInfo) {
            this.targetClassInfo = targetClassInfo;
            return this;
        }

        public TranslationInfo toImmutable() {
            return new TranslationInfo(this.targetClass, this.targetClassInfo, this.pairs);
        }
    }
}
