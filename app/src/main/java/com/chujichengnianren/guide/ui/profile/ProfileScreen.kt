package com.chujichengnianren.guide.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chujichengnianren.guide.ui.components.SectionHeading

@Composable
fun ProfileScreen(
    onRegionClick: () -> Unit,
    onStageClick: () -> Unit,
    onFavoritesClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "我的",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "只设置真正影响内容的选项。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item { SectionHeading(title = "本机设置") }
        item {
            SettingsGroup {
                ProfileRow("所在地区", "暂未选择", onRegionClick)
                ProfileRow("目前阶段", "暂未选择", onStageClick)
            }
        }

        item { SectionHeading(title = "你的内容") }
        item {
            SettingsGroup {
                ProfileRow("收藏", "0 篇", onFavoritesClick)
                ProfileRow("最近阅读", "暂无记录", onClick = {})
            }
        }

        item { SectionHeading(title = "关于这本手册") }
        item {
            SettingsGroup {
                ProfileRow("内容更新", "随安装包提供", onClick = {})
                ProfileRow("资料来源", "优先采用官方来源", onClick = {})
                ProfileRow("隐私政策", "不登录 · 不追踪", onClick = {})
                ProfileRow("关于我们", "版本 0.1.0", onClick = {})
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(content = { content() })
    }
}

@Composable
private fun ProfileRow(
    title: String,
    value: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 17.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "$value  ›",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

