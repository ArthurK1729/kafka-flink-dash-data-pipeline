### Scale up local cluster  
```
docker-compose up --scale taskmanager=5 -d
```
### Jobmanager logs
```
docker-compose logs -f jobmanager
```
### Inspect shared network members
```
docker network inspect kafka-flink-dash-data-pipeline-network
```

### TODO
1. Install spotbugs plugin
2. Install Java 11 and set JAVA_HOME
3. Install lombok plugin