package haus.lowe.quarkus.testsocket.spi.command;

public class TestMethod extends Command {
    private final String className;
    private final String methodName;

    public TestMethod(String qualifiedMethodName) {
        String[] parts = qualifiedMethodName.split("#");
        this.className = parts[0];
        this.methodName = parts[1];
    }

    public TestMethod(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String getClassName() {
        return this.className;
    }

    @Override
    public String toString() {
        return "method:"+className+"#"+methodName;
    }
}
