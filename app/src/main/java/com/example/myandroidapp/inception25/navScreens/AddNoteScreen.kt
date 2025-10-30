package com.example.myandroidapp.inception25.navScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myandroidapp.R
import com.example.myandroidapp.inception25.model.NoteDataModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun AddNoteScreen(
    email: String,
    currentNotes: List<NoteDataModel>,
    navController: NavHostController
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }

    val titleRequiredText = stringResource(R.string.title_required)
    val noteTitleHint = stringResource(R.string.note_title_hint)
    val noteContentHint = stringResource(R.string.note_content_hint)
    val saveNoteButton = stringResource(R.string.save_note_button)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = {
                title = it
                titleError = null
            },
            label = { Text(noteTitleHint) },
            modifier = Modifier.fillMaxWidth(),
            isError = titleError != null
        )

        if (titleError != null) {
            Text(
                text = titleError!!,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text(noteContentHint) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = titleRequiredText
                } else {
                    val newNote = NoteDataModel(title = title, content = content)
                    val jsonNote = Json.encodeToString(newNote)

                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "newNoteJson",
                        jsonNote
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(saveNoteButton)
        }
    }
}