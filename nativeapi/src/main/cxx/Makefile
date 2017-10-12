# Temporary Makefile, until I get around to maven native plugin
# javah -cp ../../../../core/target/classes   org.ab1rw.candora.core.adapter.NativeSocketCANAdapter


JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt
CPPFLAGS=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -g 

all:    libcandora-jni.so

gitversion.cpp:   ../../../../.git/HEAD ../../../../.git/index
	echo "namespace org_ab1rw_candora { " > $@
	echo "const char *gitversion = \"$(shell git rev-parse HEAD)\";" >> $@
	echo "}; " >> $@


org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.h:
	javah -cp ../../../../core/target/classes   org.ab1rw.candora.core.adapter.NativeSocketCANAdapter

# temporarily for now
#a.out:  candora-native.o
#	gcc -static candora-native.o -o a.out
#candora-native.o: candora-native.c

# Shared library

libcandora-jni.so: org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o gitversion.o
	ld -shared -fpic org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o gitversion.o -o libcandora-jni.so



org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o :  org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.cpp org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.h

clean:
	rm -f gitversion.cpp *.o a.out *.so
