package com.cloudstore.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cloudstore.app.analytics.AnalyticsManager
import com.cloudstore.app.model.CartItem
import com.cloudstore.app.model.CatalogRepository
import com.cloudstore.app.model.Category
import com.cloudstore.app.model.Order
import com.cloudstore.app.model.OrderStatus
import com.cloudstore.app.model.PaymentMethod
import com.cloudstore.app.model.Product
import com.cloudstore.app.model.ShippingMethod
import com.cloudstore.app.model.SortOption
import com.cloudstore.app.model.TelemetryEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CartSummary(
    val itemCount: Int = 0,
    val subtotal: Double = 0.0,
    val discountPercent: Int = 0,
    val discountAmount: Double = 0.0,
    val shippingCost: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0
)

@OptIn(FlowPreview::class)
class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val analyticsManager = AnalyticsManager(application)
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-MX"))

    val telemetryEvents: StateFlow<List<TelemetryEvent>> = analyticsManager.eventsFlow

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(Category.ALL)
    val selectedCategory: StateFlow<Category> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.FEATURED)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.CARD)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    private val _selectedShippingMethod = MutableStateFlow(ShippingMethod.STANDARD)
    val selectedShippingMethod: StateFlow<ShippingMethod> = _selectedShippingMethod.asStateFlow()

    private val _orders = MutableStateFlow(createInitialDemoOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _lastConfirmedOrder = MutableStateFlow<Order?>(null)
    val lastConfirmedOrder: StateFlow<Order?> = _lastConfirmedOrder.asStateFlow()

    private var lastLoggedSearchTerm: String = ""

    val filteredProducts: StateFlow<List<Product>> = combine(
        _searchQuery,
        _selectedCategory,
        _sortOption,
        _favoriteIds,
        _onlyFavorites
    ) { query, category, sort, favs, onlyFavs ->
        val normalizedQuery = query.trim().lowercase(Locale.getDefault())
        val baseList = CatalogRepository.products.filter { product ->
            val matchesCategory = category == Category.ALL || product.category == category
            val matchesFavorites = !onlyFavs || favs.contains(product.id)
            val matchesQuery = normalizedQuery.isEmpty() ||
                product.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                product.shortDescription.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                product.category.displayName.lowercase(Locale.getDefault()).contains(normalizedQuery)
            matchesCategory && matchesFavorites && matchesQuery
        }

        when (sort) {
            SortOption.FEATURED -> baseList
            SortOption.PRICE_ASC -> baseList.sortedBy { it.price }
            SortOption.PRICE_DESC -> baseList.sortedByDescending { it.price }
            SortOption.RATING -> baseList.sortedByDescending { it.rating }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogRepository.products)

    val cartSummary: StateFlow<CartSummary> = combine(
        _cartItems,
        _appliedCoupon,
        _selectedShippingMethod
    ) { items, coupon, shipping ->
        if (items.isEmpty()) {
            CartSummary()
        } else {
            val itemCount = items.sumOf { it.quantity }
            val subtotal = items.sumOf { it.product.price * it.quantity }
            val discountPercent = couponDiscountPercent(coupon)
            val discountAmount = subtotal * (discountPercent / 100.0)
            val taxableBase = (subtotal - discountAmount).coerceAtLeast(0.0)
            val taxAmount = taxableBase * TAX_RATE
            val shippingCost = shipping.cost
            val totalAmount = taxableBase + taxAmount + shippingCost
            CartSummary(
                itemCount = itemCount,
                subtotal = subtotal,
                discountPercent = discountPercent,
                discountAmount = discountAmount,
                shippingCost = shippingCost,
                taxAmount = taxAmount,
                totalAmount = totalAmount
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartSummary())

    val pendingOrdersCount: StateFlow<Int> = _orders
        .map { list -> list.count { it.status == OrderStatus.SHIPPED } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 1)

    init {
        viewModelScope.launch {
            _searchQuery
                .map { it.trim() }
                .debounce(600L)
                .distinctUntilChanged()
                .collect { term ->
                    if (term.length >= 2 && term != lastLoggedSearchTerm) {
                        lastLoggedSearchTerm = term
                        analyticsManager.logSearch(term)
                    }
                }
        }
    }

    fun getProductById(productId: String): Product? =
        CatalogRepository.products.find { it.id == productId }

    fun toggleTheme() {
        _isDarkTheme.update { !it }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            lastLoggedSearchTerm = ""
        }
    }

    fun submitSearch() {
        val term = _searchQuery.value.trim()
        if (term.isNotEmpty() && term != lastLoggedSearchTerm) {
            lastLoggedSearchTerm = term
            analyticsManager.logSearch(term)
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        analyticsManager.logSelectCategory(category.id, category.displayName)
    }

    fun selectSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleOnlyFavorites() {
        _onlyFavorites.update { !it }
    }

    fun toggleFavorite(product: Product) {
        val nowFavorite = !_favoriteIds.value.contains(product.id)
        _favoriteIds.update { current ->
            if (nowFavorite) current + product.id else current - product.id
        }
        analyticsManager.logToggleFavorite(product, nowFavorite)
    }

    fun trackViewItem(product: Product) {
        analyticsManager.logViewItem(product)
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        val safeQty = quantity.coerceIn(1, product.stock)
        _cartItems.update { current ->
            val existing = current.find { it.product.id == product.id }
            if (existing != null) {
                current.map { item ->
                    if (item.product.id == product.id) {
                        item.copy(quantity = (item.quantity + safeQty).coerceAtMost(product.stock))
                    } else {
                        item
                    }
                }
            } else {
                current + CartItem(product = product, quantity = safeQty)
            }
        }
        analyticsManager.logAddToCart(product, safeQty)
    }

    fun updateCartQuantity(productId: String, delta: Int) {
        _cartItems.update { current ->
            current.mapNotNull { item ->
                if (item.product.id == productId) {
                    val nextQty = item.quantity + delta
                    if (nextQty <= 0) null
                    else item.copy(quantity = nextQty.coerceAtMost(item.product.stock))
                } else {
                    item
                }
            }
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.update { current -> current.filterNot { it.product.id == productId } }
    }

    fun applyCoupon(rawCode: String): Boolean {
        val normalized = rawCode.trim().uppercase(Locale.getDefault())
        return if (couponDiscountPercent(normalized) > 0) {
            _appliedCoupon.value = normalized
            analyticsManager.logApplyCoupon(normalized)
            true
        } else {
            false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun selectShippingMethod(method: ShippingMethod) {
        _selectedShippingMethod.value = method
    }

    fun startCheckout() {
        _lastConfirmedOrder.value = null
        val total = cartSummary.value.totalAmount
        if (total > 0.0) {
            analyticsManager.logBeginCheckout(total)
        }
    }

    fun buyNow(product: Product, quantity: Int = 1) {
        _lastConfirmedOrder.value = null
        addToCart(product, quantity)
        analyticsManager.logBeginCheckout(cartSummary.value.totalAmount)
    }

    fun confirmOrder(): Order? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val summary = cartSummary.value
        val existingIds = _orders.value.map { it.id }.toSet()
        var candidateId: String
        do {
            candidateId = "ORD-${(1000..9999).random()}"
        } while (candidateId in existingIds)

        val formattedTime = synchronized(dateTimeFormat) { dateTimeFormat.format(Date()) }
        val order = Order(
            id = candidateId,
            items = items,
            subtotal = summary.subtotal,
            discount = summary.discountAmount,
            shippingCost = summary.shippingCost,
            tax = summary.taxAmount,
            total = summary.totalAmount,
            paymentMethod = _selectedPaymentMethod.value,
            shippingMethod = _selectedShippingMethod.value,
            timestamp = formattedTime,
            status = OrderStatus.SHIPPED
        )

        _orders.update { listOf(order) + it }
        _lastConfirmedOrder.value = order
        _cartItems.value = emptyList()
        _appliedCoupon.value = null

        analyticsManager.logPurchase(
            transactionId = order.id,
            value = order.total,
            tax = order.tax,
            shipping = order.shippingCost
        )
        return order
    }

    fun clearLastConfirmedOrder() {
        _lastConfirmedOrder.value = null
    }

    fun markOrderAsDelivered(orderId: String) {
        var marked = false
        _orders.update { current ->
            current.map { order ->
                if (order.id == orderId && order.status == OrderStatus.SHIPPED) {
                    marked = true
                    order.copy(status = OrderStatus.DELIVERED)
                } else {
                    order
                }
            }
        }
        if (marked) {
            analyticsManager.logOrderDelivered(orderId)
        }
    }

    private fun couponDiscountPercent(code: String?): Int = when (code?.uppercase(Locale.getDefault())) {
        "CLOUD10" -> 10
        "TECH20" -> 20
        else -> 0
    }

    private fun createInitialDemoOrders(): List<Order> {
        val mouseProduct = CatalogRepository.products[4] // Mouse Inalámbrico Logitech Signature M650 ($649)
        val cableProduct = CatalogRepository.products[7] // Kit 2 Cables USB-C Trenzados 100W ($249)
        val subtotal = mouseProduct.price + cableProduct.price
        val tax = subtotal * TAX_RATE
        return listOf(
            Order(
                id = "ORD-7319",
                items = listOf(
                    CartItem(mouseProduct, 1),
                    CartItem(cableProduct, 1)
                ),
                subtotal = subtotal,
                discount = 0.0,
                shippingCost = 0.0,
                tax = tax,
                total = subtotal + tax,
                paymentMethod = PaymentMethod.CARD,
                shippingMethod = ShippingMethod.STANDARD,
                timestamp = "Hoy, pedido reciente",
                status = OrderStatus.SHIPPED
            )
        )
    }

    companion object {
        private const val TAX_RATE = 0.16
    }
}
