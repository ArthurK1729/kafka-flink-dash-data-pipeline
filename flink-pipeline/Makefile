.PHONY = lint build-jar clean deploy-cluster teardown-cluster

FLINK_VERSION=1.11.2
SCALA_VERSION=2.11

deploy-cluster:
	docker-compose up -d --remove-orphans

teardown-cluster:
	docker-compose kill

submit-timeseries-analysis-job: build-jar
	./submit-job.sh com.pipeline.jobs.TimeseriesAnalysisJob

open-flink-ui:
	open http://localhost:8081

clean:
	@rm -rf build
	@rm -rf generated/*

lint:
	./gradlew spotlessJavaApply

build-jar: clean
	./gradlew shadowJar