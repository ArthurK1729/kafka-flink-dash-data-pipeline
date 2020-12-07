import datetime


def string_to_binary(string: str) -> bytes:
    return string.encode("utf-8")


# TODO: consider generating epoch time int
def generate_current_timestamp() -> datetime.datetime:
    return datetime.datetime.utcnow()
