package com.chujichengnianren.guide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.chujichengnianren.guide.data.content.BundledContentInitializer
import com.chujichengnianren.guide.ui.GuideApp
import com.chujichengnianren.guide.ui.theme.AdultStarterGuideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var bundledContentInitializer: BundledContentInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            bundledContentInitializer.initialize()
        }
        setContent {
            AdultStarterGuideTheme {
                GuideApp()
            }
        }
    }
}
