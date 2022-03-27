import sys
import glob
import numpy as np
from PIL import Image
import subprocess
import seam_carving
path = sys.argv[1]


for filename in glob.iglob(path + '**/*.jpeg', recursive=True):
    print(filename)
    rgb_im = Image.open(filename).convert('RGB')
    rgb_im.thumbnail((500,500,3), Image.ANTIALIAS)
    rgb_im = np.asarray(rgb_im, dtype=np.float32) / 255
    src = rgb_im[:, :, :3]
    print(src.shape)
    if len(src.shape) == 2:
        src_h, src_w = src.shape
    else:
        src_h, src_w, _ = src.shape
    dx = min(src_h, src_w) - src_w
    dy = min(src_h, src_w) - src_h
    dst = seam_carving.resize(
        src, (min(src_h, src_w), min(src_h, src_w)),
        energy_mode='backward',   # Choose from {backward, forward}
        order='width-first',  # Choose from {width-first, height-first}
        keep_mask=None
    )
    Image.fromarray(dst).save(filename)