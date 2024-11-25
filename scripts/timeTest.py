import subprocess, os


def run(k, num_of_iters):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_ITERATIONS"] = "1"

    for i in range(num_of_iters):
        subprocess.run(["mvn", "clean", "compile", "exec:java", "-D", "exec.mainClass=edu.hanyang.kmbr.TimeTestApp"])


if __name__ == "__main__":
    for k in range(10, 31, 10):
        run(k, 10)
