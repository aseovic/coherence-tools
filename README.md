Coherence Tools
===============

Coherence Tools project is a collection of tools and extensions for [Oracle Coherence]
(http://www.oracle.com/technetwork/middleware/coherence/overview/index.html) in-memory data grid.

Modules
-------
The project consists of several modules:

### Core

Contains core interfaces and classes that most of the other projects depend on, such as:

*   `Expression` abstraction, which defines a common API and provides implementations for various
    expression languages ([MVEL](http://mvel.codehaus.org/),
    [SpEL](http://static.springsource.org/spring/docs/3.0.x/reference/expressions.html),
    [OGNL](http://commons.apache.org/ognl/), [Groovy](http://groovy.codehaus.org/), and
    [Java Scripting](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html))

*   `Extractor` interface and many useful implementations, including `ExpressionExtractor`, which can be
    used anywhere Coherence `ValueExtractor` is expected.

*   `Factory` interface and a number of implementations, which can be very useful in certain situations.
    For example, when distributing work across the cluster, your tasks might depend on some non-serializable
    objects (database connection, for example). You can use serializable `DriverManagerDataSourceFactory`
    in this case, which will allow you to create properly configure `DataSource` instances within the cluster.

*   `ClusteredExecutorService`, which allows you to distribute `Callable` or `Runnable` tasks across the
    cluster and execute them using Invocation Service.

*   Useful collection implementations, including Remote Collections, which use entry processors to manipulate
    collections within partitioned cache without moving the whole collection across the wire.

*   Number of base classes and useful implementations of Coherence entry processors, filters, aggregators,
    cache stores, etc.

### POF

Provides several tools and extensions that make Portable Object Format (POF) much easier to work with,
including:

*   `PofXjcPlugin`, which implements `PortableObject` interface within classes generated from the
    XML schema by the XJC compiler.

*   `PortableTypeGenerator`, which uses bytecode instrumentation to implement POF serialization code based
    on class and field annotations and provides full support for schema evolution within class hierarchy.

### Identity

Provides support for sequential identifier generation (autoincrement) within Coherence cluster.

### Loader

Provides framework for bulk data loading and a number of useful `Source` and `Target` implementations,
including Coherence caches, databases, CSV and XML files, Cobol copybooks, etc.

### Scheduler

Provides support for [Quartz](http://quartz-scheduler.org/)-based job scheduling within Coherence cluster.

### Batch

Provides support for complex batch job execution within the cluster.

### Integration

Contains a number of smaller sub-modules that provide integration between Coherence and other systems:

* **AWS** -- provides cache store implementations for [Amazon S3](http://aws.amazon.com/s3/), [SimpleDB]
  (http://aws.amazon.com/simpledb/) and [DynamoDB](http://aws.amazon.com/dynamodb/)

* **Riak** -- provides cache store implementation for [Riak](http://basho.com/)

Building from Source
--------------------

Coherence Tools uses [Apache Maven](http://maven.apache.org/) as a build system, so for the most part building
the project is as simple as:

```
$ mvn clean install
```

However, the fact that the project depends on a commercial product which is not available in Maven Central means
that your first build will likely fail because of the missing dependency on `coherence.jar`.

In order to solve that problem you will need to install `coherence.jar` manually into your local repository
or your organization's repository manager, such as [Nexus](http://www.sonatype.org/nexus/), using appropriate
groupId and artifactId.

To install `coherence.jar` into a local repository change to `$COHERENCE_HOME/lib` directory and run the
following command:

```
$ mvn install:install-file  \
      -DgroupId=com.oracle.coherence  \
      -DartifactId=coherence  \
      -Dversion=3.7.1  \
      -Dfile=coherence.jar  \
      -Dpackaging=jar \
      -DgeneratePom=true
```

Of course, this assumes that you are installing Coherence 3.7.1 JAR file. If you want to install one of the more
recent releases, change the version number in the command above accordingly (POM file is already configured to use
the most recent version >= 3.7.1 that exists in your repository, so you don't need to change anything there).

Once the `coherence.jar` is properly installed into your Maven repository, you should be able to build all the
modules except for integrations.

Building Integrations
---------------------

By their very nature integrations depend on third party products. For example, in order to build and test Riak
integration you will need to have Riak server running.

Because of this, we have excluded integration projects from the main build. You will have to build them
separately, once you have satisfied their external dependencies (check individual projects for build prerequisites).

The easiest way to do that is to run `mvn clean install` within each project's directory, but you can also
build them all by running `mvn clean install` within parent **integration** project.

Supported Coherence Versions
----------------------------

While most of the code will work just fine with older Coherence releases without any modification,
in order to build the project yourself you will need Coherence 3.7.1 or a more recent release.

The main reason for that is that we decided to use dependencies that are already embedded into `coherence.jar`,
such as ASM and MVEL, instead of introducing additional direct dependencies. In order to compile Coherence Tools
against Coherence 3.6, you will have to modify `coherence.version` property in the POM, add direct dependencies
on ASM and MVEL, and modify package names within classes that fail to compile to point to direct dependencies
instead of the ones that are embedded into Coherence 3.7.1 or higher.

Coherence versions older than 3.6 are not officially supported.


