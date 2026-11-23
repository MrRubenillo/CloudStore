package com.cloudstore.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cloudstore.app.model.Category
import com.cloudstore.app.model.SortOption
import com.cloudstore.app.ui.screens.CartScreen
import com.cloudstore.app.ui.screens.CheckoutScreen
import com.cloudstore.app.ui.screens.HomeScreen
import com.cloudstore.app.ui.screens.OrdersScreen
import com.cloudstore.app.ui.screens.ProductDetailScreen
import com.cloudstore.app.viewmodel.StoreViewModel
import kotlinx.coroutines.launch

private object Routes {
    const val STORE = "tienda"
    const val CART = "carrito"
    const val ORDERS = "pedidos"
    const val DETAIL = "detalle/{productId}"
    const val CHECKOUT = "checkout"

    fun detail(productId: String) = "detalle/$productId"
}

private val tabRoutes = setOf(Routes.STORE, Routes.CART, Routes.ORDERS)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudStoreApp(
    viewModel: StoreViewModel
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val onlyFavorites by viewModel.onlyFavorites.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val selectedShippingMethod by viewModel.selectedShippingMethod.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val pendingOrdersCount by viewModel.pendingOrdersCount.collectAsState()
    val lastConfirmedOrder by viewModel.lastConfirmedOrder.collectAsState()

    val showMainChrome = currentRoute in tabRoutes

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isNotice = data.visuals.message.contains("inválido", ignoreCase = true) ||
                    data.visuals.message.contains("eliminado", ignoreCase = true) ||
                    data.visuals.message.contains("retirado", ignoreCase = true)
                val accentColor = if (isNotice) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.tertiary
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shadowElevation = 10.dp,
                    tonalElevation = 6.dp,
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isNotice) {
                                Icons.Rounded.Info
                            } else {
                                Icons.Rounded.CheckCircle
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = data.visuals.message,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        topBar = {
            if (showMainChrome) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Cloud,
                                contentDescription = "CloudStore",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "CloudStore",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    },
                    actions = {
                        if (currentRoute == Routes.STORE) {
                            IconButton(
                                onClick = { viewModel.toggleOnlyFavorites() },
                                modifier = Modifier.semantics {
                                    contentDescription = "Filtrar favoritos"
                                }
                            ) {
                                Icon(
                                    imageVector = if (onlyFavorites) {
                                        Icons.Rounded.Favorite
                                    } else {
                                        Icons.Rounded.FavoriteBorder
                                    },
                                    contentDescription = "Filtrar favoritos",
                                    tint = if (onlyFavorites) Color(0xFFFF5E7E) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier.semantics {
                                contentDescription = "Cambiar tema claro u oscuro"
                            }
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) {
                                    Icons.Rounded.LightMode
                                } else {
                                    Icons.Rounded.DarkMode
                                },
                                contentDescription = "Cambiar tema"
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                )
            }
        },
        bottomBar = {
            if (showMainChrome) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.STORE,
                        onClick = { navigateToTab(Routes.STORE) },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Storefront,
                                contentDescription = "Pestaña Tienda"
                            )
                        },
                        label = { Text("Tienda") },
                        modifier = Modifier.semantics { contentDescription = "Pestaña Tienda" }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.CART,
                        onClick = { navigateToTab(Routes.CART) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartSummary.itemCount > 0) {
                                        Badge {
                                            Text(cartSummary.itemCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ShoppingCart,
                                    contentDescription = "Pestaña Carrito"
                                )
                            }
                        },
                        label = { Text("Carrito") },
                        modifier = Modifier.semantics { contentDescription = "Pestaña Carrito" }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.ORDERS,
                        onClick = { navigateToTab(Routes.ORDERS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingOrdersCount > 0) {
                                        Badge {
                                            Text(pendingOrdersCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                                    contentDescription = "Pestaña Pedidos"
                                )
                            }
                        },
                        label = { Text("Pedidos") },
                        modifier = Modifier.semantics { contentDescription = "Pestaña Pedidos" }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.STORE,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Routes.STORE) {
                HomeScreen(
                    products = products,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    sortOption = sortOption,
                    favoriteIds = favoriteIds,
                    onlyFavorites = onlyFavorites,
                    appliedCoupon = appliedCoupon,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onSearchSubmit = viewModel::submitSearch,
                    onSelectCategory = viewModel::selectCategory,
                    onSelectSortOption = viewModel::selectSortOption,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onResetFilters = {
                        viewModel.updateSearchQuery("")
                        viewModel.selectCategory(Category.ALL)
                        viewModel.selectSortOption(SortOption.FEATURED)
                        if (onlyFavorites) viewModel.toggleOnlyFavorites()
                    },
                    onApplyPromoCoupon = {
                        viewModel.applyCoupon("CLOUD10")
                        showMessage("Cupón CLOUD10 aplicado (10% de descuento)")
                    },
                    onProductClick = { product ->
                        navController.navigate(Routes.detail(product.id))
                    },
                    onAddToCart = { product ->
                        viewModel.addToCart(product, 1)
                        showMessage("${product.name} agregado al carrito")
                    }
                )
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { entry ->
                val productId = entry.arguments?.getString("productId").orEmpty()
                val product = viewModel.getProductById(productId)
                if (product != null) {
                    ProductDetailScreen(
                        product = product,
                        isFavorite = favoriteIds.contains(product.id),
                        onTrackViewItem = viewModel::trackViewItem,
                        onBack = { navController.popBackStack() },
                        onToggleFavorite = viewModel::toggleFavorite,
                        onAddToCart = { item, qty ->
                            viewModel.addToCart(item, qty)
                            showMessage("Agregado al carrito ($qty): ${item.name}")
                        },
                        onBuyNow = { item, qty ->
                            viewModel.buyNow(item, qty)
                            navController.navigate(Routes.CHECKOUT)
                        }
                    )
                }
            }

            composable(Routes.CART) {
                CartScreen(
                    cartItems = cartItems,
                    summary = cartSummary,
                    appliedCoupon = appliedCoupon,
                    onUpdateQuantity = viewModel::updateCartQuantity,
                    onRemoveItem = { id ->
                        viewModel.removeFromCart(id)
                        showMessage("Producto eliminado del carrito")
                    },
                    onApplyCoupon = { code ->
                        val ok = viewModel.applyCoupon(code)
                        if (ok) {
                            showMessage("Cupón ${code.trim().uppercase()} aplicado correctamente")
                        } else {
                            showMessage("Cupón inválido. Prueba CLOUD10 o TECH20")
                        }
                    },
                    onRemoveCoupon = {
                        viewModel.removeCoupon()
                        showMessage("Cupón retirado")
                    },
                    onGoToStore = { navigateToTab(Routes.STORE) },
                    onProceedToCheckout = {
                        viewModel.startCheckout()
                        navController.navigate(Routes.CHECKOUT)
                    }
                )
            }

            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    summary = cartSummary,
                    selectedPaymentMethod = selectedPaymentMethod,
                    selectedShippingMethod = selectedShippingMethod,
                    confirmedOrder = lastConfirmedOrder,
                    onSelectPaymentMethod = viewModel::selectPaymentMethod,
                    onSelectShippingMethod = viewModel::selectShippingMethod,
                    onConfirmOrder = { viewModel.confirmOrder() },
                    onBack = { navController.popBackStack() },
                    onViewOrders = {
                        viewModel.clearLastConfirmedOrder()
                        navController.popBackStack(Routes.STORE, inclusive = false)
                        navigateToTab(Routes.ORDERS)
                    },
                    onContinueShopping = {
                        viewModel.clearLastConfirmedOrder()
                        navController.popBackStack(Routes.STORE, inclusive = false)
                        navigateToTab(Routes.STORE)
                    }
                )
            }

            composable(Routes.ORDERS) {
                OrdersScreen(
                    orders = orders,
                    isDarkTheme = isDarkTheme,
                    onMarkAsDelivered = { orderId ->
                        viewModel.markOrderAsDelivered(orderId)
                        showMessage("Pedido $orderId marcado como entregado")
                    }
                )
            }
        }
    }
}
