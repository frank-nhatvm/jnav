### JNAV : Jetpack compose Navigation 
JNAV is a library to generate the code for route of Jetpack Compose Navigation.

## How to use
# Add JNav annotation to the screen that you want to generate route
```kotlin
    @Composable
    @JNav(
    destination = "category_destination",
    baseRoute = "category_route",
    name = "CategoryNavigation",
    arguments = [
            JNavArg(
            name = "categoryId",
            type = Int::class
            ),
            JNavArg(
            name = "categoryName",
            type = String::class
            ),
            JNavArg(name = "parentCategoryId",
            type = String::class,
            isNullable = true)
    ]
    )
    fun CategoryScreen(
    categoryId: Int,
    categoryName: String
    ) {
    }
```

# Use generated route
```kotlin
NavHost(navController = navController, startDestination = HomeScreenNavigation.route) {

    composable(route = HomeScreenNavigation.route) {
        HomeScreen(openCategory = { cateId, cateName ->
            navController.navigate(
                CategoryNavigation.createRoute(
                    categoryId = cateId, categoryName = cateName, parentCategoryId = null
                )
            )
        })
    }

    composable(route = CategoryNavigation.route, arguments = CategoryNavigation.arguments()) {

        CategoryScreen(
            categoryId = CategoryNavigation.categoryId(it),
            categoryName = CategoryNavigation.categoryName(it),
            parentCateId = CategoryNavigation.parentCategoryId(it)
        )
    }

}
```

# How does generated route look like?
```kotlin
public object CategoryNavigation {
    public const val categoryIdArg: String = "categoryIdArg"

    public const val categoryNameArg: String = "categoryNameArg"

    public const val parentCategoryIdArg: String = "parentCategoryIdArg"

    public val destination: String = "category_destination"

    public val route: String =
        "category_route/$categoryIdArg={$categoryIdArg}/$categoryNameArg={$categoryNameArg}?$parentCategoryIdArg={$parentCategoryIdArg}"

    public fun arguments(): List<NamedNavArgument> {
        // list of arguments
        return listOf(navArgument(categoryIdArg){
            type = NavType.IntType
            nullable = false
        },navArgument(categoryNameArg){
            type = NavType.StringType
            nullable = false
        },navArgument(parentCategoryIdArg){
            type = NavType.StringType
            nullable = true
        })
    }

    public fun categoryId(navBackStackEntry: NavBackStackEntry): Int =
        navBackStackEntry.arguments?.getInt(categoryIdArg) ?: throw
        IllegalArgumentException("categoryId is required")

    public fun categoryName(navBackStackEntry: NavBackStackEntry): String =
        navBackStackEntry.arguments?.getString(categoryNameArg) ?: throw
        IllegalArgumentException("categoryName is required")

    public fun parentCategoryId(navBackStackEntry: NavBackStackEntry): String? =
        navBackStackEntry.arguments?.getString(parentCategoryIdArg)

    public fun createRoute(
        categoryId: Int,
        categoryName: String,
        parentCategoryId: String?,
    ): String =
        "category_route/$categoryIdArg=$categoryId/$categoryNameArg=$categoryName?$parentCategoryIdArg=$parentCategoryId"
}
```