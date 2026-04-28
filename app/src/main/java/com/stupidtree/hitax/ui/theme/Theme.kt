package com.stupidtree.hitax.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

var composePrimaryColor: Int? = null

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val primaryColor = composePrimaryColor?.let { Color(it.toInt()) } ?: Color(0x304ffe)

    val colors = if (darkTheme) {
        darkColors(
            primary = primaryColor,
            primaryVariant = primaryColor,
            secondary = primaryColor,
            onPrimary = Color(0xFF000000),
            onSecondary = Color(0xFF808080)
        )
    } else {
        lightColors(
            primary = primaryColor,
            primaryVariant = primaryColor,
            secondary = primaryColor,
            onPrimary = Color(0xFFffffff),
            onSecondary = Color(0xFF4f4f4f)
        )
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}

@Composable
fun AppBar(title: Int) {
    @Suppress("DEPRECATION")
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        },
        title = {
            Text(text = stringResource(title))
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    )
}
