package tests;

import jpigpio.*;
import jpigpio.devices.NRF24L01;

public class Test_NRF24L01_Client {
	private NRF24L01 nrf24l01;
	int cePin = 22;  //GPIO number, e.g. GPIO 22 = PIN 15
	int csnPin = 8;  //GPIO number, e.g. GPIO 8 = PIN 24

	public static void main(String args[]) {
		System.out.println("Test_NRF24L01_Client");
		Test_NRF24L01_Client app = new Test_NRF24L01_Client();
		app.run();
	}

	public void run() {

		try {

			System.out.println("NRF24L01 - Client");
			JPigpio pigpio = new PigpioSocket("pigpiod-host", 8888);
			//JPigpio pigpio = new Pigpio();
			// pigpio.setDebug(true);
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			nrf24l01 = new NRF24L01(pigpio);
			p("init");
			nrf24l01.init(cePin, csnPin);

			byte rAddr[] = { 'D', 'S', 'P', '0', '1' };
			p("setRADDR");
			nrf24l01.setRADDR(rAddr);

			byte tAddr[] = { 'C', 'B', 'X', '0', '1' };
			p("setTADDR");
			nrf24l01.setTADDR(tAddr);

			p("setConfig");
			nrf24l01.config(32);
			nrf24l01.setChannel(76);

			p("getRFChannel");
			System.out.println("RF Channel: " + nrf24l01.getRFChannel());

			p("getAutomaticRetransmission");
			System.out.println(nrf24l01.setupRetrToString(nrf24l01.getAutomaticRetransmission()));

			nrf24l01.setAddressWidths(0b11);
			p("setupAddressWidthToString");
			System.out.println("Address widths: " + nrf24l01.setupAddressWidthToString(nrf24l01.getAddressWidths()));

			//nrf24l01.printDetails(System.out);
			nrf24l01.setCRCSize(2);

			nrf24l01.setDataRate(NRF24L01.RF24_1MBPS);

			nrf24l01.printDetails(System.out);

			byte data[] = new byte[5];
			data[0] = 0x20;
			data[1] = 3;
			data[2] = '1';
			data[3] = '1';

			System.out.println("Sending ...");
			nrf24l01.send(data);
			while (nrf24l01.isSending()) {
				logStatus();
				logConfig();
				//pigpio.gpioDelay(1, JPigpio.PI_SECONDS);
			}

			nrf24l01.terminate();

			System.out.println("Done.");

		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}

	private void p(String text) {
		System.out.println(text);
	}

	private void logStatus() throws PigpioException {
		byte status = nrf24l01.getStatus();
		p(String.format("status = 0x%x %s", status, nrf24l01.statusToString(status)));
	}
	
	private void logFifoStatus() throws PigpioException {
		byte status = nrf24l01.getFIFOStatus();
		p(String.format("FIFO Status = 0x%x %s", status, nrf24l01.fifoStatusToString(status)));
	}

	private void logConfig() throws PigpioException {
		byte config = nrf24l01.getConfig();
		System.out.println(String.format("config = %x %s", config, nrf24l01.configToString(config)));
	}
} // End of class
// End of file