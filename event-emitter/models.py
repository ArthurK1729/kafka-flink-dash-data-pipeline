from pydantic import BaseModel


class TimeseriesReading(BaseModel):
    reading: float
    timestamp: int
