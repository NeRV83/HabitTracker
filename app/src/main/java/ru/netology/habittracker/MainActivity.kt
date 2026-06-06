package ru.netology.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.netology.habittracker.ui.AddEditHabitScreen
import ru.netology.habittracker.ui.HabitDetailScreen
import ru.netology.habittracker.ui.HabitListScreen
import ru.netology.habittracker.ui.theme.HabitTrackerTheme
import ru.netology.habittracker.viewmodel.HabitDetailViewModel
import ru.netology.habittracker.viewmodel.HabitListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val habitListViewModel = remember { HabitListViewModel() }

                    NavHost(
                        navController = navController,
                        startDestination = "habit_list"
                    ) {
                        composable("habit_list") {
                            HabitListScreen(
                                onAddHabit = { navController.navigate("add_habit") },
                                onEditHabit = { habitId ->
                                    navController.navigate("edit_habit/$habitId")
                                },
                                onViewHabit = { habitId ->
                                    navController.navigate("habit_detail/$habitId")
                                },
                                viewModel = habitListViewModel
                            )
                        }

                        composable("add_habit") {
                            AddEditHabitScreen(
                                navController = navController,
                                habitId = null
                            )
                        }

                        composable(
                            "edit_habit/{habitId}",
                            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val habitId = backStackEntry.arguments?.getString("habitId")
                            AddEditHabitScreen(
                                navController = navController,
                                habitId = habitId
                            )
                        }

                        composable(
                            "habit_detail/{habitId}",
                            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
                            val habitDetailViewModel = remember(habitId) {
                                HabitDetailViewModel(habitId)
                            }
                            HabitDetailScreen(
                                navController = navController,
                                habitId = habitId,
                                onEdit = { navController.navigate("edit_habit/$habitId") },
                                habitDetailViewModel = habitDetailViewModel,
                                habitListViewModel = habitListViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}