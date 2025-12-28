package com.example.myandroidapp.inception25.navScreens.main

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.Constants
import com.example.myandroidapp.inception25.data.UserDataRepository
import com.example.myandroidapp.inception25.di.ServiceLocator
import com.example.myandroidapp.inception25.model.PlantModel
import com.example.myandroidapp.inception25.navigation.*
import com.example.myandroidapp.inception25.utils.ImageUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsListScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val userId = UserDataRepository.getCurrentUserId()

    LaunchedEffect(Unit) {
        if (userId == null) {
            navController.navigate(LoginScreen)
        }
    }

    if (userId == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val plantRepository = ServiceLocator.getPlantRepository()
    val scope = rememberCoroutineScope()

    var currentSortType by remember {
        mutableStateOf(UserDataRepository.getPlantSortType())
    }

    var plants by remember { mutableStateOf(emptyList<PlantModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    fun loadPlants() {
        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                plants = when (currentSortType) {
                    PlantSortOptions.NAME_ASC -> plantRepository.getPlantsSortedByName(userId)
                    PlantSortOptions.NAME_DESC -> plantRepository.getPlantsSortedByNameDesc(userId)
                    PlantSortOptions.NEXT_WATERING -> plantRepository.getPlantsSortedByNextWatering(userId)
                    PlantSortOptions.DIFFICULTY_ASC -> plantRepository.getPlantsSortedByDifficultyAsc(userId)
                    PlantSortOptions.DIFFICULTY_DESC -> plantRepository.getPlantsSortedByDifficultyDesc(userId)
                    PlantSortOptions.TYPE -> plantRepository.getPlantsSortedByType(userId)
                    PlantSortOptions.DATE_ADDED -> plantRepository.getPlantsSortedByDateAdded(userId)
                    else -> plantRepository.getPlantsSortedByName(userId)
                }
            } catch (e: Exception) {
                errorMessage = context.getString(R.string.common_error) + ": " + e.message
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadPlants()
    }

    LaunchedEffect(currentSortType) {
        if (plants.isNotEmpty()) {
            loadPlants()
        }
    }

    fun refreshPlants() {
        scope.launch {
            try {
                plants = when (currentSortType) {
                    PlantSortOptions.NAME_ASC -> plantRepository.getPlantsSortedByName(userId)
                    PlantSortOptions.NAME_DESC -> plantRepository.getPlantsSortedByNameDesc(userId)
                    PlantSortOptions.NEXT_WATERING -> plantRepository.getPlantsSortedByNextWatering(userId)
                    PlantSortOptions.DIFFICULTY_ASC -> plantRepository.getPlantsSortedByDifficultyAsc(userId)
                    PlantSortOptions.DIFFICULTY_DESC -> plantRepository.getPlantsSortedByDifficultyDesc(userId)
                    PlantSortOptions.TYPE -> plantRepository.getPlantsSortedByType(userId)
                    PlantSortOptions.DATE_ADDED -> plantRepository.getPlantsSortedByDateAdded(userId)
                    else -> plantRepository.getPlantsSortedByName(userId)
                }
            } catch (e: Exception) {
                errorMessage = context.getString(R.string.common_error) + ": " + e.message
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.plants_title))
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showBottomSheet = true }
                    ) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = stringResource(R.string.plants_sort),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            navController.navigate(ProfileScreen)
                        }
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile_icon),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(AddPlantScreen)
                },
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.plants_add_button)
                    )
                },
                text = { Text(stringResource(R.string.plants_add_button)) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.common_loading))
                }
            }
            else if (errorMessage != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.common_warning_emoji),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { loadPlants() }
                    ) {
                        Text(stringResource(R.string.common_retry))
                    }
                }
            }
            else if (plants.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.plants_leaf_emoji),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.plants_empty_title),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.plants_empty_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = context.getString(R.string.plants_count, plants.size),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = context.getString(
                                    R.string.plants_sort_current,
                                    getSortTypeText(currentSortType, context)
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    items(plants) { plant ->
                        PlantCard(
                            plant = plant,
                            navController = navController,
                            onWaterClick = {
                                scope.launch {
                                    try {
                                        plantRepository.markPlantAsWatered(plant.id)
                                        refreshPlants()
                                    } catch (e: Exception) {
                                        errorMessage = context.getString(R.string.common_error) + ": " + e.message
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showBottomSheet) {
            PlantSortBottomSheet(
                currentSortType = currentSortType,
                onSortSelected = { sortType ->
                    currentSortType = sortType
                    UserDataRepository.savePlantSortType(sortType)
                    showBottomSheet = false
                },
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}

private fun getSortTypeText(sortType: String, context: android.content.Context): String =
    when (sortType) {
        PlantSortOptions.NAME_ASC -> context.getString(R.string.plants_sort_name_asc)
        PlantSortOptions.NAME_DESC -> context.getString(R.string.plants_sort_name_desc)
        PlantSortOptions.NEXT_WATERING -> context.getString(R.string.plants_sort_watering)
        PlantSortOptions.DIFFICULTY_ASC -> context.getString(R.string.plants_sort_difficulty_asc)
        PlantSortOptions.DIFFICULTY_DESC -> context.getString(R.string.plants_sort_difficulty_desc)
        PlantSortOptions.TYPE -> context.getString(R.string.plants_sort_type)
        PlantSortOptions.DATE_ADDED -> context.getString(R.string.plants_sort_date)
        else -> context.getString(R.string.plants_sort_name_asc)
    }

@Composable
fun PlantCard(
    plant: PlantModel,
    navController: NavController,
    onWaterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                PlantPhoto(plant = plant)

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = plant.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = plant.type,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = stringResource(R.string.plants_water_needed),
                            modifier = Modifier.size(18.dp),
                            tint = if (plant.needsWatering) Color.Red else Color.Green
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = if (plant.needsWatering) {
                                stringResource(R.string.plants_water_needed)
                            } else {
                                plant.daysUntilWatering?.let {
                                    stringResource(R.string.plants_water_next, it)
                                } ?: stringResource(R.string.plants_never_watered)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (plant.needsWatering) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.plants_difficulty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DifficultyStars(difficulty = plant.difficulty)
                    }

                    plant.location?.let { location ->
                        if (location.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.plants_location),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = location,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            if (plant.needsWatering) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onWaterClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = stringResource(R.string.plants_water_button),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.plants_water_button))
                }
            }
        }
    }
}

@Composable
fun DifficultyStars(difficulty: Int) {
    Row {
        val starsText = stringResource(R.string.plants_star_full).repeat(difficulty)

        Text(
            text = starsText,
            color = getDifficultyColor(0, difficulty),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun getDifficultyColor(starIndex: Int, difficulty: Int): Color {
    return when (difficulty) {
        1, 2 -> Color.Green
        3 -> Color(0xFFFF9800)
        4, 5 -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun PlantPhoto(plant: PlantModel) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (!plant.photoUri.isNullOrEmpty()) {
            val uriResult = remember(plant.photoUri) {
                runCatching {
                    val isRelativePath = !plant.photoUri.startsWith(Constants.UriSchemes.CONTENT) &&
                            !plant.photoUri.startsWith(Constants.UriSchemes.FILE)

                    if (isRelativePath) {
                        ImageUtils.getImageUri(context, plant.photoUri)
                    } else {
                        Uri.parse(plant.photoUri)
                    }
                }
            }

            uriResult.fold(
                onSuccess = { uri ->
                    if (uri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(uri)
                                .build(),
                            contentDescription = plant.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ShowPlantIcon()
                    }
                },
                onFailure = {
                    ShowPlantIcon()
                }
            )
        } else {
            ShowPlantIcon()
        }
    }
}

@Composable
fun ShowPlantIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.plants_sprout_emoji),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}