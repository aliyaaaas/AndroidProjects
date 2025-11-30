package com.example.myandroidapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myandroidapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    coroutineManager: CoroutineManager = remember {
        CoroutineManager(scope = CoroutineScope(SupervisorJob() + Dispatchers.Main))
    }
) {
    val context = LocalContext.current
    var coroutineSettings by remember { mutableStateOf(CoroutineSettings()) }
    var isRunning by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(coroutineManager) {
        coroutineManager.uiActions
            .onEach { action ->
                when (action) {
                    is CoroutineUiAction.ShowSequentialToast -> {
                        Toast.makeText(context, context.getString(R.string.toast_sequential_completed), Toast.LENGTH_SHORT).show()
                        isRunning = false
                    }
                    is CoroutineUiAction.ShowParallelToast -> {
                        Toast.makeText(context, context.getString(R.string.toast_parallel_completed), Toast.LENGTH_SHORT).show()
                        isRunning = false
                    }
                    CoroutineUiAction.ShowExceptionAToast -> {
                        Toast.makeText(context, context.getString(R.string.error_message_toast), Toast.LENGTH_SHORT).show()
                    }
                    CoroutineUiAction.ShowExceptionBSnackbar -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.snackbar_error))
                        }
                    }
                    CoroutineUiAction.ShowUnknownErrorToast -> {
                        Toast.makeText(context, context.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show()
                    }
                    CoroutineUiAction.ResetSettings -> {
                        coroutineSettings = CoroutineSettings()
                        Toast.makeText(context, context.getString(R.string.error_message_reset), Toast.LENGTH_SHORT).show()
                        isRunning = false
                    }
                    is CoroutineUiAction.ShowCancelledToast -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_cancelled, action.count),
                            Toast.LENGTH_SHORT
                        ).show()
                        isRunning = false
                    }
                    is CoroutineUiAction.ReLaunchInBackground -> {
                        coroutineSettings = action.settings
                        isRunning = true
                        coroutineManager.launchCoroutines(action.settings)
                    }
                }
            }
            .launchIn(this)
    }

    val lifecycle = androidx.lifecycle.ProcessLifecycleOwner.get().lifecycle
    DisposableEffect(lifecycle, coroutineSettings, isRunning, coroutineManager) {
        val observer = object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onPause(owner: androidx.lifecycle.LifecycleOwner) {
                if (!coroutineSettings.runsInBackground && isRunning) {
                    coroutineManager.onAppPaused(
                        runsInBackground = coroutineSettings.runsInBackground,
                        currentSettings = coroutineSettings
                    )
                    isRunning = false
                }
            }

            override fun onResume(owner: androidx.lifecycle.LifecycleOwner) {
                if (!coroutineSettings.runsInBackground) {
                    coroutineManager.onAppResumed()
                }
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(coroutineManager) {
        onDispose {
            coroutineManager.cleanup()
        }
    }

    fun startCoroutines() {
        isRunning = true
        coroutineManager.launchCoroutines(coroutineSettings)
    }

    fun stopCoroutines() {
        isRunning = false
        coroutineManager.cancelActiveCoroutines()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.label_slider))
            Slider(
                value = coroutineSettings.count.toFloat(),
                onValueChange = {
                    val newCount = it.toInt().coerceIn(10, 100)
                    val stepped = ((newCount - 10) / 5) * 5 + 10
                    coroutineSettings = coroutineSettings.copy(count = stepped)
                },
                valueRange = 10f..100f,
                steps = (100 - 10) / 5 - 1
            )
            Text("Count: ${coroutineSettings.count}")


            var expanded by remember { mutableStateOf(false) }
            val dispatcherLabels = mapOf(
                Dispatchers.Default to R.string.dropdown_default,
                Dispatchers.IO to R.string.dropdown_io,
                Dispatchers.Main to R.string.dropdown_main
            )
            val currentLabel = stringResource(dispatcherLabels[coroutineSettings.dispatcher]!!)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = currentLabel,
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    dispatcherLabels.forEach { (dispatcher, labelRes) ->
                        DropdownMenuItem(
                            text = { Text(stringResource(labelRes)) },
                            onClick = {
                                coroutineSettings = coroutineSettings.copy(dispatcher = dispatcher)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // последовательный
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = coroutineSettings.isSequential,
                    onCheckedChange = {
                        coroutineSettings = coroutineSettings.copy(isSequential = it)
                    }
                )
                Text(stringResource(R.string.switch_sequential))
            }

            // параллельный
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = !coroutineSettings.isSequential,
                    onCheckedChange = {
                        coroutineSettings = coroutineSettings.copy(isSequential = !it)
                    }
                )
                Text(stringResource(R.string.switch_parallel))
            }

            // отложенный
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = coroutineSettings.isLazy,
                    onCheckedChange = {
                        coroutineSettings = coroutineSettings.copy(isLazy = it)
                    }
                )
                Text(stringResource(R.string.switch_lazy))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = coroutineSettings.runsInBackground,
                    onCheckedChange = {
                        coroutineSettings = coroutineSettings.copy(runsInBackground = it)
                    }
                )
                Text(stringResource(R.string.switch_background))
            }

            if (isRunning) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { stopCoroutines() }) {
                        Text(stringResource(R.string.button_stop))
                    }
                }
            } else {
                Button(onClick = { startCoroutines() }) {
                    Text(stringResource(R.string.button_start))
                }
            }
        }
    }
}