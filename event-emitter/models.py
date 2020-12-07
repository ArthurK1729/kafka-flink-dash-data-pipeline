from pydantic import BaseModel
import datetime


# TODO: Consider defining an AVRO schema
#   after all... why the hell would I share language-dependent models if I can just share a common AVRO schema?
#   how do I dump pydantic model to avro? Do I even need pydantic?
class TimeseriesReading(BaseModel):
    number: float
    timestamp: datetime.datetime
