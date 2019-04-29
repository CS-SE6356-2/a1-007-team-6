# bscardgame-team6
A Java based implementation of the B.S. card game

## Environment Setup
You will need the following to compile and run this game

* NetBeans 8.2
* Java SE 1.8 (Runtime + JDK)

After cloning the repository, open the project folder under NetBeans > Open Project. This will import the project into NetBeans where you can Clean, Build, and finally Run. 

## Server setup
Runtime is the same as the Environment Setup above. Machine running the server project will need to open port ranges for communication over the internet with the clients. If you are using Windows you will need to create an Inbound and Outbound firewall exception for TCP and UDP port ranges 54500 to 54599. For Linux/macOS users you will need to configure an exception via iptables. After your firewall exceptions are made you will need to configure port forwarding with your router for TCP and UDP ports in range 54500 to 54599. 

## Download and run client binary (JAR)
We have a precompiled binary packaged in a JAR which uses our hosted game server available under [Releases](https://github.com/CS-SE6356-2/a1-007-team-6/releases)
