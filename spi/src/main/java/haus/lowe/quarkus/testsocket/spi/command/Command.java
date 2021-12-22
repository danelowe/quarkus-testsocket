package haus.lowe.quarkus.testsocket.spi.command;

public class Command {
    public static Command fromString(String string) {
        int index = string.indexOf(":");
        if (index > 0) {
            String type = string.substring(0, index);
            String filter = string.substring(index+1);
            switch (type) {
                case "class":
                    return new TestClass(filter);
                case "method":
                    return new TestMethod(filter);
                case "package":
                    return new TestPackage(filter);
            }
        }
        throw new IllegalArgumentException("Could not find command for: "+string);
    }
}
