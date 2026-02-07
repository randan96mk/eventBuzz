package app.eventbuzz.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val LAST_LATITUDE = doublePreferencesKey("last_latitude")
        val LAST_LONGITUDE = doublePreferencesKey("last_longitude")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FILTER_RADIUS_KM = doublePreferencesKey("filter_radius_km")
        val FILTER_CATEGORIES = stringPreferencesKey("filter_categories")
        val SHOW_FREE_ONLY = booleanPreferencesKey("show_free_only")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    val lastLatitude: Flow<Double?> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_LATITUDE]
    }

    val lastLongitude: Flow<Double?> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_LONGITUDE]
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: "system"
    }

    val filterRadiusKm: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[Keys.FILTER_RADIUS_KM] ?: 10.0
    }

    val filterCategories: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.FILTER_CATEGORIES]
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toSet()
            ?: emptySet()
    }

    val showFreeOnly: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SHOW_FREE_ONLY] ?: false
    }

    val authToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.AUTH_TOKEN]
    }

    suspend fun updateLastLocation(latitude: Double, longitude: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_LATITUDE] = latitude
            prefs[Keys.LAST_LONGITUDE] = longitude
        }
    }

    suspend fun updateThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode
        }
    }

    suspend fun updateFilterRadiusKm(radiusKm: Double) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FILTER_RADIUS_KM] = radiusKm
        }
    }

    suspend fun updateFilterCategories(categories: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FILTER_CATEGORIES] = categories.joinToString(",")
        }
    }

    suspend fun updateShowFreeOnly(showFreeOnly: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_FREE_ONLY] = showFreeOnly
        }
    }

    suspend fun updateAuthToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token != null) {
                prefs[Keys.AUTH_TOKEN] = token
            } else {
                prefs.remove(Keys.AUTH_TOKEN)
            }
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
