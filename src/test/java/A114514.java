public class A114514 {
    /// WARNING: you should manually try-catch IllegalArgumentException
    public static void test(int givenDepth, String className) throws IllegalArgumentException {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        //Preconditions.checkArgument(givenDepth < stackTraceElements.length, "Too deep: %d", givenDepth);
        if (givenDepth >= stackTraceElements.length) return;    // may not match, should not throw
        StackTraceElement element = stackTraceElements[givenDepth];
        if (className.equals(element.getClassName())) {
            // modify it
        }
    }
}
