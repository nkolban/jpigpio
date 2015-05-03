/*
 * CommonPigpio.c
 *
 *  Created on: Apr 18, 2015
 *      Author: kolban
 */

#include <stdlib.h>
#include <stdio.h>
#include <pigpio.h>
#include <jni.h>
#include "jpigpio_Pigpio.h"
#include "JPigpioC.h"

jthrowable createPigpioException(JNIEnv *env, int rc);
extern void alertCallback(int gpio, int level, unsigned int tick);

JavaVM *g_vm;

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
}

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
}

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioRead
 * Signature: (I)I
 */
jint JNICALL Java_jpigpio_Pigpio_gpioRead(JNIEnv *env, jobject obj, jint gpio) {
	return gpioRead(gpio);
}

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioWrite
 * Signature: (IZ)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioWrite(JNIEnv *env, jobject obj, jint gpio, jboolean value) {
	int rc = gpioWrite(gpio, value);
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

	(*env)->GetByteArrayRegion(env, txDataArray, 0, count, (jbyte *)buf);

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

	(*env)->GetByteArrayRegion(env, txData, 0, count, (jbyte *)buf);

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

	(*env)->GetByteArrayRegion(env, txData, 0, count, (jbyte *)txBuf);
	int rc = spiXfer(handle, txBuf, rxBuf, count);
	free(txBuf);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		free(rxBuf);
		return rc;
	}
	(*env)->SetByteArrayRegion(env, rxData, 0, count, (jbyte *)rxBuf);
	free(rxBuf);
	return rc;
} // End of Java_jpigpio_Pigpio_spiXfer

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

/**
 * Create a new Java Exception
 */
jthrowable createPigpioException(JNIEnv *env, int rc) {
	jclass pigpioExceptionClass = (*env)->FindClass(env, "jpigpio/PigpioException");
	jmethodID pigpioExceptionConstructorId = (*env)->GetMethodID(env, pigpioExceptionClass, "<init>", "(I)V");
	jthrowable newPigpioException = (*env)->NewObject(env, pigpioExceptionClass, pigpioExceptionConstructorId, rc);
	return newPigpioException;
} // End of createNewException
