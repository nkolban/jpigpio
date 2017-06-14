/*
 * CommonPigpio.c
 *
 *  Created on: Apr 18, 2015
 *      Author: kolban
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <pigpio.h>
#include <jni.h>
#include "jpigpio_Pigpio.h"
#include "JPigpioC.h"

#define DEBUG 1

jthrowable createPigpioException(JNIEnv *env, int rc);
void alertCallback(int gpio, int level, unsigned int tick);
unsigned invert(unsigned level);

JavaVM *g_vm;
int debug = 0;

#ifdef DEBUG
int lastTime;
extern void logDebug(char *text);
char debugText[2000];
#endif

// Define an array of callback functions
jobject alertFunctions[MAXPINS];

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioInitialize
 * Signature: ()V
 */
void JNICALL Java_jpigpio_Pigpio_gpioInitialize(JNIEnv *env, jobject obj) {
	// Zero out any callbacks

	int i;
	for (i = 0; i < MAXPINS; i++) {
		alertFunctions[i] = NULL;
	}
	int rc = gpioInitialise();
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
	}
	(*env)->GetJavaVM(env, &g_vm);
#ifdef DEBUG
	lastTime = gpioTick();
#endif
} // End of Java_jpigpio_Pigpio_gpioInitialize

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioTerminate
 * Signature: ()V
 */
