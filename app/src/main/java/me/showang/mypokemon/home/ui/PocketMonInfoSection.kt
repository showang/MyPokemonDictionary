package me.showang.mypokemon.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun PocketMonInfoSection(
    modifier: Modifier = Modifier,
    titleText: String,
    sizeText: String,
    itemBuilder: LazyListScope.() -> Unit,
    lazyRowTestTag: String? = null
) {
    Column(modifier = modifier) {
        SectionTitle(titleText = titleText, itemCountText = sizeText)
        LazyRow(
            modifier = Modifier.testTag(lazyRowTestTag ?: ""),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = itemBuilder
        )
    }
}