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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SleepDatabaseDao{
    //添加一個insert()將Entity類的實例SleepNight 作為其參數的函數
    @Insert
    fun insert(night: SleepNight)

    //更新的實體是具有與傳入密鑰相同的密鑰的實體
    @Update
    fun update(night: SleepNight)

    //使用@Query帶有參數並返回nullable 的get()函數添加註釋
    //:key => 在查詢中使用冒號表示法引用函數中的參數
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): SleepNight?

    //@Delete註釋刪除一個項目，你可以使用@Delete，並提供夜的列表中刪除
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    //getTonight()功能。使SleepNightreturn by by為getTonight()null，以便函數可以處理表為空的情況
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?

    //讓SQLite查詢daily_sleep_quality_table以降序返回的所有列
    //getAllNights()返回列表SleepNight實體LiveData。為您Room保持LiveData更新狀態，這意味著您只需要顯式獲取一次數據。
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}
