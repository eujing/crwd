javac TileUtils.java
javac Datum.java
javac Sensor.java
javac Location.java
javac -cp "./commons-csv-1.2.jar;%CLASSPATH%" Loader.java
javac -cp "./commons-csv-1.2.jar;./org.json.jar;%CLASSPATH%" DataServer.java
java -cp "./commons-csv-1.2.jar;./org.json.jar;%CLASSPATH%" DataServer
pause