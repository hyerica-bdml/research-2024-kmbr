import subprocess, os


def run(k, num_of_iters, use_cache):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_ITERATIONS"] = f"{num_of_iters}"
    os.environ["USE_CACHE"] = use_cache

    subprocess.run(["mvn", "clean", "compile", "exec:java", "-D", "exec.mainClass=edu.hanyang.kmbr.CityEscooterSimulationApp"])


if __name__ == "__main__":
    for use_cache in ["false", "true"]:
        run(10, 1, use_cache)
