from setuptools import setup, find_packages

setup(
    name="event-emitter",
    version="1.0",
    packages=find_packages(),
    include_package_data=True,
    install_requires=[i.strip() for i in open("requirements.txt").readlines()],
    entry_points="""
        [console_scripts]
        event-emitter=cli:cli
    """,
)
