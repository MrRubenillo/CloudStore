package com.cloudstore.app.model

import androidx.annotation.DrawableRes
import com.cloudstore.app.R

enum class Category(val displayName: String, val id: String) {
    ALL("Todos", "todos"),
    AUDIO("Audio", "audio"),
    PERIPHERALS("Periféricos", "perifericos"),
    GAMING("Gaming", "gaming"),
    STORAGE("Almacenamiento", "almacenamiento")
}

enum class SortOption(val label: String) {
    FEATURED("Destacados"),
    PRICE_ASC("Menor precio"),
    PRICE_DESC("Mayor precio"),
    RATING("Mejor calificados")
}

data class Product(
    val id: String,
    val name: String,
    val category: Category,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Double,
    val reviewsCount: Int,
    val stock: Int,
    val badge: String? = null,
    val shortDescription: String,
    val description: String,
    val specs: List<Pair<String, String>>,
    val iconName: String,
    @param:DrawableRes val imageRes: Int = 0
)

data class CartItem(
    val product: Product,
    val quantity: Int
)

enum class PaymentMethod(val title: String, val subtitle: String) {
    CARD("Tarjeta de Crédito / Débito", "Visa, Mastercard, AMEX •••• 4210"),
    PAYPAL("PayPal", "Pago rápido y protegido con tu cuenta"),
    OXXO("OXXO Pay", "Pago en efectivo en cualquier sucursal OXXO")
}

enum class ShippingMethod(val title: String, val subtitle: String, val cost: Double) {
    STANDARD("Envío Estándar", "Entrega estimada de 3 a 5 días hábiles", 0.0),
    EXPRESS("Envío Express CloudAir", "Entrega garantizada en 24 horas", 99.0)
}

enum class OrderStatus(val label: String) {
    SHIPPED("Enviado"),
    DELIVERED("Entregado")
}

data class Order(
    val id: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val shippingCost: Double,
    val tax: Double,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val shippingMethod: ShippingMethod,
    val timestamp: String,
    val status: OrderStatus
)

data class TelemetryEvent(
    val id: String,
    val eventName: String,
    val paramsSummary: String,
    val timestamp: String
)

