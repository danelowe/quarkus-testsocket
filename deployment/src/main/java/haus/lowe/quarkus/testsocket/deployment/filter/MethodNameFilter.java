package haus.lowe.quarkus.testsocket.deployment.filter;

import org.junit.platform.engine.support.descriptor.MethodSource;

public class MethodNameFilter extends MethodSourceFilter {
    private final String className;
    private final String methodName;

    public MethodNameFilter(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    Boolean shouldBeIncluded(MethodSource methodSource) {
        return methodSource.getMethodName().equals(methodName) && methodSource.getClassName().equals(className);
    }
}
