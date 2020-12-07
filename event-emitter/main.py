import asyncio

from aiokafka import AIOKafkaProducer

from logger import logger


async def main():
    logger.info("Starting producer")
    producer = AIOKafkaProducer(
        bootstrap_servers='172.18.0.3:9092',
        request_timeout_ms=5000
    )
    # Get cluster layout and initial topic/partition leadership information
    await producer.start()
    logger.info("Producer started")

    try:
        # Produce message
        await producer.send_and_wait("my_topic", b"Super message")
        logger.info("Message sent")
    finally:
        # Wait for all pending messages to be delivered or expire.
        await producer.stop()
        logger.info("Producer stopped")


if __name__ == "__main__":
    asyncio.run(main())
