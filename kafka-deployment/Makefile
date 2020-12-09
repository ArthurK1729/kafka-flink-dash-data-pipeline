.PHONY = deploy teardown control-center create-topics purge-topics

TOPIC_NAME=ts-events

deploy:
	docker-compose up -d --remove-orphans

teardown:
	docker-compose stop

control-center:
	open http://localhost:9021/clusters

purge-topics:
	docker-compose exec broker kafka-topics --delete --bootstrap-server localhost:9092 --topic ${TOPIC_NAME} --if-exists

deploy-topics:
	docker-compose exec broker kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic ${TOPIC_NAME} --if-not-exists