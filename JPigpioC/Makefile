CC=gcc
CFLAGS=-c -Wall -fPIC -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux
JAVACLASSDIR=../JPigpio/bin
LIBDIR=/mnt/share/opt/lib
TARGETLIB=libJPigpioC.so
OBJECTS=JPigpioC.o

all: jpigpio_Pigpio.h $(TARGETLIB)
	cp $(TARGETLIB) $(LIBDIR)

$(TARGETLIB): $(OBJECTS)
	gcc -shared -Wl,-soname,$@ -o $@ $(OBJECTS) -lpigpio -lpthread -lrt

jpigpio_Pigpio.h:
	javah -cp $(JAVACLASSDIR) jpigpio.Pigpio
	
.c.o:
	$(CC) $(CFLAGS) $<
	
clean:
	rm -f *.o $(TARGETLIB) jpigpio_Pigpio.h $(LIBDIR)/$(TARGETLIB)

install: $(TARGETLIB)
	cp $< $(LIBDIR)
