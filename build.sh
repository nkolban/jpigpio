cd JPigpio
javac $(find . -name "*.java") -d bin
make clean
make
cd ..
cd JPigpioC
make clean
make
