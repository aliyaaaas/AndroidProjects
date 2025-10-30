package com.example.myandroidapp.inception25.navScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.ExperimentalMaterialApi
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.model.NoteDataModel
import com.example.myandroidapp.ui.theme.AppColorScheme
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotesScreen(
    email: String,
    notes: List<NoteDataModel>,
    navController: NavHostController,
    onColorSchemeChanged: (AppColorScheme) -> Unit
) {
    var currentNotes by remember { mutableStateOf(notes) }

    LaunchedEffect(navController.currentBackStackEntry) {
        val jsonNote = navController.currentBackStackEntry?.savedStateHandle?.get<String>("newNoteJson")
        if (!jsonNote.isNullOrBlank()) {
            try {
                val newNote = Json.decodeFromString<NoteDataModel>(jsonNote)
                currentNotes = currentNotes + newNote
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>("newNoteJson")
            } catch (e: Exception) {
            }
        }
    }

    val userEmailText = stringResource(R.string.user_email, email)
    val noNotesText = stringResource(R.string.no_notes)
    val addNoteButton = stringResource(R.string.add_note_button)
    val colorSchemeLabel = stringResource(R.string.color_scheme_label)

    var expanded by remember { mutableStateOf(false) }
    val colorSchemes = AppColorScheme.values().toList()
    var selectedScheme by remember { mutableStateOf(AppColorScheme.Purple) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = userEmailText,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedScheme.name,
                onValueChange = {},
                readOnly = true,
                label = { Text(colorSchemeLabel) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                colorSchemes.forEach { scheme ->
                    DropdownMenuItem(
                        onClick = {
                            selectedScheme = scheme
                            onColorSchemeChanged(scheme)
                            expanded = false
                        }
                    ) {
                        Text(scheme.name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(noNotesText)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentNotes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.h6
                            )
                            if (note.content.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("addNote/$email")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(addNoteButton)
        }
    }
}