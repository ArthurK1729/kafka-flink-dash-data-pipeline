.PHONY = deploy teardown control-center create-topics purge-topics deploy-shared-network

TOPIC_NAME=ts-events
SHARED_NETWORK_NAME=kafka-flink-dash-data-pipeline-network

deploy: deploy-shared-network
	docker-compose up -d --remove-orphans

teardown:
	docker-compose stop

control-center:
	open http://localhost:9021/clusters

purge-topics:
	docker-compose exec broker kafka-topics --delete --bootstrap-server localhost:9092 --topic ${TOPIC_NAME} --if-exists

deploy-topics:
	docker-compose exec broker kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic ${TOPIC_NAME} --if-not-exists

# We need to create a shared network between Kafka's and Flink's docker-compose setups
deploy-shared-network:
	docker network create ${SHARED_NETWORK_NAME} || true