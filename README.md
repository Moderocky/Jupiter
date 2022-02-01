Jupiter
=====

### Opus #15

A library for working with common Input/Output routes and providing better utilities for Java's I/O streams.

## Maven Information
```xml
<repository>
    <id>kenzie</id>
    <name>Kenzie's Repository</name>
    <url>https://repo.kenzie.mx/releases</url>
</repository>
``` 

```xml
<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>jupiter</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Description

Jupiter provides a set of utilities for working with streams and channels, to make integrating them easier and to reduce the amount of necessary boilerplate for common tasks.

These utilities include:
1. Synchronization locks for streams (where multithreaded access would normally be dangerous)
2. Stream controllers that can perform simple conversion tasks
3. Forking, locking and wrapping streams with automatic resource disposal

There are also more advanced utilities for handling complex socket connections.

The `SocketPair` provides a smart two-way socket channel between two known addresses with automatic host/guest deference.

The `SocketHub` provides a fluid-size socket network connection for organising channels between an unknown set of guests.
