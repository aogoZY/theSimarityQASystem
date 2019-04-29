import os
root = "/Users/zhouyang/Study/theSimarityQASystem"

for dirpath, dirnames, filenames in os.walk(root):
    for filepath in filenames:
	this_path = os.path.join(dirpath, filepath)
	this_size = os.path.getsize(this_path)
	if(this_size / 1000 > 100000):
            print this_path
