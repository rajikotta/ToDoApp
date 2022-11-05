package com.raji.todoapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.raji.todoapp.data.PreferenceManager.PreferencesKey.HIDE_COMPLETED
import com.raji.todoapp.data.PreferenceManager.PreferencesKey.SORT_ORDER
import com.raji.todoapp.ui.viewmodel.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    val preferenceFlow = context.dataStore.data.catch { exception ->
        if (exception is IOException)
            emit(emptyPreferences())
        else
            throw exception

    }.map { preferences ->


        val sortOrder = SortOrder.valueOf(preferences[SORT_ORDER] ?: SortOrder.BY_DATE.name)
        val hideCompleted = preferences[HIDE_COMPLETED] ?: false

        FilterPreferences(sortOrder, hideCompleted)
    }


    suspend fun updateSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preference ->
            preference[SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        context.dataStore.edit { preference ->
            preference[HIDE_COMPLETED] = hideCompleted
        }
    }

    private object PreferencesKey {


        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }
}