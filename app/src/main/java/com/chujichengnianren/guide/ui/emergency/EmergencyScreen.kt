package com.chujichengnianren.guide.ui.emergency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chujichengnianren.guide.ui.components.IndexCard
import com.chujichengnianren.guide.ui.components.ScreenHeader
import com.chujichengnianren.guide.ui.theme.EmergencyOrange

private val emergencyScenarios = listOf(
    "被诈骗",
    "银行卡被盗",
    "手机丢失",
    "身份证丢失",
    "发生交通事故",
    "有人受伤",
    "遇到暴力",
    "被偷拍或骚扰",
)

@Composable
fun EmergencyScreen(
    onBackClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScreenHeader(
                title = "紧急情况",
                eyebrow = "先处理最重要的事",
                onBackClick = onBackClick,
            )
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer,
                border = BorderStroke(1.dp, EmergencyOrange.copy(alpha = 0.5f)),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = MaterialTheme.shapes.small,
                        color = EmergencyOrange,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                            )
                        }
                    }
                    Text(
                        text = "如果人身安全正受到威胁，先离开危险环境，并尽快向可信任的人或当地紧急服务求助。",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        }
        item {
            Text(
                text = "选择最接近的情况",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        itemsIndexed(emergencyScenarios, key = { _, item -> item }) { index, scenario ->
            IndexCard(
                eyebrow = (index + 1).toString().padStart(2, '0'),
                title = scenario,
                supportingText = "具体步骤待官方来源核验",
                tabColor = EmergencyOrange,
                onClick = {},
            )
        }
        item {
            Text(
                text = "当前页面不替代警方、医疗机构或其他紧急服务。具体电话与处置步骤会在官方核验后开放。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 10.dp),
            )
        }
    }
}

