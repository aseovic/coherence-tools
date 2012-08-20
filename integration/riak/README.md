Provides cache store implementation for [Riak](http://basho.com/)

### Details

Riak integration uses low-level Protocol Buffers-based Java API to persist binary entries in Riak.

Both keys and values are persisted as opaque binary blobs, in a format determined by the serializer
configured for a cache service. This makes both reads and writes very fast, but the downside is that
it also makes it impossible to use some of the advanced Riak features, such as search or MapReduce.

### Build Prerequisites

In order to build Riak integration you will have to start a single Riak node on the build machine,
accepting Protocol Buffers-based clients on port 8087 (default setting).
