import random

for i in range(4):
	f = open("test"+str(i+5)+".txt", "w")
	tam = random.randint(0,20)
	for j in range(tam):
		f.write(chr(random.randint(1,255)))
	f.close()

