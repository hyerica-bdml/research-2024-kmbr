import subprocess, os


def run(k, num_of_iters):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_ITERATIONS"] = f"{num_of_iters}"

    subprocess.run(["mvn", "clean", "compile", "exec:java", "-D", "exec.mainClass=edu.hanyang.smallest2019.CityEscooterSimulationApp"])


if __name__ == "__main__":
    run(10, 1)
