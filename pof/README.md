Provides several tools and extensions that make Portable Object Format (POF) much easier to work with,
including:

*   `PofXjcPlugin`, which implements `PortableObject` interface within classes generated from the
    XML schema by the XJC compiler.

*   `PortableTypeGenerator`, which uses bytecode instrumentation to implement POF serialization code based
    on class and field annotations and provides full support for schema evolution within class hierarchy.
