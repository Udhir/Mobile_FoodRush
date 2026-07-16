package com.example.foodrush

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.foodrush.repo.FoodRepoImpl
import com.example.foodrush.repo.UserRepoImpl
import com.example.foodrush.view.AddFoodScreen
import com.example.foodrush.view.AdminFoodListScreen
import com.example.foodrush.viewmodel.FoodViewModel
import com.example.foodrush.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registration : Screen("registration")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object FoodDetail : Screen("food_detail/{foodId}") {
        fun createRoute(foodId: String) = "food_detail/$foodId"
    }
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object AdminFoodList : Screen("admin_food_list")

    object AddFood : Screen("add_food?foodId={foodId}") {
        fun createRoute(foodId: String? = null) = if (foodId != null) "add_food?foodId=$foodId" else "add_food"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (FirebaseAuth.getInstance().currentUser != null) Screen.Dashboard.route else Screen.Login.route
) {
    val foodViewModel = remember { FoodViewModel(FoodRepoImpl()) }
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, userViewModel)
        }
        composable(Screen.Registration.route) {
            RegistrationScreen(navController, userViewModel)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController, userViewModel)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, foodViewModel)
        }
        composable(
            Screen.FoodDetail.route,
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            FoodDetailScreen(
                foodId = foodId,
                foodViewModel = foodViewModel,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // FIXED: Passing the navController here!
        composable(Screen.Cart.route) {
            CartScreen(
                navController = navController,
                onCheckout = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminFoodList.route) {
            AdminFoodListScreen(navController = navController, foodViewModel = foodViewModel)
        }

        composable(
            Screen.AddFood.route,
            arguments = listOf(navArgument("foodId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId")
            AddFoodScreen(
                foodId = foodId,
                onClose = { navController.popBackStack() }
            )
        }
    }
}

// Wrapper Composables
@Composable
fun DashboardScreen(navController: NavHostController, viewModel: FoodViewModel) {
    DashboardBody(navController, viewModel)
}
@Composable
fun SplashScreen(navController: NavHostController) {
    Splash(navController)
}
@Composable
fun LoginScreen(navController: NavHostController, viewModel: UserViewModel) {
    LoginBody(navController, viewModel)
}
@Composable
fun RegistrationScreen(navController: NavHostController, viewModel: UserViewModel) {
    RegistrationBody(navController, viewModel)
}



