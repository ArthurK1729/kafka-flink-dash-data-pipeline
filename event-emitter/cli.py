import asyncio

import click
from click import BadParameter

from commands import start_generate
from generators import GENERATORS


@click.group()
def cli():
    pass


@cli.command()
@click.argument("generator_name")
def generate(generator_name: str):
    click.echo("Generating " + generator_name + "...")

    if generator_name not in GENERATORS:
        raise BadParameter(f" {generator_name} is not a valid generator name.")

    generator = GENERATORS[generator_name]

    asyncio.run(start_generate(generator))
