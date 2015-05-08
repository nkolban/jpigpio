package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.devices.NRF24L01;

public class Test_NRF24L01_Client {
	private NRF24L01 nrf24l01;

	public static void main(String args[]) {
		System.out.println("Test_NRF24L01_Client");
		Test_NRF24L01_Client app = new Test_NRF24L01_Client();
		app.run();
	}

	public void run() {

		try {
			// JPigpio pigpio = new PigpioSocket("raspi", 8888);
			System.out.println("NRF24L01 - Client");
			JPigpio pigpio = new Pigpio();
			// pigpio.setDebug(true);
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			nrf24l01 = new NRF24L01(pigpio);
			p("init");
			nrf24l01.init(17, 18);
			byte rAddr[] = { 'c', 'l', 'i', 'e', '1' };

			p("setRADDR");
			nrf24l01.setRADDR(rAddr);
			p("setConfig");
			nrf24l01.config(4);

			byte tAddr[] = { 's', 'e', 'r', 'v', '1' };

			p("getRFChannel");
			System.out.println("RF Channel: " + nrf24l01.getRFChannel());
			p("getAutomaticRetransmission");
			System.out.println(nrf24l01.setupRetrToString(nrf24l01.getAutomaticRetransmission()));
			p("setupAddressWidthToString");
			System.out.println("Address widths: " + nrf24l01.setupAddressWidthToString(nrf24l01.getAddressWidths()));
			while (true) {
				p("setTADDR");
				nrf24l01.setTADDR(tAddr);
				byte data[] = { 'H', 'E', 'L', 'O' };
				System.out.println("Sending ...");
				nrf24l01.send(data);
				while (nrf24l01.isSending()) {
					logStatus();
					logConfig();
					//pigpio.gpioDelay(1, JPigpio.PI_SECONDS);
				}
				System.out.println("Finished sending ...");
				//pigpio.gpioDelay(1000 * 10);
				System.out.println("Waiting for data ...");
				while (!nrf24l01.dataReady()) {
					System.out.println("No data ...");
					logStatus(); logConfig(); logFifoStatus();
					pigpio.gpioDelay(2, JPigpio.PI_SECONDS);
				}

				System.out.println("Getting data ...");
				nrf24l01.getData(data);
				System.out.println("Got data");
				pigpio.gpioDelay(10, JPigpio.PI_SECONDS);
			} // Loop again!!
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