#!/bin/bash
CLASSPATH="../org.json.jar:../:./"

javac -cp $CLASSPATH DataSimulator.java
java -cp $CLASSPATH DataSimulator
