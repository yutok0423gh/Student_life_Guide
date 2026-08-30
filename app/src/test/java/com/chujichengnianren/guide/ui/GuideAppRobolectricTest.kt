package com.chujichengnianren.guide.ui

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.chujichengnianren.guide.ui.theme.AdultStarterGuideTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = Application::class)
@LooperMode(LooperMode.Mode.PAUSED)
class GuideAppRobolectricTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun appRendersAndAllFourBottomDestinationsSwitch() {
        composeRule.setContent {
            AdultStarterGuideTheme(darkTheme = false) {
                GuideApp()
            }
        }

        composeRule.onNodeWithText("你现在遇到了\n什么？").assertIsDisplayed()

        composeRule.onNodeWithText("搜索").performClick()
        composeRule.onNodeWithText("不必先知道专业名词，直接描述你遇到的事。")
            .assertIsDisplayed()

        composeRule.onNodeWithText("收藏").performClick()
        composeRule.onNodeWithText("需要反复查看的步骤，放在手边。")
            .assertIsDisplayed()

        composeRule.onNodeWithText("我的").performClick()
        composeRule.onNodeWithText("只设置真正影响内容的选项。")
            .assertIsDisplayed()

        composeRule.onNodeWithText("首页").performClick()
        composeRule.onNodeWithText("你现在遇到了\n什么？").assertIsDisplayed()
    }
}
