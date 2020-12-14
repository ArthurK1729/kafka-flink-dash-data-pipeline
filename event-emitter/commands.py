import asyncio
import time
from dataclasses import dataclass
from itertools import islice
from pathlib import Path
from typing import Generator

from fastavro.schema import load_schema

import utils
from logger import logger
from models import TimeseriesReading
from producer import AsyncKafkaProducer


@dataclass
class GeneratorConfig:
    timeseries_id: int
    topic: str
    brokers: str
    delay_seconds: int
    batch_size: int
    generator: Generator


# TODO: handle kafka keys
#   add a key partitioner?
async def start_generate(config: GeneratorConfig):
    logger.info("Starting producer")
    producer = AsyncKafkaProducer(bootstrap_servers=config.brokers, request_timeout_ms=5000)
    await producer.start()
    logger.info("Producer started")

    logger.info("Loading schema")
    message_schema = load_schema(Path(__file__).parent.parent / "schema" / "timeseries_reading.avsc")
    logger.info(f"Schema loaded: {message_schema}")

    try:
        while True:
            time.sleep(config.delay_seconds)

            await asyncio.gather(
                *[
                    producer.post_event(
                        event=utils.model_to_bytes(
                            TimeseriesReading(
                                id=config.timeseries_id, reading=num, timestamp=utils.generate_current_epoch_time_ms()
                            ),
                            avro_schema=message_schema,
                        ),
                        topic=config.topic,
                    )
                    for num in islice(config.generator, config.batch_size)
                ]
            )

            logger.info(f"Message batch of {config.batch_size} sent in {config.delay_seconds} second(s)")
    finally:
        await producer.stop()
        logger.info("Producer stopped")
