# Build Candora JNI Adapter.
#
#

# This is the default location of the Oracle JDK on Raspberry Pi
JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt

CPPFLAGS=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -g 

all:    libcandora-jni.so

gitversion.cpp:   ../../../../.git/HEAD ../../../../.git/index
	echo "namespace org_ab1rw_candora { " > $@
	echo "const char *gitversion = \"$(shell git rev-parse HEAD)\";" >> $@
	echo "}; " >> $@
	echo "Generated git build marker..."


org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.h:
	javah -cp ../../../../core/target/classes   org.ab1rw.candora.core.adapter.NativeSocketCANAdapter
	echo "Generated JNI headers..."


# Shared library
libcandora-jni.so: org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o gitversion.o
	ld -shared -fpic org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o gitversion.o -o libcandora-jni.so
    echo "Packaged shared library object..."

org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.o :  org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.cpp \
                                                            org_ab1rw_candora_core_adapter_NativeSocketCANAdapter.h

clean:
	rm -f gitversion.cpp *.o *.so
