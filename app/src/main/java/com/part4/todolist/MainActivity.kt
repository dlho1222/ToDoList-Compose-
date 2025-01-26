package com.part4.todolist

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.part4.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(contentColor = MaterialTheme.colorScheme.background) {
                        TopLevel(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun TopLevel(modifier: Modifier = Modifier) {
    val (text, setText) = remember { mutableStateOf("") }
    val todoList = remember { mutableStateListOf<ToDoData>() }
    val onSubmit: (String) -> Unit = { text ->
        val key = (todoList.lastOrNull()?.key ?: 0) + 1
        todoList.add(ToDoData(key = key, text = text))
        setText("")
    }
    val onToggle: (Int, Boolean) -> Unit = { key, checked ->
        val i = todoList.indexOfFirst { it.key == key }
        todoList[i] = todoList[i].copy(done = checked)
    }

    val onDelete: (Int) -> Unit = { key ->
        val i = todoList.indexOfFirst { it.key == key }
        todoList.removeAt(i)
    }

    val onEdit: (Int, String) -> Unit = { key, text ->
        val i = todoList.indexOfFirst { it.key == key }
        todoList[i] = todoList[i].copy(text = text)
    }

    Column(modifier = modifier.padding(8.dp)) {
        ToDoInput(
            text = text,
            onTextChange = setText,
            onSubmit = onSubmit
        )

        LazyColumn {
            items(todoList, key = { it.key }) { todoData ->
                ToDo(
                    todoData = todoData,
                    onToggle = onToggle,
                    onDelete = onDelete,
                    onEdit = onEdit,
                )
            }
        }
    }
}

@Composable
fun ToDoInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Button(
            onClick = {
                onSubmit(text)
            }
        ) {
            Text(text = "입력")
        }
    }
}

@Composable
fun ToDo(
    todoData: ToDoData,
    onToggle: (Int, Boolean) -> Unit = { _, _ -> },
    onDelete: (Int) -> Unit = {},
    onEdit: (Int, String) -> Unit = { _, _ -> }
) {

    var isEditing by remember { mutableStateOf(false) }
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Crossfade(isEditing) {
            when (it) {
                false -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = todoData.text,
                            modifier = Modifier.weight(1f)
                        )

                        Text(text = "완료")

                        Checkbox(
                            checked = todoData.done,
                            onCheckedChange = { checked ->
                                onToggle(todoData.key, checked)
                            }
                        )

                        Button(onClick = {
                            isEditing = true
                        }) {
                            Text(text = "수정")
                        }

                        Spacer(modifier = Modifier.padding(4.dp))

                        Button(onClick = {
                            onDelete(todoData.key)
                        }) {
                            Text(text = "삭제")
                        }
                    }
                }

                true -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val (text, setText) = remember { mutableStateOf(todoData.text) }
                        OutlinedTextField(
                            value = text,
                            onValueChange = setText,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Button(onClick = {
                            onEdit(todoData.key, text)
                            isEditing = false
                        }) {
                            Text(text = "완료")
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopLevelPreview(modifier: Modifier = Modifier) {
    ToDoListTheme {
        TopLevel()
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoInputPreview(modifier: Modifier = Modifier) {
    ToDoListTheme {
        ToDoInput("테스트", {}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoPreview(modifier: Modifier = Modifier) {
    ToDoListTheme {
        ToDo(
            ToDoData(
                0, "테스트"
            )
        )
    }
}

data class ToDoData(
    val key: Int,
    val text: String,
    val done: Boolean = false
)