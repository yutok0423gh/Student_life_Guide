package com.chujichengnianren.guide.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chujichengnianren.guide.ui.components.IndexCard
import com.chujichengnianren.guide.ui.components.SectionHeading
import com.chujichengnianren.guide.ui.theme.ActionBlue
import com.chujichengnianren.guide.ui.theme.EmergencyOrange
import com.chujichengnianren.guide.ui.theme.GoldTab
import com.chujichengnianren.guide.ui.theme.TealTab

private data class SearchPrompt(
    val id: String,
    val title: String,
    val hint: String,
    val color: androidx.compose.ui.graphics.Color,
)

private val prompts = listOf(
    SearchPrompt("rent-contract", "第一次签租房合同", "可以搜：租房合同", ActionBlue),
    SearchPrompt("failed-course", "挂科以后怎么办", "可以搜：挂科 补考", GoldTab),
    SearchPrompt("scam", "怀疑自己被骗了", "可以搜：诈骗 止损", EmergencyOrange),
    SearchPrompt("internship", "第一次找实习", "可以搜：实习 协议", TealTab),
)

@Composable
fun SearchScreen(
    onGuideClick: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "搜索",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "不必先知道专业名词，直接描述你遇到的事。",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("你想解决什么？") },
                placeholder = { Text("例如：租房押金不退") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                    )
                },
                shape = MaterialTheme.shapes.medium,
            )
        }

        if (query.isBlank()) {
            item { SectionHeading(title = "试试这样搜", caption = "场景优先") }
            items(prompts.size, key = { prompts[it].id }) { index ->
                val prompt = prompts[index]
                IndexCard(
                    eyebrow = (index + 1).toString().padStart(2, '0'),
                    title = prompt.title,
                    supportingText = prompt.hint,
                    tabColor = prompt.color,
                    onClick = { query = prompt.hint.substringAfter("可以搜：") },
                )
            }
        } else {
            item {
                SectionHeading(title = "搜索预览", caption = "内容核验中")
            }
            item {
                IndexCard(
                    eyebrow = "未发布",
                    title = "与“$query”相关的手册",
                    supportingText = "打开阅读结构；具体内容待官方来源核验",
                    tabColor = ActionBlue,
                    onClick = { onGuideClick("search-preview") },
                )
            }
        }
    }
}
