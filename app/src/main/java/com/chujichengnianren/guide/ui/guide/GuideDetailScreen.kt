package com.chujichengnianren.guide.ui.guide

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
import com.chujichengnianren.guide.ui.components.ScreenHeader
import com.chujichengnianren.guide.ui.theme.EmergencyOrange

private val scenarioTitles = mapOf(
    "sick" to "生病了，接下来怎么办？",
    "failed-course" to "挂科以后怎么办？",
    "scammed" to "怀疑被诈骗了怎么办？",
    "lost-item" to "重要物品丢了怎么办？",
    "renting" to "第一次租房怎么准备？",
    "internship" to "第一次找实习怎么开始？",
    "resign" to "想辞职要先准备什么？",
    "unpaid-wages" to "遇到欠薪怎么办？",
    "bank-card" to "银行卡出问题怎么办？",
    "unsure" to "不知道该从哪里开始？",
)

@Composable
fun GuideDetailScreen(
    guideId: String,
    onBackClick: () -> Unit,
) {
    val title = scenarioTitles[guideId] ?: "手册内容预览"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ScreenHeader(
                title = title,
                eyebrow = "内容骨架 · 暂未发布",
                onBackClick = onBackClick,
            )
        }
        item {
            VerificationNotice()
        }
        item {
            GuideSection(
                index = "01",
                title = "30 秒告诉我怎么办",
                body = "这篇手册正在核验适用于中国大陆的官方来源。核验完成前，不提供可能误导你的具体步骤。",
            )
        }
        item {
            GuideSection(
                index = "02",
                title = "现在先做这些",
                body = "这里会按优先级列出立即行动，并明确哪些步骤受省份、城市或学校规则影响。",
            )
        }
        item {
            GuideSection(
                index = "03",
                title = "需要准备什么",
                body = "需要材料时，这里会提供可在本机勾选的清单。",
            )
        }
        item {
            GuideSection(
                index = "04",
                title = "不要这样做",
                body = "高风险误区会单独显示，不和普通提示混在一起。",
                accent = true,
            )
        }
        item {
            GuideSection(
                index = "05",
                title = "官方依据",
                body = "每条会标注来源、适用地区和最后核验日期。",
            )
        }
    }
}

@Composable
private fun VerificationNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "核验中",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "此信息尚未完成官方来源核验。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
private fun GuideSection(
    index: String,
    title: String,
    body: String,
    accent: Boolean = false,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (accent) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (accent) EmergencyOrange.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = index,
                style = MaterialTheme.typography.labelMedium,
                color = if (accent) EmergencyOrange else MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

