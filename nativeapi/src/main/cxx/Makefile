# Temporary Makefile, until I get around to maven native plugin
# javah -cp ../../../../core/target/classes   org.ab1rw.candora.core.adapter.NativeSocketCANAdapter


JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt
CFLAGS=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -g -Wall

all:    a.out candora-native.so.1.0

# temporarily for now
a.out:  candora-native.o
	gcc -static candora-native.o -o a.out

# Shared library
candora-native.so.1.0: org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o
    ld -shared -fpic org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o -o candora-native.so.1.0

candora-native.o: candora-native.c

org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o :  org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.c org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.h logging.h

clean:
	rm *.o a.out
