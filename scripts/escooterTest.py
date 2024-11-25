import subprocess, os


def run(k, num_of_iters, use_cache):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_ITERATIONS"] = f"{1}"
    os.environ["USE_CACHE"] = use_cache

    for i in range(num_of_iters):
        subprocess.run(["mvn", "clean", "compile", "exec:java", "-D", "exec.mainClass=edu.hanyang.kmbr.CityEscooterSimulationApp"])


if __name__ == "__main__":
    for use_cache in ["false", "true"]:
        run(10, 10, use_cache)
