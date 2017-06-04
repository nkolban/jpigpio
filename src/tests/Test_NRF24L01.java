package tests;

import jpigpio.*;
import jpigpio.devices.NRF24L01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test_NRF24L01 {
	private NRF24L01 nrf24l01;
	int cePin = 22;  //GPIO number, e.g. GPIO 22 = PIN 15
	int csnPin = 8;  //GPIO number, e.g. GPIO 8 = PIN 24

	public static void main(String args[]) throws PigpioException {
		System.out.println("Test_NRF24L01");
		Test_NRF24L01 app = new Test_NRF24L01();
		app.run();
	}

	public void run() throws PigpioException {

		JPigpio pigpio = new PigpioSocket("pigpiod-host", 8888);

		//JPigpio pigpio = new Pigpio();

		pigpio.gpioInitialize();

		nrf24l01 = new NRF24L01(pigpio);

		p("Initializing...");
		if (!nrf24l01.init(cePin, csnPin)) {
			p("Failed to initialize nRF module. Module not present?");
			nrf24l01.terminate();
			pigpio.gpioTerminate();
			return;
		}

		// 5 byte address width
		nrf24l01.setAddressWidth(5);

		// set remote device address - to which data will be sent and from which data will be received
		byte rAddr[] = { 'R', 'C', 'V', '0', '1' };
		nrf24l01.setRADDR(rAddr);

		// set transmitter device address - from which data will be sent
		byte tAddr[] = { 'S', 'N', 'D', '0', '1' };
		nrf24l01.setTADDR(tAddr, true);

		// following params should be configured the same as the other side
		nrf24l01.setPayloadSize(32); 				// 32 bytes payload
		nrf24l01.setChannel(76);     				// RF channel
		nrf24l01.setRetries(5,15);   				// 5 retries, 15x250ms delay between retries
		nrf24l01.setCRCSize(2);      				// 16 bit CRC
		nrf24l01.setDataRate(NRF24L01.RF24_1MBPS);	// 1Mbit/s data rate
		nrf24l01.setAutoACK(false);					// expecting automatic acknowledgements from receiver
		nrf24l01.setPALevel(NRF24L01.RF24_PA_LOW);  // low power - testing devices won't be so far apart

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		byte role = 0;
		String cmd = "R";  // start with sender role

		byte counter = 0;
		byte senderData[] = new byte[] {1,2,3,4};
		byte receiverReply[] = new byte[] {99,99,0,0,0,0,0};
		byte data32[] = new byte[32];

		nrf24l01.clearRegisterBits(NRF24L01.CONFIG_REGISTER,(byte)(1<<NRF24L01.MASK_RX_DR));
		nrf24l01.printDetails(System.out);

		showHelp();
		while (role != 3) {

			// read keyboard input
			try {
				if (br.ready())
					cmd = br.readLine();
			} catch (IOException e) {
				p("IOException happened.");
			}

			switch (cmd.toUpperCase()) {
				case "S":
					p("***** Switching to SENDER role");
					cmd = "";
					role = 0;
					nrf24l01.stopListening();
					nrf24l01.powerDown();
					nrf24l01.setRADDR(rAddr);
					nrf24l01.setTADDR(tAddr, true);
					nrf24l01.printDetails(System.out);
					break;

				case "R":
					p("***** Switching to RECEIVER role");
					cmd = "";
					role = 1;
					nrf24l01.powerDown();
					nrf24l01.setRADDR(tAddr);
					nrf24l01.setTADDR(rAddr, true);
					nrf24l01.printDetails(System.out);
					nrf24l01.startListening();
					break;

				case "E":
					role = 3;
					break;

				case "":
					break;
				default:
					showHelp();
			}

			// SENDER ROLE ======================
			if (role == 0){
				counter++;
				senderData[0] = counter;
				System.out.print("Sending ... " + Utils.dumpData(senderData) + ": ");

				int result = nrf24l01.write(senderData);
				switch (result) {
					case 0:
						p("OK");
						break;
					case 1:
						p("FAILED - max retries");
						break;
					case 2:
						p("FAILED - timeout");
						break;
				}

				// Handle reply from receiver
				nrf24l01.startListening();  // start listening for reply
				try { Thread.sleep(100);} catch (InterruptedException e){} // wait for reply

				// if OK, then read reply
				if (nrf24l01.dataReady()) {
					nrf24l01.getData(data32);
					System.out.print("Reply : "+Utils.dumpData(data32));
				}

				nrf24l01.stopListening();  // done with listening, back to sending

				try { Thread.sleep(200);} catch (InterruptedException e){}
			}

			// RECEIVER ROLE =======================
			if (role == 1) {
				if (nrf24l01.dataReady()) {
					nrf24l01.getData(data32);
					System.out.println("Received : "+Utils.dumpData(data32));

					nrf24l01.stopListening();
					for (byte i=0;i<4;i++)
						receiverReply[2+i] = data32[i];
					nrf24l01.write(receiverReply);
					nrf24l01.startListening();

				} else {
					p("Waiting for sender..." + counter++);
					try { Thread.sleep(1000);} catch (InterruptedException e){}
				}

			}

		}

		nrf24l01.terminate();
		pigpio.gpioTerminate();

		System.out.println("Done.");

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

	private void showHelp() {
		p("Type:");
		p("s - to switch to sender mode");
		p("r - to switch to receiver mode");
		p("e - to exit");
	}

} // End of class
// End of file