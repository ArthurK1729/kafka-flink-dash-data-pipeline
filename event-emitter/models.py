from pydantic import BaseModel


class TimeseriesReading(BaseModel):
    id: int
    reading: float
    timestamp: int
