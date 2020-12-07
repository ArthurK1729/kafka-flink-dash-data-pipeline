import asyncio
import time
from itertools import islice

from constants import DELAY_SECONDS, MESSAGES_IN_BATCH
from generators import gaussian_process_generator
from logger import logger
from models import TimeseriesReading
from producer import AsyncKafkaProducer
import utils


# TODO: create argument options and process selector
# TODO: actually create a cli to run these generators
#   event-emitter generator gaussian_process --mean 2.5 --std 5.3 --start 1.1 --topic ts-events
#   event-emitter generator gaussian_noise --mean 2.5 --std 0.0 --topic ts-events
#   event-emitter generator brown_noise --mean 2.5 --std 0.1 --topic ts-events


async def main():
    logger.info("Initialising generator")
    generator = gaussian_process_generator(mean=5.0, std=2.0, start=0.0)
    logger.info("Generator initialised")

    logger.info("Starting producer")
    producer = AsyncKafkaProducer()
    await producer.start()
    logger.info("Producer started")

    try:
        while True:
            time.sleep(DELAY_SECONDS)

            await asyncio.gather(
                *[
                    producer.post_event(
                        event=TimeseriesReading(number=num, timestamp=utils.generate_current_timestamp()),
                        topic="ts-events",
                    )
                    for num in islice(generator, MESSAGES_IN_BATCH)
                ]
            )

            logger.info(f"Message batch of {MESSAGES_IN_BATCH} sent in {DELAY_SECONDS} second(s)")
    finally:
        await producer.stop()
        logger.info("Producer stopped")


if __name__ == "__main__":
    asyncio.run(main())
