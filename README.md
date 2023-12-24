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
            )
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
composable(route = CategoryNavigation.route, arguments = CategoryNavigation.arguments()) {
            CategoryScreen(
                categoryId = CategoryNavigation.categoryId(it),
                categoryName = CategoryNavigation.categoryName(it)
            )
        }
```

# How does generated route look like?
```kotlin
public object CategoryNavigation {
public const val categoryIdArg: String = "categoryIdArg"

    public const val categoryNameArg: String = "categoryNameArg"

    public val destination: String = "category_destination"

    public val route: String =
        "category_route/$categoryIdArg={$categoryIdArg}/$categoryNameArg={$categoryNameArg}"

    public fun arguments(): List<NamedNavArgument> {
        // list of arguments
        return listOf(navArgument(categoryIdArg) {
            type = NavType.IntType
            nullable = false
        }, navArgument(categoryNameArg) {
            type = NavType.StringType
            nullable = false
        })
    }

    public fun categoryId(navBackStackEntry: NavBackStackEntry): Int =
        navBackStackEntry.arguments?.getInt(categoryIdArg)
            ?: throw IllegalArgumentException("categoryId is required")

    public fun categoryName(navBackStackEntry: NavBackStackEntry): String =
        navBackStackEntry.arguments?.getString(categoryNameArg)
            ?: throw IllegalArgumentException("categoryName is required")
}
```