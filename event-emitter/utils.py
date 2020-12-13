from datetime import datetime, timezone
from io import BytesIO

from fastavro import schemaless_writer
from pydantic.main import BaseModel


def string_to_binary(string: str) -> bytes:
    return string.encode("utf-8")


def generate_current_epoch_time_ms() -> int:
    return int(datetime.now(tz=timezone.utc).timestamp() * 1000)


def model_to_bytes(event: BaseModel, avro_schema: dict) -> bytes:
    stream = BytesIO()
    schemaless_writer(stream, avro_schema, event.dict())

    return stream.getvalue()
