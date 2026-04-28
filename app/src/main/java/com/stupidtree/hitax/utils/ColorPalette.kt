package com.stupidtree.hitax.utils

import com.stupidtree.hitax.R

object ColorPalette {
    data class Palette(
        val name: String,
        val colors: List<String>,
        val displayNameRes: Int
    )

    val MATERIAL = Palette(
        "material", listOf(
            "#ec407a", "#FF9E00", "#7c4dff",
            "#536dfe", "#2196f3", "#26c6da",
            "#009688", "#7cba59", "#E96D71"
        ), R.string.palette_material
    )

    val PASTEL = Palette(
        "pastel", listOf(
            "#F8BBD0", "#FFCCBC", "#E1BEE7",
            "#C5CAE9", "#BBDEFB", "#B2EBF2",
            "#B2DFDB", "#DCEDC8", "#FFF9C4"
        ), R.string.palette_pastel
    )

    val MACARON = Palette(
        "macaron", listOf(
            "#FF9AA2", "#FFB7B2", "#B5EAD7",
            "#C7CEEA", "#FFDAC1", "#E2F0CB",
            "#A0E7E5", "#FFD1DC", "#DAEAF0"
        ), R.string.palette_macaron
    )

    val NATURE = Palette(
        "nature", listOf(
            "#A8E6CF", "#DCEDC1", "#FFD3B6",
            "#FFAAA5", "#FF8B94", "#B5EAD7",
            "#C7CEEA", "#E2F0CB", "#97D2C1"
        ), R.string.palette_nature
    )

    val OCEAN = Palette(
        "ocean", listOf(
            "#004D7A", "#008793", "#00BFA5",
            "#26A69A", "#4DB6AC", "#80CBC4",
            "#B2DFDB", "#00695C", "#00796B"
        ), R.string.palette_ocean
    )

    val SUNSET = Palette(
        "sunset", listOf(
            "#FF6F61", "#FF8A65", "#FFAB91",
            "#FFCCBC", "#E91E63", "#F06292",
            "#F48FB1", "#FFB74D", "#FF8A80"
        ), R.string.palette_sunset
    )

    val all = listOf(MATERIAL, PASTEL, MACARON, NATURE, OCEAN, SUNSET)

    fun fromId(id: String): Palette = all.find { it.name == id } ?: MATERIAL
}
