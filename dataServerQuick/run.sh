#!/bin/bash
CLASSPATH="./commons-csv-1.2.jar:./org.json.jar:./ssj-2.5.jar:./"
javac TimeUtils.java
javac Datum.java
javac Sensor.java
javac Location.java
javac -cp $CLASSPATH Loader.java
javac -cp $CLASSPATH DataServer.java
java -cp $CLASSPATH DataServer