void JNICALL Java_jpigpio_Pigpio_gpioTerminate(JNIEnv *env, jobject obj) {
	return gpioTerminate();
}

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioSetMode
 * Signature: (II)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioSetMode(JNIEnv *env, jobject obj, jint gpio, jint mode) {
	int rc = gpioSetMode(gpio, mode);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_gpioSetMode

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioGetMode
 * Signature: (I)I
 */
jint JNICALL Java_jpigpio_Pigpio_gpioGetMode(JNIEnv *env, jobject obj, jint gpio) {
	return gpioGetMode(gpio);
} // End of Java_jpigpio_Pigpio_gpioGetMode

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioSetPullUpDown
 * Signature: (II)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioSetPullUpDown(JNIEnv *env, jobject obj, jint pin, jint pud) {
	int rc = gpioSetPullUpDown(pin, pud);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_gpioSetPullUpDown

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioRead
 * Signature: (I)Z
 */
jboolean JNICALL Java_jpigpio_Pigpio_gpioRead(JNIEnv *env, jobject obj, jint gpio) {
	return gpioRead(gpio);
} // End of Java_jpigpio_Pigpio_gpioRead

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioWrite
 * Signature: (IZ)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioWrite(JNIEnv *env, jobject obj, jint gpio, jboolean value) {
	int rc = gpioWrite(gpio, value);
#ifdef DEBUG
	if (debug) {
		sprintf(debugText, "gpioWrite: gpio: %d, value: 0x%x", gpio, value);
		logDebug(debugText);
	}
#endif
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_gpioWrite

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioServo
 * Signature: (II)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioServo(JNIEnv *env, jobject obj, jint gpio, jint pulseWidth) {
	int rc = gpioServo(gpio, pulseWidth);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_gpioServo

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioTrigger
 * Signature: (IJZ)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioTrigger(JNIEnv *env, jobject obj, jint gpio, jlong pulseLen, jboolean level) {
	int rc = gpioTrigger(gpio, pulseLen, level);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_gpioTrigger

/*
 * Class:     jpigpio_Pigpio
 * Method:    i2cOpen
 * Signature: (II)I
 */
jint JNICALL Java_jpigpio_Pigpio_i2cOpen(JNIEnv *env, jobject obj, jint i2cBus, jint i2cAddr) {
	int rc = i2cOpen(i2cBus, i2cAddr, 0);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
	}
	return rc;
} // End of Java_jpigpio_Pigpio_i2cOpen

/*
 * Class:     jpigpio_Pigpio
 * Method:    i2cClose
 * Signature: (I)V
 */
void JNICALL Java_jpigpio_Pigpio_i2cClose(JNIEnv *env, jobject obj, jint handle) {
	int rc = i2cClose(handle);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_i2cClose

/*
 * Class:     jpigpio_Pigpio
 * Method:    i2cReadDevice
 * Signature: (I[B)I
 */
jint JNICALL Java_jpigpio_Pigpio_i2cReadDevice(JNIEnv *env, jobject obj, jint handle, jbyteArray array) {

	unsigned count = (*env)->GetArrayLength(env, array);
	jbyte *buf = malloc(count);

	(*env)->GetByteArrayRegion(env, array, 0, count, buf);

	int rc = i2cReadDevice(handle, (char *) buf, count);

	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		free(buf);
		return rc;
	}
	(*env)->SetByteArrayRegion(env, array, 0, count, buf);
	free(buf);
	return rc;
} // End of Java_jpigpio_Pigpio_i2cReadDevice

/*
 * Class:     jpigpio_Pigpio
 * Method:    i2cWriteDevice
 * Signature: (I[B)V
 */
void JNICALL Java_jpigpio_Pigpio_i2cWriteDevice(JNIEnv *env, jobject obj, jint handle, jbyteArray txDataArray) {
	unsigned count = (*env)->GetArrayLength(env, txDataArray);
	char *buf = malloc(count);

	(*env)->GetByteArrayRegion(env, txDataArray, 0, count, (jbyte *) buf);

	int rc = i2cWriteDevice(handle, buf, count);

	free(buf);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_i2cWriteDevice

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioDelay
 * Signature: (J)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioDelay(JNIEnv *env, jobject obj, jlong delay) {
	gpioDelay((unsigned int) delay);
} // End of Java_jpigpio_Pigpio_gpioDelay

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioTick
 * Signature: ()J
 */
jlong JNICALL Java_jpigpio_Pigpio_gpioTick(JNIEnv *env, jobject obj) {
	return gpioTick();
} // End of Java_jpigpio_Pigpio_gpioTick

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioSetAlertFunc
 * Signature: (ILjpigpio/Alert;)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioSetAlertFunc(JNIEnv *env, jobject obj, jint gpio, jobject alert) {
	// Register the generic callback
	int rc = gpioSetAlertFunc(gpio, alertCallback);
	// Handle an error registering the alert function
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
	if (alertFunctions[gpio] != NULL) {
		(*env)->DeleteGlobalRef(env, alertFunctions[gpio]);
	}
	alertFunctions[gpio] = (*env)->NewGlobalRef(env, alert);
} // End of Java_jpigpio_Pigpio_gpioSetAlertFunc

/*
 * Class:     jpigpio_Pigpio
 * Method:    spiOpen
 * Signature: (III)I
 */
jint JNICALL Java_jpigpio_Pigpio_spiOpen(JNIEnv *env, jobject obj, jint channel, jint baudRate, jint flags) {

	int rc = spiOpen(channel, baudRate, flags);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
	}
#ifdef DEBUG
	if (debug) {
		sprintf(debugText, "spiOpen: channel=%d, baudRate=%d, flags=%x - handle=%d", channel, baudRate, flags, rc);
		logDebug(debugText);
	}
#endif
	return rc;
} // End of Java_jpigpio_Pigpio_spiOpen

/*
 * Class:     jpigpio_Pigpio
 * Method:    spiClose
 * Signature: (I)V
 */
void JNICALL Java_jpigpio_Pigpio_spiClose(JNIEnv *env, jobject obj, jint handle) {
	int rc = spiClose(handle);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
	}
} // End of Java_jpigpio_Pigpio_spiClose

/*
 * Class:     jpigpio_Pigpio
 * Method:    spiRead
 * Signature: (I[B)I
 */
jint JNICALL Java_jpigpio_Pigpio_spiRead(JNIEnv *env, jobject obj, jint handle, jbyteArray rxData) {
	unsigned count = (*env)->GetArrayLength(env, rxData);
	jbyte *buf = malloc(count);

	int rc = spiRead(handle, (char *) buf, count);

	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		free(buf);
		return rc;
	}
	(*env)->SetByteArrayRegion(env, rxData, 0, count, buf);
	free(buf);
	return rc;
} // End of Java_jpigpio_Pigpio_spiRead

/*
 * Class:     jpigpio_Pigpio
 * Method:    spiWrite
 * Signature: (I[B)I
 */
jint JNICALL Java_jpigpio_Pigpio_spiWrite(JNIEnv *env, jobject obj, jint handle, jbyteArray txData) {
	unsigned count = (*env)->GetArrayLength(env, txData);
	char *buf = malloc(count);

	(*env)->GetByteArrayRegion(env, txData, 0, count, (jbyte *) buf);

	int rc = spiWrite(handle, buf, count);

	free(buf);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return rc;
	}
	return rc;
} // End of Java_jpigpio_Pigpio_spiWrite

/*
 * Class:     jpigpio_Pigpio
 * Method:    spiXfer
 * Signature: (I[B[B)I
 */
jint JNICALL Java_jpigpio_Pigpio_spiXfer(JNIEnv *env, jobject obj, jint handle, jbyteArray txData, jbyteArray rxData) {
	unsigned count = (*env)->GetArrayLength(env, txData);
	char *txBuf = malloc(count);
	char *rxBuf = malloc(count);

	(*env)->GetByteArrayRegion(env, txData, 0, count, (jbyte *) txBuf);

	int rc = spiXfer(handle, txBuf, rxBuf, count);

	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		free(rxBuf);
		return rc;
	}
#ifdef DEBUG
	if (debug) {
		char strTXData[1024];
		char strRXData[1024];
		strcpy(strTXData, "");
		strcpy(strRXData, "");
		char strByte[10];
		unsigned int i;
		for (i = 0; i < count; i++) {
			sprintf(strByte, "0x%x ", txBuf[i]);
			strcat(strTXData, strByte);
			sprintf(strByte, "0x%x ", rxBuf[i]);
			strcat(strRXData, strByte);
		}
		sprintf(debugText, "spiXfer: handle=%d, count=%d, TX Data: %s, RX Data: %s", handle, count, strTXData, strRXData);
		logDebug(debugText);
	}
#endif
	// Verified that parms are env, target array, start index, number of bytes, source buffer
	(*env)->SetByteArrayRegion(env, rxData, 0, count, (jbyte *) rxBuf);
	free(rxBuf);
	free(txBuf);
	return rc;
} // End of Java_jpigpio_Pigpio_spiXfer

