package haus.lowe.quarkus.testsocket.deployment.filter;

import org.junit.platform.engine.support.descriptor.MethodSource;

public class ClassNameFilter extends MethodSourceFilter {
    private final String className;

    public ClassNameFilter(String className) {
        this.className = className;
    }

    @Override
    Boolean shouldBeIncluded(MethodSource methodSource) {
        System.out.println(className);
        System.out.println(methodSource.getJavaClass().getName());
        System.out.println(methodSource.getJavaClass().getName().equals(className));

        return methodSource.getJavaClass().getName().equals(className);
    }
}
