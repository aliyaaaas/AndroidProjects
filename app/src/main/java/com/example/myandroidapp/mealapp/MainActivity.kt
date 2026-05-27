package com.example.myandroidapp.mealapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myandroidapp.mealapp.core.DetailActivity
import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.ui.theme.MealAppTheme
import com.example.myandroidapp.R
import com.example.myandroidapp.mealapp.core.utils.Constants
import com.example.myandroidapp.mealapp.core.utils.ScreenLogger
import com.example.myandroidapp.mealapp.info.InfoBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private val snackbarHostState = SnackbarHostState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ScreenLogger.logScreenOpen(this, R.string.screen_name_main)

        setContent {
            MealAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    MealSearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun MealSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel,
    snackbarHostState: SnackbarHostState
) {
    val lastQuery by viewModel.lastQuery.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val mealList by viewModel.mealList.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val shouldShowInfo by viewModel.shouldShowInfoScreen.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(lastQuery) {
        if (lastQuery.isNotBlank()) {
            searchQuery = lastQuery
        }
    }
    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage.collect { message ->
            message?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.onSnackbarShown()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader()

        SearchSection(
            searchQuery = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearchClick = { viewModel.searchMeals(searchQuery) },
            isSearchEnabled = searchQuery.isNotBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))

        MealListSection(
            mealList = mealList,
            isLoading = isLoading,
            errorMessage = errorMessage,
            searchQuery = searchQuery,
            onMealClick = { meal ->

                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra(Constants.KEY_MEAL_ID, meal.id)
                }
                context.startActivity(intent)
            }
        )
    }

    if (shouldShowInfo) InfoBottomSheet(onDismiss = {
        viewModel.markInfoScreenAsSeen()
    })
}

@Composable
private fun ScreenHeader() {
    Text(
        text = stringResource(R.string.screen_title),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    Text(
        text = stringResource(R.string.screen_subtitle),
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

@Composable
private fun SearchSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    isSearchEnabled: Boolean
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        label = { Text(stringResource(R.string.search_hint)) },
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = onSearchClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = isSearchEnabled,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(stringResource(R.string.search_button))
    }
}

@Composable
private fun MealListSection(
    mealList: List<MealModel>,
    isLoading: Boolean,
    errorMessage: String?,
    searchQuery: String,
    onMealClick: (MealModel) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            ErrorCard(errorMessage = errorMessage)
        }
        mealList.isNotEmpty() -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = mealList,
                    key = { it.id }
                ) { meal ->
                    MealCard(
                        meal = meal,
                        onMealClick = onMealClick
                    )
                }
            }
        }
        searchQuery.isNotBlank() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_results))
            }
        }
    }
}

@Composable
private fun ErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = errorMessage,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun MealCard(
    meal: MealModel,
    onMealClick: (MealModel) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onMealClick(meal) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = meal.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = meal.name,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${meal.category} • ${meal.area}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}