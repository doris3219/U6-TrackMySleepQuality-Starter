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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */


class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    //創建一個LiveData要在應用程序導航至SleepQualityFragment時更改的
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    //添加一個doneNavigating()功能來重置觸發導航的變量
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    //定義viewModelJob並為其分配實例Job。viewModelJob當不再使用視圖模型並將其銷毀時，這使您可以取消由此視圖模型啟動的所有協程
    private var viewModelJob = Job()

    //當ViewModel被銷毀時，onCleared()被調用
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //範圍確定協程將在哪個線程上運行，並且範圍還需要了解作業,透過CoroutineScope獲取作用域，然後傳遞調度程序和作業
    //使用Dispatchers.Main意味著在中啟動的協程uiScope將在主線程上運行
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //定義一個變量tonight來保存當前夜晚。設置變量MutableLiveData，因為您需要能夠觀察和更改數據
    private var tonight = MutableLiveData<SleepNight?>()

    init {
        initializeTonight()
    }

    //tonight通過調用從數據庫獲取的值getTonightFromDatabase()，並將其分配給tonight.value
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase(
            )
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    fun onStartTracking() {

        //啟動協程，以此來持續更新UI
        uiScope.launch {
            val newNight = SleepNight()

            insert(newNight)

            //更新tonight
            tonight.value = getTonightFromDatabase()
        }

    }

    //insert()為一個private suspend以a SleepNight為參數的函數。
    private suspend fun insert(night: SleepNight) {
        //通過insert()DAO 調用將夜晚插入數據庫中
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    //睡眠開始按鈕OnClick事件
    //從數據庫獲取所有夜晚，並將它們分配給nights變量
    private val nights = database.getAllNights()

    //睡眠開始按鈕OnClick事件
    //傳遞nights到map()函數Transformations。要訪問您的字符串資源，
    // 請將映射函數定義為call formatNights()。 供應nights和Resources對象。
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }


    //睡眠停止按鈕OnClick事件
    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight        //當此變量具有值時，應用會導航至SleepQualityFragment，並在夜間進行
        }
    }
    //睡眠停止按鈕OnClick事件
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }


    //透過OnClear及clear為“清除”按鈕添加點擊處理程序
    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value = true
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }


    val startButtonVisible = Transformations.map(tonight) {
        it == null
    }
    val stopButtonVisible = Transformations.map(tonight) {
        it != null
    }
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    //創建封裝事件
    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

}

