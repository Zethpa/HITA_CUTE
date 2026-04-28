package com.stupidtree.hitax.utils

import com.stupidtree.hitax.R

data class ThemeColorOption(
    val id: String,
    val displayNameRes: Int,
    val primaryColorHex: String
)

object ThemeColors {
    // 20 muted, well-distributed preset colors
    val PRESETS = listOf(
        ThemeColorOption("blue",       R.string.theme_blue,       "#304ffe"),
        ThemeColorOption("steel",      R.string.theme_steel,      "#5C7FA0"),
        ThemeColorOption("sky",        R.string.theme_sky,        "#5A9BB8"),
        ThemeColorOption("teal",       R.string.theme_teal,       "#4D9088"),
        ThemeColorOption("mint",       R.string.theme_mint,       "#5E9B8C"),
        ThemeColorOption("sage",       R.string.theme_sage,       "#6E9B72"),
        ThemeColorOption("olive",      R.string.theme_olive,      "#8E9A5E"),
        ThemeColorOption("amber",      R.string.theme_amber,      "#B88A50"),
        ThemeColorOption("terracotta", R.string.theme_terracotta, "#C07850"),
        ThemeColorOption("coral",      R.string.theme_coral,      "#C07060"),
        ThemeColorOption("rose",       R.string.theme_rose,       "#B06070"),
        ThemeColorOption("mauve",      R.string.theme_mauve,      "#9B6888"),
        ThemeColorOption("plum",       R.string.theme_plum,       "#806890"),
        ThemeColorOption("violet",     R.string.theme_violet,     "#7068A0"),
        ThemeColorOption("indigo",     R.string.theme_indigo,     "#5C6098"),
        ThemeColorOption("slate",      R.string.theme_slate,      "#687890"),
        ThemeColorOption("taupe",      R.string.theme_taupe,      "#887870"),
        ThemeColorOption("wine",       R.string.theme_wine,       "#883850"),
        ThemeColorOption("bronze",     R.string.theme_bronze,     "#907050"),
        ThemeColorOption("charcoal",   R.string.theme_charcoal,   "#586068"),
    )

    val all = PRESETS

    fun fromId(id: String): ThemeColorOption = all.find { it.id == id } ?: PRESETS[0]

    fun findClosest(hex: String): ThemeColorOption {
        val c = android.graphics.Color.parseColor(hex)
        val cr = (c shr 16) and 0xFF
        val cg = (c shr 8) and 0xFF
        val cb = c and 0xFF
        return PRESETS.minByOrNull { opt ->
            val tc = android.graphics.Color.parseColor(opt.primaryColorHex)
            val tr = (tc shr 16) and 0xFF
            val tg = (tc shr 8) and 0xFF
            val tb = tc and 0xFF
            2 * (cr - tr) * (cr - tr) + (cg - tg) * (cg - tg) + 2 * (cb - tb) * (cb - tb)
        } ?: PRESETS[0]
    }

    fun resolveStyleRes(themeId: String): Int {
        return when (themeId) {
            "steel"      -> R.style.AppTheme_Steel
            "sky"        -> R.style.AppTheme_Sky
            "teal"       -> R.style.AppTheme_Teal
            "mint"       -> R.style.AppTheme_Mint
            "sage"       -> R.style.AppTheme_Sage
            "olive"      -> R.style.AppTheme_Olive
            "amber"      -> R.style.AppTheme_Amber
            "terracotta" -> R.style.AppTheme_Terracotta
            "coral"      -> R.style.AppTheme_Coral
            "rose"       -> R.style.AppTheme_Rose
            "mauve"      -> R.style.AppTheme_Mauve
            "plum"       -> R.style.AppTheme_Plum
            "violet"     -> R.style.AppTheme_Violet
            "indigo"     -> R.style.AppTheme_Indigo
            "slate"      -> R.style.AppTheme_Slate
            "taupe"      -> R.style.AppTheme_Taupe
            "wine"       -> R.style.AppTheme_Wine
            "bronze"     -> R.style.AppTheme_Bronze
            "charcoal"   -> R.style.AppTheme_Charcoal
            else         -> R.style.AppTheme
        }
    }
}
