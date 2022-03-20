#!/bin/sh



rmiregistry &
javac Server.java && javac Start.java && javac Client.java
(java Server &)&& sleep 5
(java Start &)&& sleep 5
java Client 
sleep 10


rm $(find . -name "*.class")
