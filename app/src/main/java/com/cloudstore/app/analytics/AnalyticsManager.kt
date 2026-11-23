package com.cloudstore.app.analytics

import android.content.Context
import android.os.Bundle
import com.cloudstore.app.model.Product
import com.cloudstore.app.model.TelemetryEvent
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Centralizes Firebase Analytics conversion funnel tracking with safe execution
 * and exposes an in-memory session feed for live UI inspection.
 */
class AnalyticsManager(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics? = runCatching {
        FirebaseAnalytics.getInstance(context.applicationContext)
    }.getOrNull()

    private val _eventsFlow = MutableStateFlow<List<TelemetryEvent>>(emptyList())
    val eventsFlow: StateFlow<List<TelemetryEvent>> = _eventsFlow.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.forLanguageTag("es-MX"))

    fun logSearch(searchTerm: String) {
        val cleanTerm = searchTerm.trim()
        if (cleanTerm.isEmpty()) return

        runCatching {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SEARCH_TERM, cleanTerm)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
        }
        recordEvent(
            eventName = FirebaseAnalytics.Event.SEARCH,
            paramsSummary = "search_term=\"$cleanTerm\""
        )
    }

    fun logSelectCategory(categoryId: String, categoryName: String) {
        runCatching {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "category")
                putString(FirebaseAnalytics.Param.ITEM_ID, categoryId)
                putString(FirebaseAnalytics.Param.ITEM_NAME, categoryName)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        }
        recordEvent(
            eventName = FirebaseAnalytics.Event.SELECT_CONTENT,
            paramsSummary = "content_type=category, item_id=$categoryId"
        )
    }

    fun logViewItem(product: Product) {
        runCatching {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, product.id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, product.name)
                putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.category.displayName)
                putDouble(FirebaseAnalytics.Param.PRICE, product.price)
                putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_MXN)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
        }
        recordEvent(
            eventName = FirebaseAnalytics.Event.VIEW_ITEM,
            paramsSummary = "${product.id} • $${product.price.toInt()} $CURRENCY_MXN"
        )
    }

    fun logAddToCart(product: Product, quantity: Int) {
        runCatching {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, product.id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, product.name)
                putDouble(FirebaseAnalytics.Param.PRICE, product.price)
                putLong(FirebaseAnalytics.Param.QUANTITY, quantity.toLong())
                putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_MXN)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
        }
        recordEvent(
            eventName = FirebaseAnalytics.Event.ADD_TO_CART,
            paramsSummary = "${product.id} x$quantity • $${(product.price * quantity).toInt()} $CURRENCY_MXN"
        )
    }

    fun logBeginCheckout(value: Double) {
        runCatching {
            val bundle = Bundle().apply {
                putDouble(FirebaseAnalytics.Param.VALUE, value)
                putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_MXN)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
        }
        recordEvent(
            eventName = FirebaseAnalytics.Event.BEGIN_CHECKOUT,
            paramsSummary = "value=${"%.2f".format(value)} $CURRENCY_MXN"
        )
    }

    fun logPurchase(
        transactionId: String,
        value: Double,
        tax: Double,
        shipping: Double
    ) {
        runCatching {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId)
                putDouble(FirebaseAnalytics.Param.VALUE, value)
                putDouble(FirebaseAnalytics.Param.TAX, tax)
                putDouble(FirebaseAnalytics.Param.SHIPPING, shipping)
                putString(FirebaseAnalytics.Param.CURRENCY, CURRENCY_MXN)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
        }
        recordEvent(
            eventName = FirebaseAnalytics.Event.PURCHASE,
            paramsSummary = "id=$transactionId, total=${"%.2f".format(value)} $CURRENCY_MXN, iva=${"%.2f".format(tax)}"
        )
    }

    fun logOrderDelivered(transactionId: String) {
        runCatching {
            val bundle = Bundle().apply {
                putString("transaction_id", transactionId)
                putString("status", "delivered")
            }
            firebaseAnalytics?.logEvent("order_delivered", bundle)
        }
        recordEvent(
            eventName = "order_delivered",
            paramsSummary = "transaction_id=$transactionId, status=delivered"
        )
    }

    fun logApplyCoupon(code: String) {
        runCatching {
            val bundle = Bundle().apply {
                putString("coupon_code", code)
            }
            firebaseAnalytics?.logEvent("apply_coupon", bundle)
        }
        recordEvent(
            eventName = "apply_coupon",
            paramsSummary = "coupon_code=$code"
        )
    }

    fun logToggleFavorite(product: Product, isFavorite: Boolean) {
        runCatching {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, product.id)
                putString(FirebaseAnalytics.Param.ITEM_NAME, product.name)
                putBoolean("is_favorite", isFavorite)
            }
            firebaseAnalytics?.logEvent("toggle_favorite", bundle)
        }
        recordEvent(
            eventName = "toggle_favorite",
            paramsSummary = "${product.id} • fav=$isFavorite"
        )
    }

    private fun recordEvent(eventName: String, paramsSummary: String) {
        val now = synchronized(timeFormat) { timeFormat.format(Date()) }
        val entry = TelemetryEvent(
            id = UUID.randomUUID().toString(),
            eventName = eventName,
            paramsSummary = paramsSummary,
            timestamp = now
        )
        _eventsFlow.update { current ->
            (listOf(entry) + current).take(MAX_TELEMETRY_HISTORY)
        }
    }

    companion object {
        private const val CURRENCY_MXN = "MXN"
        private const val MAX_TELEMETRY_HISTORY = 30
    }
}
