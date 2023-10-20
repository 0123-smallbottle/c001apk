package com.example.c001apk.ui.activity

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import com.example.c001apk.util.ActivityCollector
import com.example.c001apk.util.ThemeUtils
import com.google.android.material.color.DynamicColors
import rikka.material.app.MaterialActivity

abstract class BaseActivity : MaterialActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun computeUserThemeKey() = ThemeUtils.colorTheme + ThemeUtils.getNightThemeStyleRes(this)

    override fun onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars()
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    override fun onApplyUserThemeResource(theme: Resources.Theme, isDecorView: Boolean) {
        if (!ThemeUtils.isSystemAccent) theme.applyStyle(ThemeUtils.colorThemeStyleRes, true)
        theme.applyStyle(ThemeUtils.getNightThemeStyleRes(this), true) //blackDarkMode
    }
}
