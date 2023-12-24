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