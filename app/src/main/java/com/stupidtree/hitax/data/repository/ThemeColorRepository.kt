package com.stupidtree.hitax.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class ThemeColorRepository(application: Application) {
    private val sp: SharedPreferences =
        application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun getActiveThemeId(): String =
        sp.getString(KEY_ACTIVE_THEME, "blue") ?: "blue"

    fun setActiveThemeId(id: String) {
        sp.edit().putString(KEY_ACTIVE_THEME, id).apply()
    }

    fun isAutoFromBg(): Boolean =
        sp.getBoolean(KEY_AUTO_FROM_BG, false)

    fun setAutoFromBg(enabled: Boolean) {
        sp.edit().putBoolean(KEY_AUTO_FROM_BG, enabled).apply()
    }

    companion object {
        const val SP_NAME = "theme_colors"
        const val KEY_ACTIVE_THEME = "active_theme_id"
        const val KEY_AUTO_FROM_BG = "auto_from_bg"

        private var instance: ThemeColorRepository? = null
        fun getInstance(application: Application): ThemeColorRepository {
            if (instance == null) instance = ThemeColorRepository(application)
            return instance!!
        }
    }
}
