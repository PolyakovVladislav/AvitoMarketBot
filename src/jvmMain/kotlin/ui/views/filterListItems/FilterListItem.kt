@file:OptIn(ExperimentalUnitApi::class)

package ui.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import ui.style.icClose

@Preview
@Composable
fun ListItem(
    index: Int,
    text: String,
    onClick: (Int) -> Unit
)
{
    Row(
        modifier = Modifier.clickable(onClick = { onClick(index) }).padding(8.dp)
    )
    {
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
            fontSize = TextUnit(24f, TextUnitType.Sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            icClose(),
            contentDescription = null,
            modifier = Modifier.size(10.dp).align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(8.dp))
    }
}