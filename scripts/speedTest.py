import subprocess, os


def run(k, num_of_points, num_of_iters):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_POINTS"] = f"{num_of_points}"
    os.environ["NUM_OF_ITERATIONS"] = f"{num_of_iters}"

    subprocess.run(["mvn", "clean", "compile", "exec:java", "-D", "exec.mainClass=edu.hanyang.kmbr.SpeedTestApp"])


if __name__ == "__main__":
    for k in range(5, 31, 5):
        for num_of_points in range(500, 10001, 500):
            run(k, num_of_points, 10)
