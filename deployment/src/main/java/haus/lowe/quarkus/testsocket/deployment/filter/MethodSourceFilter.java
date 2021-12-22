package haus.lowe.quarkus.testsocket.deployment.filter;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.PostDiscoveryFilter;

abstract class MethodSourceFilter implements PostDiscoveryFilter {

    abstract Boolean shouldBeIncluded(MethodSource methodSource);

    @Override
    public FilterResult apply(TestDescriptor testDescriptor) {
        if (testDescriptor.getSource().isPresent() && testDescriptor.getSource().get() instanceof MethodSource) {
            MethodSource methodSource = (MethodSource)testDescriptor.getSource().get();
            return FilterResult.includedIf(shouldBeIncluded(methodSource));
        } else {
            return FilterResult.included("not a method");
        }
    }

}
