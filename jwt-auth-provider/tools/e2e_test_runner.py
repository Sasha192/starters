import argparse
import logging


class TestRunner:

    def __init__(self):
        arg_parser = argparse.ArgumentParser()
        arg_parser.add_argument("--name", required=False, default="localhost:9092",
                                help="Bootstrap server address")
        arg_parser.add_argument("--debug", required=False, default="False")
        self.args = arg_parser.parse_args()
        logging.basicConfig(level=logging.INFO)
        if self.args.debug is not None and self.args.debug == "True":
            logging.basicConfig(level=logging.DEBUG)

    @property
    def args(self):
        return self._args

    @args.setter
    def args(self, value):
        self._args = value

    def run(self):
        pass


if __name__ == "__main__":
    test_runner = TestRunner()
    test_runner.run()
