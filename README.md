# Quarkus Extension for Continuous Testing

Provides a websocket endpoint that can be used to view and control tests in Quarkus Continuous Testing mode.

When Quarkus is started up in dev mode, a websocket listener is set up at `/q/dev/haus.lowe.testsocket`

To start running the tests, connect to the websocket endpoint, and send a frame with one of the 3 commands:

```
class:com.example.MyClass
method:com.example.MyClass#myMethod
package:com.example
```

The tests from that package, class or method will be run, 
and the results of the test run will be sent as frames back to the websocket.

I have sketched out an [IntelliJ plugin](https://github.com/danelowe/quarkus-testsocket-intellij-plugin) 
that uses this websocket to integrate Quarkus Continuous testing with the IntelliJ test runner UI, 
so you can run tests from context menus and gutter icons,
and view the results in the test UI without having to wait for the whole project to build for each test run.

This project contains 3 sub-projects:
- **deployment**: This is part of the standard Quarkus extension setup, and contains build time processing 
  and bytecode recording per the quarkus way.
- **runtime**: Also a standard part of a Quarkus extension to contain code used at runtime. 
  The websocket handler is in this project.  
- **spi**: This project is configured as a `parentFirstArtifact`, which Quarkus loads with the main classloader. 
  It is necessary for this particular project because the Quarkus testrunner instance is available only to the 
  deployment artifact, while the websocket can only run in runtime. The two need to talk to one another,
  and do so via static methods on the `TestSocketWiring` class, which only works if the `deployment` and `runtime`
  code have loaded this class via the same (parent) classloader.  