# AIS Simulator

A simple AIS server simulator.

# How to build & run

Build with `mvn clean install`, this creates a fat jar with embedded Payara Micro Enterprise server.

Run with `java -jar target/ais-simulator-microbundle.jar`.

# How to access admin page of simulator

After that open http://localhost:8080/ in your web browser. This will display the "*AIS-simulator admin page*" (see screenshot below) where you can do the following:

* select which .csv datafile AIS-simulator should use when it is running
* upload new .csv datafiles to the simulator
* start, stop and pause the AIS-simulator
* select which nth value the simulator should use when it is running

![image.png](./assets/image.png)

# Before you press "Start"

Please note: You can only start up the AIS-simulator from the admin page after you have first done the following:

1. uploaded at least one .csv datafile to the AIS-simulator server
2. selected which datafile the AIS-simulator should read from when it is running.

Both of these things you can do from the AIS-simulator admin page at http://localhost:8080/.

# Connecting to simulator from client

Once the AIS-simulator is up and running clients can connect to it using port 8040 e.g. http://localhost:8040/.

# REST interface

The admin page for the AIS-simulator server uses a REST interface to communicate with/configure the AIS-simulator server itself. The OpenAPI specification for that REST interface is available at http://localhost:8080/openapi.

There is also Swagger UI available for the REST interface at http://localhost:8080/rest/openapi-ui/.

# Where to get .csv datafiles for the simulator

CSV datafiles can be downloaded from [ftp://ftp.ais.dk/ais_data/](ftp://ftp.ais.dk/ais_data/) using e.g. the [FileZilla client](https://filezilla-project.org/).

# Uploaded files are deleted when OS is rebooted?

Uploaded files are currently stored in `/tmp/uvms`. In Linux this folder and all files contained within it are automatically deleted once the OS is rebooted. 

On Windows this doesn't happen and any uploaded files will remain even after the OS is restarted.

# Unresolved issues #1

This application currently works only in Linux (and Mac?).

On Windows you will have to change the path of the temporary download directory from `/temp/uvms` to a path that works in Windows e.g. `C:\temp\uvms`. That change will have to be made in the following three files:

* /pom.xml
* /src/main/webapp/WEB-INF/web.xml
* /src/main/java/se/havochvatten/unionvms/rest/AisServerState.java

# Unresolved issues #2

Another issue on Windows is that you may get a `java.net.SocketException: Protocol family not supported` thrown by the JVM if you try to connect (from the client) to url http://localhost:8040/. This issue may be related to running Docker Desktop.

One way to get around this issue seems to be to make the query to http://[your host computer name]:8040/ instead. So you could use e.g. http://desktop-25pafta:8040/ if your host computer was named `desktop-25pafta`.
