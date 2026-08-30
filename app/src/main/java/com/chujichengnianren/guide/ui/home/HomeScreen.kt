package com.chujichengnianren.guide.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chujichengnianren.guide.ui.components.EmptyState
import com.chujichengnianren.guide.ui.components.IndexCard
import com.chujichengnianren.guide.ui.components.SectionHeading
import com.chujichengnianren.guide.ui.theme.ActionBlue
import com.chujichengnianren.guide.ui.theme.EmergencyOrange
import com.chujichengnianren.guide.ui.theme.GoldTab
import com.chujichengnianren.guide.ui.theme.TealTab
import com.chujichengnianren.guide.ui.theme.VioletTab

private data class HomeScenario(
    val id: String,
    val title: String,
    val hint: String,
    val tabColor: Color,
)

private data class HomeCategory(
    val id: String,
    val name: String,
    val hint: String,
    val tabColor: Color,
)

private val scenarios = listOf(
    HomeScenario("sick", "生病了", "挂号、急诊与医保", EmergencyOrange),
    HomeScenario("failed-course", "挂科了", "补考、重修与学籍", GoldTab),
    HomeScenario("scammed", "被诈骗了", "止损、留证与求助", EmergencyOrange),
    HomeScenario("lost-item", "东西丢了", "先处理最要紧的证件", TealTab),
    HomeScenario("renting", "想租房", "看房、合同与押金", ActionBlue),
    HomeScenario("internship", "想找实习", "渠道、简历与协议", VioletTab),
    HomeScenario("resign", "想辞职", "交接、证明与社保", GoldTab),
    HomeScenario("unpaid-wages", "遇到欠薪", "留证并寻找正规渠道", EmergencyOrange),
    HomeScenario("bank-card", "银行卡出问题", "冻结、挂失与补办", TealTab),
    HomeScenario("unsure", "不知道怎么办", "从问题类型开始判断", ActionBlue),
)

private val categories = listOf(
    HomeCategory("education", "学校与学籍", "报到、选课、挂科、毕业", GoldTab),
    HomeCategory("housing", "住宿与租房", "宿舍、看房、合同、退租", ActionBlue),
    HomeCategory("health", "医疗与保险", "挂号、医保、急诊、用药", EmergencyOrange),
    HomeCategory("finance", "钱与银行", "银行卡、预算、借贷、缴费", TealTab),
    HomeCategory("career", "实习与工作", "求职、协议、薪资、离职", VioletTab),
    HomeCategory("documents", "证件与办事", "身份证、护照、档案、户籍", ActionBlue),
    HomeCategory("safety", "安全与维权", "反诈、留证、投诉、求助", EmergencyOrange),
    HomeCategory("life", "独立生活", "做饭、快递、搬家、维修", TealTab),
)

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onEmergencyClick: () -> Unit,
    onScenarioClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onRegionClick: () -> Unit,
    onStageClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "初级成年人入门手册",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "你好，先把问题拆小。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "你现在遇到了\n什么？",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        item {
            RegionSelector(
                onClick = onRegionClick,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item {
            SearchEntry(
                onClick = onSearchClick,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item {
            EmergencyEntry(
                onClick = onEmergencyClick,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeading(
                    title = "我现在……",
                    caption = "先选最接近的一项",
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                ScenarioGrid(
                    scenarios = scenarios,
                    onScenarioClick = onScenarioClick,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                SectionHeading(title = "根据你的阶段")
                IndexCard(
                    eyebrow = "个性排序",
                    title = "你目前处在哪个阶段？",
                    supportingText = "设置后只调整内容顺序，不需要登录",
                    tabColor = ActionBlue,
                    onClick = onStageClick,
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeading(
                    title = "八大分类",
                    caption = "按事情找",
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(categories, key = { it.id }) { category ->
                        IndexCard(
                            eyebrow = category.id,
                            title = category.name,
                            supportingText = category.hint,
                            tabColor = category.tabColor,
                            onClick = { onCategoryClick(category.id) },
                            modifier = Modifier.width(222.dp),
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SectionHeading(title = "最近阅读", caption = "只保存在本机")
                EmptyState(
                    marker = "00",
                    title = "还没有阅读记录",
                    body = "看过的手册会留在这里。",
                )
            }
        }
    }
}

@Composable
private fun RegionSelector(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "地区",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "暂未选择 · 先看全国通用内容",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            Text(
                text = "设置  ›",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SearchEntry(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 17.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "搜索：挂科、租房、被诈骗、实习……",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmergencyEntry(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
        border = BorderStroke(1.dp, EmergencyOrange.copy(alpha = 0.45f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "遇到紧急情况？",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    text = "先保人身安全，再按步骤处理",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.78f),
                )
            }
            Text(
                text = "打开  ›",
                style = MaterialTheme.typography.labelLarge,
                color = EmergencyOrange,
            )
        }
    }
}

@Composable
private fun ScenarioGrid(
    scenarios: List<HomeScenario>,
    onScenarioClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        scenarios.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEachIndexed { itemIndex, scenario ->
                    IndexCard(
                        eyebrow = (rowIndex * 2 + itemIndex + 1).toString().padStart(2, '0'),
                        title = scenario.title,
                        supportingText = scenario.hint,
                        tabColor = scenario.tabColor,
                        onClick = { onScenarioClick(scenario.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
