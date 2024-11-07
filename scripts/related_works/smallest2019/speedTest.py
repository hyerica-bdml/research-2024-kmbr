import subprocess, os


def run(k, num_of_points, num_of_iters):
    os.environ["K"] = f"{k}"
    os.environ["NUM_OF_POINTS"] = f"{num_of_points}"
    os.environ["NUM_OF_ITERATIONS"] = f"{num_of_iters}"

    subprocess.run(["mvn", "exec:java", "-D", "exec.mainClass=edu.hanyang.smallest2019.SpeedTestApp"])


if __name__ == "__main__":
#     k = 5
#     num_of_iters = [3, 1, 10, 10, 1]
#     num_of_points = [4000, 4500, 7000, 7500, 8500]
#     for np, ni in zip(num_of_points, num_of_iters):
#         run(k, np, ni)
#
#     k = 10
#     num_of_iters = [2, 6, 2, 4, 10, 10, 10, 10, 10, 10, 10, 10, 10]
#     num_of_points = [4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000, 8500, 9000, 9500, 10000]
#     for np, ni in zip(num_of_points, num_of_iters):
#         run(k, np, ni)
#
    for k in range(15, 31, 5):
        for num_of_points in range(500, 10001, 500):
            run(k, num_of_points, 10)
