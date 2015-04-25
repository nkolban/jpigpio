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


/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioInitialize
 * Signature: ()V
 */
void JNICALL Java_jpigpio_Pigpio_gpioInitialize(JNIEnv *env, jobject obj) {
	int rc = gpioInitialise();
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
	}
} // End of Java_jpigpio_Pigpio_gpioInitialize

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioTerminate
 * Signature: ()V
 */
void JNICALL Java_jpigpio_gpioTerminate(JNIEnv *env, jobject obj) {
	return gpioTerminate();
}

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioSetMode
 * Signature: (II)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioSetMode(JNIEnv *env, jobject obj, jint pin, jint mode) {
	int rc = gpioSetMode(pin, mode);
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
jint JNICALL Java_jpigpio_Pigpio_gpioGetMode(JNIEnv *env, jobject obj, jint pin) {
	return gpioGetMode(pin);
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
jint JNICALL Java_jpigpio_Pigpio_gpioRead(JNIEnv *env, jobject obj, jint pin) {
	return gpioRead(pin);
}

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioWrite
 * Signature: (II)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioWrite(JNIEnv *env, jobject obj, jint pin, jint value) {
	int rc = gpioWrite(pin, value);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
}

/*
 * Class:     jpigpio_Pigpio
 * Method:    i2cOpen
 * Signature: (II)I
 */
jint JNICALL Java_jpigpio_Pigpio_i2cOpen(JNIEnv *env, jobject obj, jint i2cBus,
		jint i2cAddr) {
	return i2cOpen(i2cBus, i2cAddr, 0);
}

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
}

/*
 * Class:     jpigpio_Pigpio
 * Method:    i2cReadDevice
 * Signature: (I[B)I
 */
jint JNICALL Java_jpigpio_Pigpio_i2cReadDevice(JNIEnv *env, jobject obj,
		jint handle, jintArray array) {

	unsigned count = (*env)->GetArrayLength(env, array);
	char *buf = malloc(count);
	int *intArray = malloc(count * sizeof(int));

	(*env)->GetIntArrayRegion(env, array, 0, count, intArray);

	int i;
	for (i = 0; i < count; i++) {
		buf[i] = intArray[i];
	}

	int rc = i2cReadDevice(handle, buf, count);

	for (i = 0; i < count; i++) {
		intArray[i] = buf[i];
	}
	(*env)->SetIntArrayRegion(env, array, 0, count, intArray);

	free(intArray);
	free(buf);

	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return rc;
	}
	return rc;
} // End of Java_jpigpio_Pigpio_i2cReadDevice

/*
 * Class:     jpigpio_Pigpio
 * Method:    i2cWriteDevice
 * Signature: (I[B)V
 */
void JNICALL Java_jpigpio_Pigpio_i2cWriteDevice(JNIEnv *env, jobject obj, jint handle, jbyteArray array) {
	char *buf;
	unsigned count;
	count = (*env)->GetArrayLength(env, array);
	buf = malloc(count);
	int *intArray = malloc(count * sizeof(int));

	(*env)->GetIntArrayRegion(env, array, 0, count, intArray);

	int i;
	for (i = 0; i < count; i++) {
		buf[i] = intArray[i];
	}
	int rc = i2cWriteDevice(handle, buf, count);
	free(intArray);
	if (rc < 0) {
		(*env)->Throw(env, createPigpioException(env, rc));
		return;
	}
} // End of Java_jpigpio_Pigpio_i2cWriteDevice

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioDelay
 * Signature: (I)V
 */
void JNICALL Java_jpigpio_Pigpio_gpioDelay(JNIEnv *env, jobject obj, jint delay) {
	gpioDelay(delay);
} // End of Java_jpigpio_Pigpio_gpioDelay

/*
 * Class:     jpigpio_Pigpio
 * Method:    gpioTick
 * Signature: ()J
 */
jlong JNICALL Java_jpigpio_Pigpio_gpioTick(JNIEnv *env, jobject obj) {
	return gpioTick();
} // End of Java_jpigpio_Pigpio_gpioTick

/**
 * Create a new Java Exception
 */
jthrowable createPigpioException(JNIEnv *env, int rc) {
	jclass pigpioExceptionClass = (*env)->FindClass(env, "jpigpio/PigpioException");
	jmethodID pigpioExceptionConstructorId = (*env)->GetMethodID(env, pigpioExceptionClass, "<init>", "(I)V");
	jthrowable newPigpioException = (*env)->NewObject(env, pigpioExceptionClass, pigpioExceptionConstructorId, rc);
	return newPigpioException;
} // End of createNewException
