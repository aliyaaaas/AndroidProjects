package com.example.myandroidapp.inception25.navScreens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.navigation.PlantSortOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantSortBottomSheet(
    currentSortType: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.plants_sort_header),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(getSortOptions(context)) { sortOption ->
                    SortOptionItem(
                        sortOption = sortOption,
                        currentSortType = currentSortType,
                        onClick = {
                            onSortSelected(sortOption.id)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(stringResource(R.string.plants_sort_close))
            }
        }
    }
}

data class SortOption(
    val id: String,
    val title: String,
    val iconResId: Int
)

@Composable
private fun SortOptionIcon(iconResId: Int) {
    when (iconResId) {
        1 -> Icon(Icons.Default.SortByAlpha, contentDescription = null)
        2 -> Icon(Icons.Default.WaterDrop, contentDescription = null)
        3 -> Icon(Icons.Default.Star, contentDescription = null)
        4 -> Icon(Icons.Default.Category, contentDescription = null)
        5 -> Icon(Icons.Default.DateRange, contentDescription = null)
        else -> Icon(Icons.Default.Sort, contentDescription = null)
    }
}

private fun getSortOptions(context: android.content.Context): List<SortOption> {
    return listOf(
        SortOption(
            id = PlantSortOptions.NAME_ASC,
            title = context.getString(R.string.plants_sort_name_asc),
            iconResId = 1
        ),
        SortOption(
            id = PlantSortOptions.NAME_DESC,
            title = context.getString(R.string.plants_sort_name_desc),
            iconResId = 1
        ),
        SortOption(
            id = PlantSortOptions.NEXT_WATERING,
            title = context.getString(R.string.plants_sort_watering),
            iconResId = 2
        ),
        SortOption(
            id = PlantSortOptions.DIFFICULTY_ASC,
            title = context.getString(R.string.plants_sort_difficulty_asc),
            iconResId = 3
        ),
        SortOption(
            id = PlantSortOptions.DIFFICULTY_DESC,
            title = context.getString(R.string.plants_sort_difficulty_desc),
            iconResId = 3
        ),
        SortOption(
            id = PlantSortOptions.TYPE,
            title = context.getString(R.string.plants_sort_type),
            iconResId = 4
        ),
        SortOption(
            id = PlantSortOptions.DATE_ADDED,
            title = context.getString(R.string.plants_sort_date),
            iconResId = 5
        )
    )
}

@Composable
private fun SortOptionItem(
    sortOption: SortOption,
    currentSortType: String,
    onClick: () -> Unit
) {
    val isSelected = sortOption.id == currentSortType

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                SortOptionIcon(sortOption.iconResId)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = sortOption.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}