import subprocess, os


def run(k, num_of_points, num_of_iters):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_POINTS"] = f"{num_of_points}"
    os.environ["NUM_OF_ITERATIONS"] = f"{num_of_iters}"

    subprocess.run(["mvn", "exec:java", "-D", "exec.mainClass=edu.hanyang.enclosing1998.SpeedTestApp"])


if __name__ == "__main__":
#     for k in range(5, 31, 5):
#         for num_of_points in range(500, 3001, 500):
#             run(k, num_of_points, 10)
#     for k in range(5, 31, 5):
#         for num_of_points in range(3500, 5001, 500):
#             run(k, num_of_points, 3)
#     for k in range(5, 31, 5):
#         for num_of_points in range(5500, 10001, 500):
#             run(k, num_of_points, 1)

    for num_of_points in range(8500, 10001, 500):
        run(10, num_of_points, 1)

    for k in range(15, 31, 5):
        for num_of_points in range(5500, 10001, 500):
            run(k, num_of_points, 1)

