# Usage examples  
```
event-emitter generate gaussian_process --topic ts-events --brokers localhost:9092 -o std=8.0,start=-1.8 -d 1 -b 100    
event-emitter generate gaussian_noise --topic ts-events --brokers localhost:9092 -o std=8.0,mean=25.98 -d 1 -b 1000
```