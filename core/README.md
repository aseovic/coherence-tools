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