void JNICALL Java_jpigpio_Pigpio_setDebug(JNIEnv *env, jobject obj, jboolean state) {
	debug = state;
}

/**
 * Pulse a named pin and then wait for a response on a different pin.  The output pin should already
 * have a PI_OUTPUT mode and the input pin should already have a PI_INPUT mode.  The wait duration is in
 * microseconds.  The pulse hold duration is how long (in microseconds) the pulse should be held for.  The
 * default is to pulse the output high and then return low however if the pulseLow flag is set the inverse
 * will happen (pulse the output low and then return high).
 *
 * The return is how long we waited for the pulse measured in microseconds.  If no response is received, we
 * return -1 to indicate a timeout.
 * @param outGpio The pin on which the output pulse will occur.
 * @param inGpio The pin on which the input pulse will be sought.
 * @param waitDuration The maximum time to wait in microseconds.
 * @param pulseHoldDuration The time to hold the output pulse in microseconds.
 * @param pulseLow True if the pulse should be a low pulse otherwise a high pulse will be sent.
 * @return The time in microseconds waiting for a pulse or -1 to signfify a timeout.
 */

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioxPulseAndWait
 * Signature: (IIJJZ)J
 */
jlong JNICALL Java_jpigpio_Pigpio_gpioxPulseAndWait(JNIEnv *env, jobject obj, //
		jint outGpio, jint inGpio, jlong waitDuration, jlong pulseHoldDuration, jboolean pulseLow) {
	unsigned outLevel;
	long start;

#ifdef DEBUG
	char text[1024];
	sprintf(text, "gpioxPulseAndWait(outGpio=%d, inGpio=%d, waitDuration=%ld, pulseHoldDuration=%ld, pulseLow=%d)", //
			outGpio, inGpio, (long) waitDuration, (long) pulseHoldDuration, pulseLow);
	logDebug(text);
#endif
	// Determine if the output pulse should be high or low.
	if (pulseLow) {
		outLevel = PI_LOW;
	} else {
		outLevel = PI_HIGH;
	}

	// Pulse the output pin for the correct duration
	gpioWrite(outGpio, outLevel);
	gpioDelay(pulseHoldDuration);
	gpioWrite(outGpio, invert(outLevel));

	// Loop until we have waited long enough or the input changes
	int state = 1;
	// 1 = Looking for high
	// 2 = looking for low

	// Loop until high
	// Start a timer
	// Loop until low
	// return now - start timer
	long waitStart = gpioTick();
	//printf("%ld\n",gpioTick() - waitStart );
	while ((gpioTick() - waitStart) < waitDuration) {
		//printf("%ld\n",gpioTick() - waitStart );
		if (state == 1) {
			if (gpioRead(inGpio) == PI_HIGH) {
				state = 2;
				start = gpioTick();
			}
		} // End of state == 1
		else if (state == 2) {
			if (gpioRead(inGpio) == PI_LOW) {
				return gpioTick() - start;
			}
		} // End of state == 2
	} // End while loop because we timed out

	// We didn't get a response in time.
	return -1;
} // End of Java_jpigpio_Pigpio_gpioxPulseAndWait

