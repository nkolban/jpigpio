sudo rm -rf /var/run/pigpio.pid
export LIBPATH="/mnt/share/opt/lib"
sudo java -Djava.library.path=$LIBPATH -cp ../bin tests/Test_VS1053