package com.example.myandroidapp.mealapp.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myandroidapp.R
import com.example.myandroidapp.ui.theme.MealAppTheme

class DetailActivity : ComponentActivity() {

    companion object {
        const val EXTRA_MEAL_ID = "meal_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mealId = intent.getStringExtra(EXTRA_MEAL_ID) ?: ""

        setContent {
            MealAppTheme {
                Scaffold(
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 25.dp)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Button(
                                onClick = { finish() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(stringResource(R.string.button_back))
                            }
                        }
                    }
                ) { innerPadding ->
                    DetailScreen(
                        modifier = Modifier.padding(innerPadding),
                        mealId = mealId
                    )
                }
            }
        }
    }
}

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    mealId: String,
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory)
) {
    val meal by viewModel.mealDetails.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(mealId) {
        viewModel.loadMealDetails(mealId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: stringResource(R.string.error_load_details),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
            meal != null -> {
                MealDetailContent(meal!!)
            }
            else -> {
                Text(
                    text = stringResource(R.string.meal_not_found),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun MealDetailContent(meal: com.example.myandroidapp.mealapp.core.domain.model.MealModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = meal.imageUrl,
            contentDescription = meal.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Text(
            text = meal.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "${meal.category} • ${meal.area}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.instructions_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = meal.instructions,
            style = MaterialTheme.typography.bodyMedium
        )

        if (!meal.youtubeUrl.isNullOrEmpty()) {
            Text(
                text = stringResource(R.string.video_tutorial, meal.youtubeUrl),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}