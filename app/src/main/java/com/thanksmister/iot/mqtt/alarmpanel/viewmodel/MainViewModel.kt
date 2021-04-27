/*
 * Copyright (c) 2018 ThanksMister LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thanksmister.iot.mqtt.alarmpanel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.thanksmister.iot.mqtt.alarmpanel.persistence.*
import com.thanksmister.iot.mqtt.alarmpanel.utils.MqttUtils
import com.thanksmister.iot.mqtt.alarmpanel.utils.MqttUtils.Companion.STATE_DISABLED
import com.thanksmister.iot.mqtt.alarmpanel.utils.MqttUtils.Companion.TYPE_SENSOR
import com.thanksmister.iot.mqtt.alarmpanel.utils.MqttUtils.Companion.TYPE_ALARM
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject
constructor(application: Application,
            private val messageDataSource: MessageDao,
            private val sensorSource: SensorDao,
            private val sunSource: SunDao,
            private val dashboardSource: DashboardDao,
            private val configuration: Configuration) : AndroidViewModel(application) {

    private val disposable = CompositeDisposable()
    private val toastText = ToastMessage()
    private val alertText = AlertMessage()
    private val snackbarText = SnackbarMessage()

    fun getToastMessage(): ToastMessage {
        return toastText
    }

    fun getAlertMessage(): AlertMessage {
        return alertText
    }

    fun getSnackBarMessage(): SnackbarMessage {
        return snackbarText
    }

    private fun showSnackbarMessage(message: Int) {
        snackbarText.value = message
    }

    private fun showAlertMessage(message: String) {
        alertText.value = message
    }

    private fun showToastMessage(message: String) {
        toastText.value = message
    }

    fun getMessages():Flowable<List<MessageMqtt>> {
        return messageDataSource.getMessages()
                .filter {messages -> messages.isNotEmpty()}
    }

    /**
     * Get sensors.
     * @return a [Flowable]
     */
    fun getSensors(): Flowable<List<Sensor>> {
        return sensorSource.getSenors()
    }

    inner class AlarmState {
        var payload: String? = null
        var delay: Int? = -1
    }

    fun getAlarmState():Flowable<AlarmState> {
        return messageDataSource.getMessages(TYPE_ALARM)
                .filter {messages -> messages.isNotEmpty()}
                .map {messages -> messages[messages.size - 1]}
                .map {message ->
                    val state = AlarmState()
                    state.delay = message.delay
                    state.payload = message.payload
                    state
                }
    }

    fun getDashboards():Flowable<List<Dashboard>> {
        return dashboardSource.getDashboards()
    }

    fun clearMessages():Completable {
        return Completable.fromAction {
            messageDataSource.deleteAllMessages()
        }
    }

    fun hasPlatform() : Boolean {
        return (configuration.hasPlatformModule() && configuration.webUrl?.isNotEmpty()?:false)
    }

    fun setAlarmMode(value: String) {
        configuration.alarmMode = value
    }

    fun getAlarmMode(): String {
        return configuration.alarmMode
    }

    fun getSun(): Flowable<Sun> {
        return sunSource.getItems()
                .filter {items -> items.isNotEmpty()}
                .map { items -> items[items.size - 1] }
    }

    public override fun onCleared() {
        if (!disposable.isDisposed) {
            disposable.clear()
        }
    }
}