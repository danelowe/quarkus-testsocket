package haus.lowe.quarkus.testsocket.spi.command;

public class TestPackage extends Command {
    private final String packageName;

    public TestPackage(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public String toString() {
        return "package:"+packageName;
    }
}
