package com.stupidtree.hitax.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ColorPaletteRepository(application: Application) {
    private val sp: SharedPreferences =
        application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun getActivePaletteId(): String =
        sp.getString(KEY_ACTIVE_PALETTE, "material") ?: "material"

    fun setActivePaletteId(id: String) {
        sp.edit().putString(KEY_ACTIVE_PALETTE, id).apply()
    }

    fun getCustomColor(subjectId: String): Int? {
        val json = sp.getString(KEY_CUSTOM_COLORS, "{}") ?: "{}"
        val map: Map<String, Double> = try {
            Gson().fromJson(json, object : TypeToken<Map<String, Double>>() {}.type)
        } catch (_: Exception) {
            emptyMap()
        }
        return map[subjectId]?.toInt()
    }

    fun setCustomColor(subjectId: String, color: Int) {
        val json = sp.getString(KEY_CUSTOM_COLORS, "{}") ?: "{}"
        val map: MutableMap<String, Double> = try {
            Gson().fromJson(json, object : TypeToken<MutableMap<String, Double>>() {}.type)
        } catch (_: Exception) {
            mutableMapOf()
        }
        map[subjectId] = color.toDouble()
        sp.edit().putString(KEY_CUSTOM_COLORS, Gson().toJson(map)).apply()
    }

    fun clearCustomColor(subjectId: String) {
        val json = sp.getString(KEY_CUSTOM_COLORS, "{}") ?: "{}"
        val map: MutableMap<String, Double> = try {
            Gson().fromJson(json, object : TypeToken<MutableMap<String, Double>>() {}.type)
        } catch (_: Exception) {
            mutableMapOf()
        }
        map.remove(subjectId)
        sp.edit().putString(KEY_CUSTOM_COLORS, Gson().toJson(map)).apply()
    }

    companion object {
        const val SP_NAME = "color_palette"
        const val KEY_ACTIVE_PALETTE = "active_palette"
        const val KEY_CUSTOM_COLORS = "custom_colors"

        private var instance: ColorPaletteRepository? = null
        fun getInstance(application: Application): ColorPaletteRepository {
            if (instance == null) instance = ColorPaletteRepository(application)
            return instance!!
        }
    }
}
