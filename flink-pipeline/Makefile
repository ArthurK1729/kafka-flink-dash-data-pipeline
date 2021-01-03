.PHONY = lint build-jar \
		clean deploy-cluster teardown-cluster \
		open-flink-ui submit-timeseries-analysis-job \
		spotbugs test build-latest-flink


FLINK_VERSION=1.12
SCALA_VERSION=2.11

FLINK_IMAGE_TAG=${FLINK_VERSION}-scala_${SCALA_VERSION}-java11

DOCKER_COMPOSE_COMMAND=FLINK_IMAGE_TAG=${FLINK_IMAGE_TAG} docker-compose

FLINK_BUILD_TMP_DIR=/tmp/flink-docker
FLINK_COMMIT_SHA=fb04fbd8471473ce33513f118df64f0daf759022

# 1.12.0 Docker image not available as of yet
# so I am building the image locally myself as a workaround
build-latest-flink:
	rm -rf ${FLINK_BUILD_TMP_DIR}
	git clone https://github.com/apache/flink-docker.git ${FLINK_BUILD_TMP_DIR}
	cd ${FLINK_BUILD_TMP_DIR} \
		&& git checkout ${FLINK_COMMIT_SHA} \
		&& cd ${FLINK_VERSION}/scala_${SCALA_VERSION}-java11-debian \
		&& docker build . -t flink:${FLINK_IMAGE_TAG}
	rm -rf ${FLINK_BUILD_TMP_DIR}


deploy-cluster:
	@${DOCKER_COMPOSE_COMMAND} up -d --remove-orphans || echo "Did you run make build-latest-flink?"

teardown-cluster:
	@${DOCKER_COMPOSE_COMMAND} kill

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

spotbugs:
	./gradlew check

test:
	./gradlew test
