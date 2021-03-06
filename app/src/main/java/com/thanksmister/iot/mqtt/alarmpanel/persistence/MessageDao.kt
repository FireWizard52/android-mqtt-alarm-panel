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

package com.thanksmister.iot.mqtt.alarmpanel.persistence

import androidx.room.*

import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Data Access Object for the messages table.
 */
@Dao
interface MessageDao {

    /**
     * Get a message by id.
     * @return the message from the table with a specific id.
     */
    @Query("SELECT * FROM Messages WHERE messageId = :id")
    fun getMessageById(id: String): Flowable<MessageMqtt>

    /**
     * Get all messages
     * @return list of all messages
     */
    @Query("SELECT * FROM Messages ORDER BY createdAt DESC")
    fun getMessages(): Flowable<List<MessageMqtt>>

    /**
     * Get all messages
     * @return list of all messages
     */
    @Query("SELECT * FROM Messages WHERE type = :type ORDER BY createdAt DESC LIMIT 1")
    fun getMessages(type: String): Flowable<List<MessageMqtt>>

    /**
     * Get all messages
     * @return list of all messages
     */
    @Query("SELECT * FROM Messages WHERE type = :type ORDER BY createdAt DESC LIMIT 1")
    fun getSensorMessages(type: String): Maybe<List<MessageMqtt>>

    /**
     * Insert a message in the database. If the message already exists, replace it.
     * @param user the message to be inserted.
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MessageMqtt)

    @Transaction
    fun insertMessageTransaction(message: MessageMqtt) {
        insertMessage(message)
    }

    /**
     * Delete all messages.
     */
    @Query("DELETE FROM Messages")
    fun deleteAllMessages()
}