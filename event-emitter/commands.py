import asyncio
import time
from itertools import islice
from pathlib import Path
from typing import Generator

from fastavro.schema import load_schema

import utils
from logger import logger
from models import TimeseriesReading
from producer import AsyncKafkaProducer


# TODO: handle kafka keys
#   add a key partitioner?
async def start_generate(generator: Generator, topic: str, brokers: str, delay_seconds: int, batch_size: int):
    logger.info("Starting producer")
    producer = AsyncKafkaProducer(bootstrap_servers=brokers, request_timeout_ms=5000)
    await producer.start()
    logger.info("Producer started")

    logger.info("Loading schema")
    message_schema = load_schema(Path(__file__).parent.parent / "schema" / "timeseries_reading.avsc")
    logger.info(f"Schema loaded: {message_schema}")

    try:
        while True:
            time.sleep(delay_seconds)

            await asyncio.gather(
                *[
                    producer.post_event(
                        event=utils.model_to_bytes(
                            TimeseriesReading(reading=num, timestamp=utils.generate_current_epoch_time_ms()),
                            avro_schema=message_schema
                        ),
                        topic=topic
                    )
                    for num in islice(generator, batch_size)
                ]
            )

            logger.info(f"Message batch of {batch_size} sent in {delay_seconds} second(s)")
    finally:
        await producer.stop()
        logger.info("Producer stopped")
