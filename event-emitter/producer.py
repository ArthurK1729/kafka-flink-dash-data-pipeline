from aiokafka import AIOKafkaProducer
from pydantic import BaseModel

import utils


class AsyncKafkaProducer:
    def __init__(self, bootstrap_servers: str, request_timeout_ms: int):
        self._producer = AIOKafkaProducer(bootstrap_servers=bootstrap_servers, request_timeout_ms=request_timeout_ms)

    async def start(self):
        await self._producer.start()

    async def stop(self):
        await self._producer.stop()

    async def post_event(self, event: BaseModel, topic: str):
        await self._producer.send_and_wait(topic, utils.string_to_binary(event.json()))