/**
 * A callback function that is invoked when a gpioSetAlertFunc() happens.
 * We use the gpio pin value as a lookup into a saved set of Java Alert objects that have
 * been registered.  Once we have the correct Alert object, we setup the environment to
 * call back into Java to call the Alert.alert() method.
 */
void alertCallback(int gpio, int level, unsigned int tick) {
	JNIEnv *env;

	if (alertFunctions[gpio] == NULL) {
		printf("JPigpio: alertCallback(gpio=%d, level=%d, tick=%d): Odd ... alert callback but no alert function registered\n", gpio, level, tick);
		return;
	}
	//printf("JPigpio: alertCallback(gpio=%d, level=%d, tick=%d)\n", gpio, level, tick);

	int getEnvStat = (*g_vm)->GetEnv(g_vm, (void **) &env, JNI_VERSION_1_8);

	if (getEnvStat == JNI_EDETACHED) {
		(*g_vm)->AttachCurrentThread(g_vm, (void **) &env, NULL);
	}

	// We have now attached this C thread to the JVM environment.
	// Lookup the class of the Alert, lookup the alert method methodId and
	// then call the alert() method with the correct parameters.

	jclass classz = (*env)->GetObjectClass(env, alertFunctions[gpio]);
	jmethodID methodId = (*env)->GetMethodID(env, classz, "alert", "(IIJ)V");
	(*env)->CallVoidMethod(env, alertFunctions[gpio], methodId, gpio, level, (jlong) tick);

	// Cleanup the environment by detaching the thread.
	(*g_vm)->DetachCurrentThread(g_vm);
} // End of alertCallback

#ifdef DEBUG
void logDebug(char *text) {
	if (debug) {
		printf("%.6d: %s\n", gpioTick() - lastTime, text);
		lastTime = gpioTick();
	}
}
#endif

unsigned invert(unsigned level) {
	if (level == PI_HIGH) {
		return PI_LOW;
	}
	return PI_HIGH;
}

/**
 * Create a new Java Exception
 */
jthrowable createPigpioException(JNIEnv *env, int rc) {
	jclass pigpioExceptionClass = (*env)->FindClass(env, "jpigpio/PigpioException");
	jmethodID pigpioExceptionConstructorId = (*env)->GetMethodID(env, pigpioExceptionClass, "<init>", "(I)V");
	jthrowable newPigpioException = (*env)->NewObject(env, pigpioExceptionClass, pigpioExceptionConstructorId, rc);
	return newPigpioException;
} // End of createNewException
