import asyncio
from typing import Dict, Any

import click
from click import BadParameter

from commands import start_generate
from generators import GENERATORS

Options = Dict[str, Any]


def _parse_options(ctx, param, value) -> Options:
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
@click.option("-t", "--topic", "topic")
@click.option("-b", "--brokers", "brokers")
@click.option("-o", "--options", "options", callback=_parse_options)
@click.option("-d", "--delay_seconds", "delay_seconds", type=int, required=False, default=1)
@click.option("-b", "--batch_size", "batch_size", type=int, required=False, default=100)
def generate(generator_name: str, topic: str, brokers: str, options: Options, delay_seconds: int, batch_size: int):
    click.echo(f"Parsed options: {options}")
    click.echo(f"Generating {generator_name}...")

    if generator_name not in GENERATORS:
        raise BadParameter(f"{generator_name} is not a valid generator name.")

    generator = GENERATORS[generator_name](**options)

    asyncio.run(
        start_generate(
            generator=generator, topic=topic, brokers=brokers, delay_seconds=delay_seconds, batch_size=batch_size
        )
    )
