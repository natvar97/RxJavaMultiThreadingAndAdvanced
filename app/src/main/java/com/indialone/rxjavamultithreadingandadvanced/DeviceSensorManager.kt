package com.indialone.rxjavamultithreadingandadvanced

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.Observable

class DeviceSensorManager(context: Context) {

    private var accelerometerEventObservable: Observable<SensorEvent> = Observable
        .create { emitter ->
            val sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    emitter.onNext(event!!)
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            val sensorManager: SensorManager =
                context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

            sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )

            emitter.setCancellable {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }

    fun accelerometerEventObservable(): Observable<SensorEvent> {
        return accelerometerEventObservable
    }


}