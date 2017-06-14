LIBSDIR=/mnt/share/opt/lib
PROGRAMS=test_nunchuck test_servo
all: $(PROGRAMS)


test_servo: test_servo.o
	gcc -L$(LIBSDIR)  -o test_servo test_servo.o -lJPigpioC -lpigpio -lpthread -lrt
		
test_nunchuck: test_nunchuck.o
	gcc -L$(LIBSDIR)  -o test_nunchuck test_nunchuck.o -lJPigpioC -lpigpio -lpthread -lrt

.c.o:
	gcc -Wall -I.. -c $<
	
clean:
	rm $(PROGRAMS) *.o