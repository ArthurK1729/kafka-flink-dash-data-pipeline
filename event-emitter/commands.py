import asyncio
import time
from itertools import islice
from typing import Generator

import utils
from logger import logger
from models import TimeseriesReading
from producer import AsyncKafkaProducer


async def start_generate(generator: Generator, topic: str, brokers: str, delay_seconds: int, batch_size: int):
    logger.info("Starting producer")
    producer = AsyncKafkaProducer(bootstrap_servers=brokers, request_timeout_ms=5000)
    await producer.start()
    logger.info("Producer started")

    try:
        while True:
            time.sleep(delay_seconds)

            await asyncio.gather(
                *[
                    producer.post_event(
                        event=TimeseriesReading(number=num, timestamp=utils.generate_current_timestamp()), topic=topic
                    )
                    for num in islice(generator, batch_size)
                ]
            )

            logger.info(f"Message batch of {batch_size} sent in {delay_seconds} second(s)")
    finally:
        await producer.stop()
        logger.info("Producer stopped")
