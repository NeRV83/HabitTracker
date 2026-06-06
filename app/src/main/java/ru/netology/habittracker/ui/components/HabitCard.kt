package ru.netology.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.netology.habittracker.data.Frequency
import ru.netology.habittracker.data.Habit

@Composable
fun HabitCard(
    habit: Habit,
    priorityColor: Int,
    isCompleted: Boolean,
    progressPercent: Float,
    isEditable: Boolean, // Флаг - можно ли редактировать и отмечать
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleCompletion: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(priorityColor))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.subtitle1
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(priorityColor).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (habit.frequency == Frequency.DAILY) "Ежедневно" else "Еженедельно",
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                color = Color(priorityColor)
                            )
                        }
                    }

                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.body2,
                        maxLines = 2
                    )
                }

                // Кнопка отметки выполнения - активна только если isEditable = true
                if (isEditable) {
                    IconButton(onClick = onToggleCompletion) {
                        Icon(
                            if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = if (isCompleted) "Отметить невыполненным" else "Отметить выполненным",
                            tint = if (isCompleted) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                } else {
                    // В режиме просмотра показываем только статус без возможности изменения
                    Icon(
                        if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = if (isCompleted) "Выполнено" else "Не выполнено",
                        tint = if (isCompleted) Color(0xFF4CAF50) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Прогресс-бар
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (habit.frequency == Frequency.DAILY) "Прогресс за неделю" else "Выполнено за неделю",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                    Text(
                        text = if (habit.frequency == Frequency.DAILY)
                            "${progressPercent.toInt()}%"
                        else
                            if (progressPercent == 100f) "Выполнено" else "Не выполнено",
                        style = MaterialTheme.typography.caption,
                        color = if (progressPercent == 100f) Color(0xFF4CAF50) else Color.Gray
                    )
                }

                LinearProgressIndicator(
                    progress = progressPercent / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Color(priorityColor),
                    backgroundColor = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Кнопки редактирования и удаления - активны только если isEditable = true
                if (isEditable) {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать", modifier = Modifier.size(20.dp))
                    }

                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}