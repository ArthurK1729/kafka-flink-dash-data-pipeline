import asyncio
from typing import Dict, Any

import click
from click import BadParameter

from commands import start_generate, GeneratorConfig
from generators import GENERATORS

Options = Dict[str, Any]


def _parse_options(ctx, param, value) -> Options:
    """
        Makes sure the passed in overrides are of this form
            -o std=8.0,mean=28.98
    """
    parsed_options = dict()

    for item in value.split(","):
        key, value = item.split("=")
        parsed_options[key] = value

    return parsed_options


@click.group()
def cli():
    pass


@cli.command()
@click.argument("generator_name")
@click.option("-i", "--id", "timeseries_id", type=int, required=False, default=0)
@click.option("-t", "--topic", "topic")
@click.option("-b", "--brokers", "brokers")
@click.option("-o", "--options", "options", callback=_parse_options)
@click.option("-d", "--delay_seconds", "delay_seconds", type=int, required=False, default=1)
@click.option("-b", "--batch_size", "batch_size", type=int, required=False, default=100)
def generate(
    timeseries_id: int,
    generator_name: str,
    topic: str,
    brokers: str,
    options: Options,
    delay_seconds: int,
    batch_size: int,
):
    """
    Main entrypoint of timeseries generation

    :param timeseries_id: Identifier for the timeseries. Used to distinguish streams that feed into the same topic
    :param generator_name: Selector for the generator
    :param topic: Kafka topic to write to
    :param brokers: Kafka broker list
    :param options: Optional overrides for generators (mean, standard deviation, etc)
    :param delay_seconds: How many seconds to wait before sending a batch of readings
    :param batch_size: How many readings to send in one batch
    """
    click.echo(f"Parsed options: {options}")
    click.echo(f"Generating {generator_name}...")

    if generator_name not in GENERATORS:
        raise BadParameter(f"{generator_name} is not a valid generator name.")

    generator = GENERATORS[generator_name](**options)

    config = GeneratorConfig(
        timeseries_id=timeseries_id,
        topic=topic,
        brokers=brokers,
        delay_seconds=delay_seconds,
        batch_size=batch_size,
        generator=generator,
    )

    asyncio.run(start_generate(config))
