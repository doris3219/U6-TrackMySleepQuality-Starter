/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//註釋數據類@Entity。命名表格daily_sleep_quality_table
@Entity(tableName = "daily_sleep_quality_table")

data class SleepNight(
        //將nightId設為主鍵。將參數設置為autoGenerate，true以便Room為每個實體生成ID。這樣可以保證每個ID是唯一的
        @PrimaryKey(autoGenerate = true)
        var nightId: Long = 0L,

        @ColumnInfo(name = "start_time_milli")
        val startTimeMilli: Long = System.currentTimeMillis(),

        @ColumnInfo(name = "end_time_milli")
        var endTimeMilli: Long = startTimeMilli,        //初始化結束時間。將其設置為開始時間，以表示尚未記錄結束時間

        @ColumnInfo(name = "quality_rating")
        var sleepQuality: Int = -1          //初始化sleepQuanity將值設為-1，表示尚未收集到任何質量數據

)
