sudo rm -rf /var/run/pigpio.pid
export LIBPATH="/mnt/share/opt/lib"
java -Djava.library.path=$LIBPATH -cp ../bin tests/Test_Nunchuck