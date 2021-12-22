package haus.lowe.quarkus.testsocket.deployment.filter;

import haus.lowe.quarkus.testsocket.spi.TestSocketWiring;
import haus.lowe.quarkus.testsocket.spi.command.Command;
import haus.lowe.quarkus.testsocket.spi.command.TestClass;
import haus.lowe.quarkus.testsocket.spi.command.TestMethod;
import haus.lowe.quarkus.testsocket.spi.command.TestPackage;
import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.PostDiscoveryFilter;

/**
 * PostDiscoveryFilter that delegates to the filter picked by the SPI Wiring.
 *
 * Registered as a service for discovery by JUnit so that we can then use the [TestSocketWiring] to add a filter.
 * This is to work around the fact that Quarkus TestRunner doesn't allow setting arbitrary PostDiscoveryFilters
 */
public class WiringTestFilter implements PostDiscoveryFilter {

    @Override
    public FilterResult apply(TestDescriptor testDescriptor) {
        PostDiscoveryFilter filter = getCommandFilter();
        if (filter == null) {
            return FilterResult.included("No filter applied");
        }
        if (testDescriptor.getSource().isPresent() && testDescriptor.getSource().get() instanceof MethodSource) {
            return filter.apply(testDescriptor);
        } else {
            return FilterResult.included("not a method");
        }
    }

    private PostDiscoveryFilter getCommandFilter() {
        Command command = TestSocketWiring.getTestCommand();
        if (command == null) {
            return null;
        } else if (command instanceof TestClass){
            return new ClassNameFilter(((TestClass) command).getClassName());
        } else if (command instanceof TestMethod){
            return new MethodNameFilter(
                    ((TestMethod) command).getClassName(),
                    ((TestMethod) command).getMethodName()
            );
        } else if (command instanceof TestPackage){
            return new PackageNameFilter(((TestPackage) command).getPackageName());
        }
        throw new IllegalStateException("Cannot find a filter for "+command);
    }
}