object CatalogRepository {
    val products: List<Product> = listOf(
        Product(
            id = "PROD-001",
            name = "Audífonos In-Ear Soundcore P40i TWS ANC",
            category = Category.AUDIO,
            price = 999.0,
            originalPrice = 1249.0,
            rating = 4.8,
            reviewsCount = 540,
            stock = 24,
            badge = "Top Ventas",
            shortDescription = "Cancelación activa adaptativa, estuche con soporte para celular y 60 h totales.",
            description = "Disfruta música y llamadas claras en transporte o oficina gracias a sus 6 micrófonos con IA, drivers de 11 mm con graves potentes y carga rápida inalámbrica.",
            specs = listOf(
                "Conectividad" to "Bluetooth 5.3 Multipunto",
                "Autonomía" to "12 h por carga / 60 h con estuche",
                "Carga rápida" to "10 min = 5 horas de uso (USB-C)",
                "Resistencia" to "IPX5 contra lluvia y sudor"
            ),
            iconName = "Earbuds",
            imageRes = R.drawable.prod_earbuds
        ),
        Product(
            id = "PROD-002",
            name = "Audífonos Diadema Sony WH-CH520 Bluetooth",
            category = Category.AUDIO,
            price = 849.0,
            originalPrice = 999.0,
            rating = 4.7,
            reviewsCount = 684,
            stock = 18,
            badge = "-15%",
            shortDescription = "Hasta 50 horas de batería, DSEE y conexión simultánea a dos equipos.",
            description = "Ligeros y cómodos para jornadas largas de estudio o trabajo remoto. Incorporan almohadillas suaves, micrófono integrado para videollamadas y ecualización desde la app.",
            specs = listOf(
                "Autonomía" to "Hasta 50 horas continuas",
                "Carga rápida" to "3 min = 1.5 horas vía USB-C",
                "Conexión" to "Bluetooth 5.2 Multipunto",
                "Peso" to "147 gramos plegables"
            ),
            iconName = "Headphones",
            imageRes = R.drawable.prod_headphones
        ),
        Product(
            id = "PROD-003",
            name = "Bocina Portátil JBL Go 4 Waterproof IP67",
            category = Category.AUDIO,
            price = 899.0,
            originalPrice = null,
            rating = 4.8,
            reviewsCount = 312,
            stock = 20,
            badge = "Nuevo",
            shortDescription = "Tamaño ultracompacto con graves sorprendentes y resistencia al agua y polvo.",
            description = "Llévate el sonido JBL Pro a cualquier lugar con su correa integrada, emparejamiento estéreo con otra unidad mediante Auracast y hasta 7 horas de reproducción continua.",
            specs = listOf(
                "Protección" to "IP67 sumergible y a prueba de polvo",
                "Batería" to "7 horas + 2 h extra con Playtime Boost",
                "Conectividad" to "Bluetooth 5.3 + Auracast",
                "Puerto" to "USB-C de carga rápida"
            ),
            iconName = "Speaker",
            imageRes = R.drawable.prod_speaker
        ),
        Product(
            id = "PROD-004",
            name = "Teclado Mecánico Compacto 75% Hot-Swap RGB",
            category = Category.PERIPHERALS,
            price = 1299.0,
            originalPrice = 1499.0,
            rating = 4.9,
            reviewsCount = 290,
            stock = 12,
            badge = "Top Ventas",
            shortDescription = "Switches lineales prelubricados, keycaps PBT y doble modo inalámbrico.",
            description = "Diseñado para escritorio limpio y escritura ágil. Cuenta con estructura amortiguada tipo Gasket, perilla metálica de volumen y compatibilidad directa con Windows y macOS.",
            specs = listOf(
                "Formato" to "75% compacto (82 teclas)",
                "Switches" to "Mecánicos lineales Hot-Swappable",
                "Conexión" to "USB-C / Dongle 2.4 GHz / Bluetooth",
                "Batería" to "4,000 mAh recargable"
            ),
            iconName = "Keyboard",
            imageRes = R.drawable.prod_keyboard
        ),
        Product(
            id = "PROD-005",
            name = "Mouse Inalámbrico Logitech Signature M650",
            category = Category.PERIPHERALS,
            price = 649.0,
            originalPrice = 799.0,
            rating = 4.8,
            reviewsCount = 745,
            stock = 32,
            badge = "-18%",
            shortDescription = "Rueda SmartWheel de precisión, clics silenciosos SilentTouch y agarre suave.",
            description = "Alterna automáticamente entre desplazamiento línea por línea y giro rápido con un impulso. Funciona mediante Bluetooth Low Energy o receptor Logi Bolt incluido.",
            specs = listOf(
                "Sensor" to "Óptico de hasta 4,000 DPI",
                "Botones" to "5 botones programables silenciosos",
                "Batería" to "Hasta 24 meses con 1 pila AA",
                "Conexión" to "Bluetooth LE y Logi Bolt USB"
            ),
            iconName = "Mouse",
            imageRes = R.drawable.prod_mouse
        ),
        Product(
            id = "PROD-006",
            name = "Hub USB-C UGREEN 7 en 1 HDMI 4K 100W PD",
            category = Category.PERIPHERALS,
            price = 549.0,
            originalPrice = 699.0,
            rating = 4.8,
            reviewsCount = 468,
            stock = 28,
            badge = "Oferta",
            shortDescription = "Salida HDMI 4K@60Hz, 2x USB-A 3.0, lector SD/TF y carga rápida Pass-Through.",
            description = "Expande los puertos de tu laptop, tablet o consola portátil con un cuerpo compacto de aluminio disipador de calor y transferencia de datos de hasta 5 Gbps.",
            specs = listOf(
                "Video" to "HDMI hasta 4K a 60 Hz",
                "Carga" to "USB-C Power Delivery hasta 100W",
                "Datos" to "2x USB 3.0 (5 Gbps) + Lector SD/microSD",
                "Chasis" to "Aleación de aluminio anodizado"
            ),
            iconName = "Devices",
            imageRes = R.drawable.prod_hub
        ),
        Product(
            id = "PROD-007",
            name = "Lámpara LED para Monitor Baseus Eye-Care USB-C",
            category = Category.PERIPHERALS,
            price = 699.0,
            originalPrice = null,
            rating = 4.7,
            reviewsCount = 215,
            stock = 16,
            badge = "Destacado",
            shortDescription = "Iluminación asimétrica sin reflejos en pantalla con atenuador táctil.",
            description = "Ilumina tu teclado y escritorio sin generar deslumbramiento en el monitor ni fatiga visual. Ajusta la temperatura de color en 3 niveles desde luz cálida hasta blanca fría.",
            specs = listOf(
                "Óptica" to "Asimétrica anti-reflejo CRI >95",
                "Temperatura" to "3000K / 4000K / 6500K regulable",
                "Alimentación" to "Cable USB-C conectado al monitor o PC",
                "Montaje" to "Pinza con contrapeso para monitores planos/curvos"
            ),
            iconName = "Monitor",
            imageRes = R.drawable.prod_stand
        ),
        Product(
            id = "PROD-008",
            name = "Kit 2 Cables USB-C Trenzados 100W 2 Metros",
            category = Category.PERIPHERALS,
            price = 249.0,
            originalPrice = 329.0,
            rating = 4.9,
            reviewsCount = 980,
            stock = 45,
            badge = "Top Ventas",
            shortDescription = "Nylon trenzado de alta resistencia con chip E-Marker para laptops y teléfonos.",
            description = "Paquete de 2 cables USB-C a USB-C de 2 metros certificados para carga rápida Power Delivery de hasta 100W, conectores reforzados y velcro organizador incluido.",
            specs = listOf(
                "Potencia" to "Hasta 100W (20V/5A) con chip E-Marker",
                "Longitud" to "2 metros cada cable (pack de 2)",
                "Material" to "Nylon trenzado doble + puntas de aluminio",
                "Resistencia" to "Más de 20,000 dobleces probados"
            ),
            iconName = "Devices",
            imageRes = R.drawable.prod_cable
        ),
        Product(
            id = "PROD-009",
            name = "Control Inalámbrico 8BitDo Ultimate C Bluetooth",
            category = Category.GAMING,
            price = 749.0,
            originalPrice = 899.0,
            rating = 4.8,
            reviewsCount = 334,
            stock = 19,
            badge = "-16%",
            shortDescription = "Joysticks precisos, vibración dual y agarre ergonómico para PC, Android y Switch.",
            description = "Control ligero de respuesta rápida con cruceta clásica de alta precisión, gatillos suaves y batería interna recargable vía USB-C para largas sesiones de juego.",
            specs = listOf(
                "Compatibilidad" to "Windows, Android, SteamOS y Switch",
                "Conexión" to "Bluetooth / Dongle 2.4 GHz / USB-C",
                "Autonomía" to "Hasta 25 horas de juego por carga",
                "Extras" to "Vibración háptica y modo Turbo"
            ),
            iconName = "SportsEsports",
            imageRes = R.drawable.prod_gamepad
        ),
        Product(
            id = "PROD-010",
            name = "Headset Gamer HyperX Cloud Stinger 2 Core",
            category = Category.GAMING,
            price = 799.0,
            originalPrice = null,
            rating = 4.6,
            reviewsCount = 264,
            stock = 21,
            badge = "Gaming",
            shortDescription = "Drivers direccionales de 40 mm, sonido espacial DTS y micrófono abatible.",
            description = "Comodidad ligera de solo 275 gramos con diadema ajustable, control de volumen integrado en la orejera y micrófono que se silencia al levantarlo.",
            specs = listOf(
                "Drivers" to "Dinámicos de 40 mm con neodimio",
                "Audio" to "DTS Headphone:X Spatial Audio (código PC)",
                "Conector" to "Jack universal de 3.5 mm (PC/Consola/Móvil)",
                "Peso" to "275 gramos con almohadillas suaves"
            ),
            iconName = "HeadsetMic",
            imageRes = R.drawable.prod_headset
        ),
        Product(
            id = "PROD-011",
            name = "SSD Portátil SanDisk Extreme 1TB USB-C",
            category = Category.STORAGE,
            price = 1649.0,
            originalPrice = 1899.0,
            rating = 4.9,
            reviewsCount = 412,
            stock = 15,
            badge = "Top Ventas",
            shortDescription = "Tamaño de bolsillo (29 g) con velocidades de hasta 1,050 MB/s por USB 3.2 Gen 2.",
            description = "Respalda proyectos, fotos RAW, juegos y máquinas virtuales en segundos. Incluye cable USB-C a USB-A y adaptador para funcionar en cualquier computadora o teléfono moderno.",
            specs = listOf(
                "Capacidad" to "1 TB de estado sólido",
                "Velocidad" to "Hasta 1,050 MB/s lectura / 1,000 MB/s escritura",
                "Interfaz" to "USB 3.2 Gen 2 (10 Gbps)",
                "Peso" to "28.7 gramos ultra compacto"
            ),
            iconName = "SdStorage",
            imageRes = R.drawable.prod_ssd
        ),
        Product(
            id = "PROD-012",
            name = "Power Bank Baseus 20,000 mAh 65W Carga Rápida",
            category = Category.STORAGE,
            price = 1199.0,
            originalPrice = 1399.0,
            rating = 4.8,
            reviewsCount = 378,
            stock = 17,
            badge = "-14%",
            shortDescription = "Pantalla digital LED de voltaje, 2 puertos USB-C PD 65W y apto para laptops.",
            description = "Energía portátil suficiente para cargar una laptop USB-C, tablet y smartphone al mismo tiempo con protección térmica inteligente y pantalla digital en tiempo real.",
            specs = listOf(
                "Capacidad" to "20,000 mAh (74 Wh apto para avión)",
                "Potencia" to "Salida máxima de 65W Power Delivery",
                "Puertos" to "2x USB-C PD + 1x USB-A Quick Charge",
                "Pantalla" to "Display digital de porcentaje y watts"
            ),
            iconName = "Memory",
            imageRes = R.drawable.prod_powerbank
        )
    )
}
