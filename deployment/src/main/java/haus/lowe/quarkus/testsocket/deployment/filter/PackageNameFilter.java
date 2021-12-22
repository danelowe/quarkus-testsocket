package haus.lowe.quarkus.testsocket.deployment.filter;

import org.junit.platform.engine.support.descriptor.MethodSource;

public class PackageNameFilter extends MethodSourceFilter {
    private final String packageName;

    public PackageNameFilter(String packageName) {
        this.packageName = packageName;
    }

    @Override
    Boolean shouldBeIncluded(MethodSource methodSource) {
        return packageName.equals("") || methodSource.getJavaClass().getPackageName().equals(packageName);
    }
}
