package jpigpio;

/**
 * Created by Jozef on 17.04.2016.
 */
interface PiCallback {

    int dataBit = 0; // bit-map representing respective GPIOs

    void callback();

}
