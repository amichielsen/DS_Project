# DS_Project
DS Course project


# Port Mapping
* User web interface: 80
* REST: 8080
* MC: 5000
* TCP file transfer: 5044


# Running NS
```
export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64
cd ./DS_Project/
git pull https://amichielsen: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@github.com/amichielsen/DS_Project
cd ./NameServer
mvn clean install
cd ./target
java -jar NamingServer-0.0.1-SNAPSHOT.jar
```

# Running Node
```
export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64
cd ./DS_Project/
git pull https://amichielsen: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@github.com/amichielsen/DS_Project
cd ./Node
mvn clean install
cd ./target
java -jar Node-0.0.1-SNAPSHOT.jar
```
