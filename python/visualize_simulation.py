from copy import deepcopy
from PIL import Image
import numpy as np
import pathlib
import matplotlib.pyplot as plt
import matplotlib.patches as patches


def get_simulation_record():
    with open("../data/points/simulation.txt", "r") as f:
        lines = f.readlines()

    frames = []
    frame = {
        "points": dict(),
        "mbr": None
    }

    move_count = 0

    for line in lines:
        splited = line.split(" ")

        if splited[0] == "create":

            pid = int(splited[1])
            x = float(splited[2])
            y = float(splited[3])

            frame["points"][pid] = np.array([x, y])
        # else:
        #     splited = line.split(" ")
        #     point_ids = list(map(int, splited[1:]))
        #     frame["mbr"] = point_ids
        #     frames.append(frame)
        #     break

        elif splited[0] == "move":
            pid = int(splited[1])
            x = float(splited[2])
            y = float(splited[3])

            frame["points"][pid] = np.array([x, y])

            move_count += 1

            # if move_count == 1000:
            #     frames.append(frame)
            #     frame = {
            #         "points": deepcopy(frame["points"]),
            #         "mbr": deepcopy(frame["mbr"])
            #     }
            #     move_count = 0

        elif splited[0] == "mbr":
            point_ids = list(map(int, splited[1:]))
            frame["mbr"] = np.array(point_ids, dtype=np.int32)

            frames.append(frame)
            frame = {
                "points": deepcopy(frame["points"]),
                "mbr": deepcopy(frame["mbr"])
            }
            move_count = 0

    splited = lines[-1].split(" ")
    point_ids = list(map(int, splited[1:]))
    frame["mbr"] = point_ids

    frames.append(frame)
    return frames


def draw_frame(ax, frame):
    points = np.stack(list(map(lambda key: frame["points"][key], frame["points"])), axis=0)
    ax.scatter(points[:, 0], points[:, 1], s=0.5, c="blue")

    mbr = frame["mbr"]
    if mbr is not None:
        mbr_points = np.stack(list(map(lambda key: frame["points"][key], mbr)), axis=0)
        ax.scatter(mbr_points[:, 0], mbr_points[:, 1], s=0.5, c="red")

        mbr_xy = np.min(mbr_points, axis=0)
        w = np.max(mbr_points[:, 0]) - mbr_xy[0]
        h = np.max(mbr_points[:, 1]) - mbr_xy[1]

        ax.add_patch(patches.Rectangle(
            mbr_xy, w, h, fill=False, edgecolor="red", linewidth=1, facecolor="red"
        ))



def visualize():
    print("Reading records...")
    frames = get_simulation_record()
    print("Simulation records have been read.")

    for i, frame in enumerate(frames):
        fig, ax = plt.subplots(figsize=(12, 12), dpi=150)
        draw_frame(ax, frame)
        fig.savefig(f"simulation/{i + 1:08d}.jpg")
        plt.close(fig)


if __name__ == "__main__":
    visualize()
    image, *images = [Image.open(str(f)) for f in pathlib.Path("./simulation").glob("*.jpg")]
    image.save("kmbr.gif", format="GIF", append_images=images, save_all=True, duration=200, loop=0)
