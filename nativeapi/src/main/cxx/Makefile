# Temporary Makefile, until I get around to maven native plugin
# javah -cp ../../../../core/target/classes   org.ab1rw.candora.core.adapter.NativeSocketCANAdapter


JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt
CFLAGS=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -g -Wall

a.out:  candora-native.o  org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o
	gcc -static candora-native.o -o a.out

candora-native.o: candora-native.c
	$(CC) $(CFLAGS) -c candora-native.c

org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o :  org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.c

clean:
	rm *.o a.out
