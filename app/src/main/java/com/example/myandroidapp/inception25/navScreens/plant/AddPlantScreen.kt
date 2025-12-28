package com.example.myandroidapp.inception25.navScreens.plant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.data.UserDataRepository
import com.example.myandroidapp.inception25.di.ServiceLocator
import com.example.myandroidapp.inception25.navigation.PlantsListScreen
import com.example.myandroidapp.inception25.utils.ImageUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    navController: NavController
) {
    val userId = UserDataRepository.getCurrentUserId() ?: run {
        navController.navigate(com.example.myandroidapp.inception25.navigation.LoginScreen)
        return
    }

    val context = LocalContext.current
    val plantTypes = context.resources.getStringArray(R.array.plant_types).toList()

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(plantTypes.firstOrNull() ?: "") }
    var location by remember { mutableStateOf("") }
    var wateringInterval by remember { mutableStateOf(7f) }
    var difficulty by remember { mutableStateOf(3f) }
    var notes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var originalPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var savedPhotoPath by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            originalPhotoUri = it
            savedPhotoPath = null
        }
    }

    val plantRepository = ServiceLocator.getPlantRepository()
    val scope = rememberCoroutineScope()
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_plant_title)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        savedPhotoPath != null -> {
                            val fileUri = ImageUtils.getImageUri(context, savedPhotoPath!!)
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(fileUri)
                                    .build(),
                                contentDescription = stringResource(R.string.add_plant_photo_placeholder),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        originalPhotoUri != null -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(originalPhotoUri)
                                    .build(),
                                contentDescription = stringResource(R.string.add_plant_photo_placeholder),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = stringResource(R.string.add_plant_photo_button),
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.add_plant_photo_placeholder),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = if (originalPhotoUri != null || savedPhotoPath != null)
                        stringResource(R.string.add_plant_photo_change)
                    else
                        stringResource(R.string.add_plant_photo_button),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.add_plant_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = !isExpanded }
            ) {
                TextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.add_plant_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    plantTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.add_plant_location)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.add_plant_watering_interval, wateringInterval.toInt()),
                modifier = Modifier.fillMaxWidth()
            )

            Slider(
                value = wateringInterval,
                onValueChange = { wateringInterval = it },
                valueRange = 1f..30f,
                steps = 28,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.add_plant_difficulty, difficulty.toInt()),
                modifier = Modifier.fillMaxWidth()
            )

            Slider(
                value = difficulty,
                onValueChange = { difficulty = it },
                valueRange = 1f..5f,
                steps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.add_plant_notes)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = context.getString(R.string.add_plant_name_required)
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        scope.launch {
                            try {
                                var finalPhotoPath: String? = savedPhotoPath

                                if (originalPhotoUri != null && savedPhotoPath == null) {
                                    finalPhotoPath = ImageUtils
                                        .saveImageToPrivateStorage(context, originalPhotoUri!!)
                                }

                                val plantId = plantRepository.addPlant(
                                    com.example.myandroidapp.inception25.model.PlantInputModel(
                                        name = name,
                                        type = selectedType,
                                        location = location.ifBlank { null },
                                        photoUri = finalPhotoPath,
                                        wateringInterval = wateringInterval.toInt(),
                                        difficulty = difficulty.toInt(),
                                        notes = notes.ifBlank { null },
                                        rating = 3
                                    ),
                                    userId = userId
                                )

                                isLoading = false
                                navController.navigate(PlantsListScreen)
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = "${context.getString(R.string.common_error)}: ${e.message}"
                                e.printStackTrace()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.add_plant_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    }
}