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

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


//將SleepNight作為唯一的列表entites,設置版本為1，更改架構時都必須增加版本，exportSchema為false，以免保留架構版本歷史記錄備份
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {
    abstract val sleepDatabaseDao: SleepDatabaseDao     //聲明一個返回的抽象值SleepDatabaseDao


    //提供數據庫
    //INSTANCE為數據庫聲明一個私有的可為空的變量，並將其初始化為null
    //volatile變量的值將永遠不會被緩存，並且所有讀寫操作都將在主內存中進行。這有助於確保的值INSTANCE始終是最新的
    companion object {
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        //添加一個synchronized{}塊。傳遞，this以便您可以訪問上下文
        //synchronized意味著一次只能有一個執行線程進入該代碼塊，從而確保數據庫僅被初始化一次
        fun getInstance(context: Context): SleepDatabase {
            synchronized(this) {
                var instance = INSTANCE

                //檢查是否instance為空，即尚無數據庫
                if (instance == null) {
                    //使用數據庫構建器獲取數據庫
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                        )
                            //為遷移對象提供架構更改時的遷移策略
                            .fallbackToDestructiveMigration()
                            .build()
                        INSTANCE = instance
                    }

                    //忽略返回類型不匹配錯誤；完成後，您將永遠不會返回null
                    return instance
                }
            }
        }
    }


