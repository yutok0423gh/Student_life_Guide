package com.chujichengnianren.guide.ui.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chujichengnianren.guide.ui.components.IndexCard
import com.chujichengnianren.guide.ui.components.ScreenHeader
import com.chujichengnianren.guide.ui.theme.ActionBlue
import com.chujichengnianren.guide.ui.theme.EmergencyOrange
import com.chujichengnianren.guide.ui.theme.GoldTab
import com.chujichengnianren.guide.ui.theme.TealTab
import com.chujichengnianren.guide.ui.theme.VioletTab

private data class CategoryPreview(
    val name: String,
    val description: String,
    val topics: List<String>,
    val color: Color,
)

private val categoryPreviews = mapOf(
    "education" to CategoryPreview(
        "学校与学籍",
        "报到、选课、挂科与毕业",
        listOf("大学报到", "第一次挂科", "补考与重修", "毕业材料"),
        GoldTab,
    ),
    "housing" to CategoryPreview(
        "住宿与租房",
        "宿舍、看房、合同与退租",
        listOf("第一次住宿舍", "第一次看房", "签租房合同", "押金与退租"),
        ActionBlue,
    ),
    "health" to CategoryPreview(
        "医疗与保险",
        "挂号、医保、急诊与用药",
        listOf("第一次看病", "怎么挂号", "医保怎么用", "什么情况去急诊"),
        EmergencyOrange,
    ),
    "finance" to CategoryPreview(
        "钱与银行",
        "银行卡、预算、借贷与缴费",
        listOf("第一次办银行卡", "银行卡挂失", "做一份月度预算", "识别高风险借贷"),
        TealTab,
    ),
    "career" to CategoryPreview(
        "实习与工作",
        "求职、协议、薪资与离职",
        listOf("第一次找实习", "看懂实习协议", "入职前准备", "离职与交接"),
        VioletTab,
    ),
    "documents" to CategoryPreview(
        "证件与办事",
        "身份证、护照、档案与户籍",
        listOf("身份证丢失", "第一次办护照", "毕业档案去向", "常用证件备份"),
        ActionBlue,
    ),
    "safety" to CategoryPreview(
        "安全与维权",
        "反诈、留证、投诉与求助",
        listOf("怀疑遭遇诈骗", "保留有效证据", "银行卡被盗用", "遇到骚扰"),
        EmergencyOrange,
    ),
    "life" to CategoryPreview(
        "独立生活",
        "做饭、快递、搬家与维修",
        listOf("洗衣标签怎么看", "厨房基础安全", "第一次搬家", "快递丢失"),
        TealTab,
    ),
)

@Composable
fun CategoryScreen(
    categoryId: String,
    onBackClick: () -> Unit,
    onGuideClick: (String) -> Unit,
) {
    val category = categoryPreviews[categoryId] ?: CategoryPreview(
        name = "内容分类",
        description = "这个分类暂时不可用",
        topics = emptyList(),
        color = MaterialTheme.colorScheme.primary,
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScreenHeader(
                title = category.name,
                eyebrow = category.description,
                onBackClick = onBackClick,
            )
        }
        itemsIndexed(category.topics, key = { index, _ -> "$categoryId-$index" }) { index, topic ->
            IndexCard(
                eyebrow = (index + 1).toString().padStart(2, '0'),
                title = topic,
                supportingText = "内容待中国大陆官方来源核验",
                tabColor = category.color,
                onClick = { onGuideClick("$categoryId-$index") },
            )
        }
    }
}

