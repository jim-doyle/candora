# Temporary Makefile, until I get around to maven native plugin

JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt
CFLAGS=-I$(JAVA_HOME)/include

a.out:  candora-native.o
	gcc -static candora-native.o -o a.out

candora-native.o: candora-native.c
	$(CC) $(CFLAGS) -g -c candora-native.c

clean:
	rm *.o a.out
