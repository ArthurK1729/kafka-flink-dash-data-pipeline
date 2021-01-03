from typing import Iterable

import numpy as np

from constants import SEED_OF_RANDOMNESS

np.random.seed(SEED_OF_RANDOMNESS)


def gaussian_noise_generator(mean: float = 0.0, std: float = 1.0) -> Iterable[float]:
    """
    Generator that returns samples of a Gaussian distribution

    :param mean: mean of Gaussian distribution
    :param std: standard deviation of Gaussian distribution
    """

    def _generate() -> float:
        return np.random.normal(float(mean), float(std), 1)[0]

    while True:
        yield _generate()


def gaussian_process_generator(mean: float = 0.0, std: float = 1.0, start: float = 0.0) -> Iterable[float]:
    """
    1-D Gaussian process generator

    :param start: initial position of Gaussian process
    :param mean: mean of Gaussian process
    :param std:  standard deviation of Gaussian process
    """

    current = float(start)

    def _generate() -> float:
        return current + np.random.normal(float(mean), float(std), 1)[0]

    while True:
        yield current
        current = _generate()


GENERATORS = {"gaussian_noise": gaussian_noise_generator, "gaussian_process": gaussian_process_generator}
