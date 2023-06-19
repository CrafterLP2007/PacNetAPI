# PacNetAPI - Documentation
This api communication between a server and one or multiple clients over a network connection. The server can send messages to specific clients, and clients can send messages to the server.

#

## Installation
To use the PacNetAPI in your Java project, you can include the library as a dependency. You have two options to include the dependency:

### Maven:
```xml
<dependency>
    <groupId>de.crafterlp</groupId>
    <artifactId>pacnetapi</artifactId>
    <version>{VERSION}</version>
</dependency>
```

- Replace the {VERSION} to the [newest version](https://github.com/CrafterLP2007/PacNetAPI/blob/master/pom.xml) of this project!

### Gradle
```groovy
dependencies {
    implementation 'de.crafterlp:gitwrap4j:{VERSION}'
}
```

- Replace the {VERSION} to the [newest version](https://github.com/CrafterLP2007/PacNetAPI/blob/master/pom.xml) of this project!

#

## Getting started

### Use the  Server:

```java
package de.mypackage;

public class TestServer extends Server {

    public Test(int port, boolean secureMode, boolean debug) {
        super(port, secureMode, debug);
    }

    @Override
    public void preStart() {
        sendMessage("MyClient", new DataPackage("id", "object")); // Send a specific client a datapackage message
        start(); // Don't forget to start the Server!
    }

    @Override
    public void handlePacket(DataPackage dataPackage) {
        System.out.println(dataPackage.id() + " " + dataPackage.get[1]); // Displays the id and the value of the sent datapackage from a client
    }
}

```

- **port**: The port where the Server listen to
- **secureMode**: Running SSL-Certificate on the Server
- **debug**: Prints all messages from the server

### Use the Client
```java
package de.mypackage;

public class Test extends Client {


    public Test(String host, int port, String identifier, boolean debug) {
        super(host, port, identifier, debug);
    }

    @Override
    public void preStart() {
        sendMessage("MyClient", new DataPackage("id", "object")); // Send a datapackage to the server
        start(); // Don't forget to start the Client!
    }

    @Override
    public void handlePacket(DataPackage dataPackage) {
        System.out.println(dataPackage.id() + " " + dataPackage.get[1]); // Displays the id and the value of the sent datapackage from the server
    }
}
```

- **host**: The host of the Server
- **port**: The port of the Server
- **identifier**: The identifier of the Client
- **debug**: Prints all messages from the client

## More useful methods:

Broadcast message to all registered clients:
```java
sendBroadCast(DataPackage dataPackage); // Send all registered clients a message
```

Enable/Disable the messages:
```java
setDebug(false); // Sets the debug mode to false
```

## EventHandlers
There a some event handlers (e.g. onConnected, onClientRegistered(), etc.) you can overwrite to handle these events.

# Contributions
Contributions to the PacNetAPI are welcome! If you encounter any issues or have suggestions for improvements, please open an issue on the [PacNetAPI GitHub repository](https://github.com/CrafterLP2007/PacNetAPI/issues).

# License
This project is licensed under the [MIT License](https://github.com/CrafterLP2007/PacNetAPI/blob/master/LICENCE).