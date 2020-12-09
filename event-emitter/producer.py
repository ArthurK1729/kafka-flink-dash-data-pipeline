import uuid

from aiokafka import AIOKafkaProducer


class AsyncKafkaProducer:
    def __init__(self, bootstrap_servers: str, request_timeout_ms: int):
        self._producer = AIOKafkaProducer(
            bootstrap_servers=bootstrap_servers,
            request_timeout_ms=request_timeout_ms,
            client_id=str(uuid.uuid4()),
            compression_type="gzip"
        )

    async def start(self):
        await self._producer.start()

    async def stop(self):
        await self._producer.stop()

    async def post_event(self, event: bytes, topic: str):
        await self._producer.send_and_wait(topic, event)
