FDIRS =-F/System/Library/Frameworks/JavaVM.framework/Frameworks\
       -F/System/Library/Frameworks\

FRAMEWORKS = -framework JavaVM -framework Cocoa -framework JavaNativeFoundation -framework ApplicationServices\

INCLUDES = -I/System/Library/Frameworks/JavaVM.framework/Headers\

LIBFILE = ../bin/org/fruit/alayer/macosx/libax.jnilib

AX_CLASSFILE = ../bin/org/fruit/alayer/macosx/AX.class
AX_CLASS = org.fruit.alayer.macosx.AX

FLAGS= -O3


.PHONY: all clean

all: $(LIBFILE)

./AX.h: $(AX_CLASSFILE)
	javah -jni -force -o ./AX.h -classpath ../bin $(AX_CLASS)

$(LIBFILE): AX.h Main.m Main.h
	gcc $(FLAGS) $(INCLUDES)  $(FDIRS) $(FRAMEWORKS) -dynamiclib -o $(LIBFILE) Main.m


clean:
	rm -f AX.h
	rm -f *.o
	rm -f *.jnilib