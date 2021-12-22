package haus.lowe.quarkus.testsocket.spi.command;

public class TestClass extends Command {
    private final String className;

    public TestClass(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    @Override
    public String toString() {
        return "class:"+className;
    }
}
