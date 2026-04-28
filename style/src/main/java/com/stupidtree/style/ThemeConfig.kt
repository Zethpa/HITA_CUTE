package com.stupidtree.style

/**
 * Bridge for :app module to pass compile-time theme style resource IDs
 * to BaseActivity (in :style module), which cannot reference :app's R directly.
 */
object ThemeConfig {
    @JvmStatic
    var themeStyleRes: Int = 0

    @JvmStatic
    var composePrimaryColor: Int = 0
}
