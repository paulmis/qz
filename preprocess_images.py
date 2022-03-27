import sys
import glob
import numpy as np
from PIL import Image
import subprocess

path = sys.argv[1]


for filename in glob.iglob(path + '**/*.jpeg', recursive=True):
    print(filename)
    src = np.array(Image.open(filename))
    print(src.shape)
    if len(src.shape) == 2:
        src_h, src_w = src.shape
    else:
        src_h, src_w, _ = src.shape
    dx = min(src_h, src_w) - src_w
    dy = min(src_h, src_w) - src_h
    print((dx,dy))
    #subprocess.Popen("python seam_carving.py -resize -dx {} -dy {} -im {} -out {}".format(dx,dy,filename,filename), shell = True, stdout=subprocess.PIPE)