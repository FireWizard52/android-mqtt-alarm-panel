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

package com.thanksmister.iot.mqtt.alarmpanel.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thanksmister.iot.mqtt.alarmpanel.R
import com.thanksmister.iot.mqtt.alarmpanel.persistence.Sensor
import com.thanksmister.iot.mqtt.alarmpanel.utils.MqttUtils
import kotlinx.android.synthetic.main.adapter_sensor_preference.view.*

class SensorAdapter(private val sensorTopic: String, private val listener: OnItemClickListener) : RecyclerView.Adapter<SensorAdapter.ViewHolder>() {

    private var items: List<Sensor> = emptyList()

    interface OnItemClickListener {
        fun onItemClick(sensor: Sensor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_sensor_preference, parent, false)
        return ViewHolder(v)
    }

    fun setItems(items: List<Sensor>){
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (items.isNotEmpty()) items.size else 0
    }

    override fun onBindViewHolder(holder: SensorAdapter.ViewHolder, position: Int) {
        holder.bindItems(items[position], sensorTopic, listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(item: Sensor, sensorTopic: String, listener: OnItemClickListener) {

            itemView.nameText.text = item.name
            itemView.topicSensorText.text = sensorTopic + item.topic
            itemView.payloadSensorText.text = itemView.context.getString(R.string.text_sensor_payload, item.payloadActive, item.payloadInactive)

            when (item.type) {
                MqttUtils.SENSOR_DOOR_TYPE -> {
                    itemView.typeIcon.setImageResource(R.drawable.ic_door)
                }
                MqttUtils.SENSOR_WINDOW_TYPE -> {
                    itemView.typeIcon.setImageResource(R.drawable.ic_window_open)
                }
                MqttUtils.SENSOR_MOTION_TYPE -> {
                    itemView.typeIcon.setImageResource(R.drawable.ic_motion_sensor)
                }
                MqttUtils.SENSOR_CAMERA_TYPE -> {
                    itemView.typeIcon.setImageResource(R.drawable.ic_video)
                }
                MqttUtils.SENSOR_SOUND_TYPE -> {
                    itemView.typeIcon.setImageResource(R.drawable.ic_baseline_mic)
                }
                else -> {
                    itemView.typeIcon.setImageResource(R.drawable.ic_sensor) // generic sensor icon
                }

            }

            when {
                item.notify -> itemView.notifyIcon.visibility = View.VISIBLE
                else -> itemView.notifyIcon.visibility = View.GONE
            }

            when {
                item.alarmMode -> itemView.alarmIcon.visibility = View.VISIBLE
                else -> itemView.alarmIcon.visibility = View.GONE
            }

            when {
                item.alarmMode || item.notify -> itemView.iconLayout.visibility = View.VISIBLE
                else -> itemView.iconLayout.visibility = View.GONE
            }

            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}