.PHONY = init start-producer clean lint

init:
	pip install --upgrade pip
	pip install -r requirements.txt
	pip install -r requirements-dev.txt

lint:
	black -l 120 .
	flake8 --config=.flake8 --exit-zero .

start-producer:
	@python main.py

clean:
	rm -rf build
	rm -rf __pycache__