package com.chujichengnianren.guide.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chujichengnianren.guide.ui.components.ScreenHeader

@Composable
fun RegionPickerScreen(
    onBackClick: () -> Unit,
) {
    var province by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ScreenHeader(
                title = "设置所在地区",
                eyebrow = "仅用于匹配适用范围",
                onBackClick = onBackClick,
            )
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    text = "不会申请定位权限。你也可以稍后设置，未选择时只展示全国通用内容。",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        item {
            OutlinedTextField(
                value = province,
                onValueChange = { province = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("省级地区") },
                placeholder = { Text("例如：广东省") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )
        }
        item {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("城市") },
                placeholder = { Text("例如：深圳市") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = province.isNotBlank(),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text("完成")
                }
                Surface(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    color = androidx.compose.ui.graphics.Color.Transparent,
                ) {
                    Text(
                        text = "稍后设置",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = "当前为界面骨架；持久化将在本地设置层接入。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

