import subprocess, os


def run(k, num_of_points):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_POINTS"] = f"{num_of_points}"

    subprocess.run(["mvn", "exec:java", "-D", "exec.mainClass=edu.hanyang.enclosing1998.SpeedTestApp"])


if __name__ == "__main__":
    for k in range(5, 31, 5):
        for num_of_points in range(500, 10001, 500):
            run(k, num_of_points)

