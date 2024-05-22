import argparse
import numpy as np
import matplotlib.pylab as plt


def random_cluster_probs(n_clusters):
    num_major_clusters = n_clusters//5
    alpha = [20 for _ in range(num_major_clusters)] + [1 for _ in range(n_clusters - num_major_clusters)]
    probs = np.random.dirichlet(alpha)
    return probs


def create_datapoints(n_points, n_clusters, cluster_center_mean, cluster_center_std, cluster_std_range):
    cluster_probs = random_cluster_probs(n_clusters)

    cluster_centers = np.random.normal(loc=cluster_center_mean, scale=cluster_center_std, size=(n_clusters, 2))
    cluster_stds = np.random.rand(n_clusters, 2) * (cluster_std_range[1] - cluster_std_range[0]) + cluster_std_range[0]

    points = []
    cluster_indices = []

    for c in range(n_clusters):
        cluster_center = cluster_centers[c]
        cluster_std = cluster_stds[c]
        num_of_points = int(np.ceil(n_points * cluster_probs[c]))
        points.append(np.random.normal(loc=cluster_center, scale=cluster_std, size=(num_of_points, 2)))
        cluster_indices.append(np.ones(num_of_points) * c)

    points = np.concatenate(points, axis=0)
    cluster_indices = np.concatenate(cluster_indices, axis=0)

    points = points[:n_points]
    cluster_indices = cluster_indices[:n_points]
    
    return points, cluster_indices
        

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--n", type=int, required=True)

    opt = parser.parse_args()

    points, cluster_indices = create_datapoints(opt.n, 10, 100, 20, [1, 3])
    plt.scatter(points[:, 0], points[:, 1], s=1)
    plt.savefig("./generated_points.png")

    with open("../data/points/points.txt", "w") as f:
        f.write(f"{opt.n}\n")
        for i in range(len(points)):
            f.write(f"{cluster_indices[i]} {points[i, 0]} {points[i, 1]}\n")
