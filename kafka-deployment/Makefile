.PHONY = deploy teardown control-center create-topics

TOPIC_NAME=ts-events

deploy:
	docker-compose up -d --remove-orphans

teardown:
	docker-compose stop

control-center:
	open http://localhost:9021/clusters

deploy-topics:
	docker-compose exec broker kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic ${TOPIC_NAME} || true