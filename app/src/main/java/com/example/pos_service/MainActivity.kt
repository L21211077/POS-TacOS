package com.example.pos_service

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pos_service.ui.theme.POSTacOSTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// ==========================================
// MODELOS DE DATOS
// ==========================================

enum class ProductCategory(val label: String) {
    TODOS("🔥 Todos"),
    COMIDA("🌮 Comida"),
    BEBIDAS("🥤 Bebidas")
}

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: ProductCategory,
    val emoji: String = "🌮",
    val rating: String = "4.9",
    val description: String = "Especialidad de la casa"
)

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val product: Product,
    val quantity: Int,
    val selectedModifiers: List<String> = emptyList(),
    val customNote: String = ""
) {
    val formattedNotes: String
        get() {
            val parts = mutableListOf<String>()
            if (selectedModifiers.isNotEmpty()) {
                parts.add(selectedModifiers.joinToString(", "))
            }
            if (customNote.isNotBlank()) {
                parts.add("Nota: ${customNote.trim()}")
            }
            return parts.joinToString(" • ")
        }
}

// Opciones de preparación específicas según categoría
val foodModifiersList = listOf(
    "Con todo",
    "Sin cebolla",
    "Sin cilantro",
    "Sin salsa",
    "Sin aguacate",
    "Salsa aparte"
)

val beverageModifiersList = listOf(
    "Al tiempo"
)

// Opciones de mesas y ubicación
val tablesList = listOf("Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4", "Mesa 5", "Mesa 6", "Mesa 7", "Barra")

// ==========================================
// DATOS DE EJEMPLO
// ==========================================

val sampleProducts = listOf(
    // --- SECCIÓN COMIDA ---
    Product("1", "Taco de Asada", 28.00, ProductCategory.COMIDA, "🥩", "4.9", "Carne asada al carbón"),
    Product("2", "Taco de Adobada", 25.00, ProductCategory.COMIDA, "🌮", "4.9", "Adobada al pastor tradicional"),
    Product("3", "Taco al Pastor", 25.00, ProductCategory.COMIDA, "🌮", "5.0", "Con piña y adobo casero"),
    Product("4", "Taco Suadero", 25.00, ProductCategory.COMIDA, "🌮", "4.8", "Suadero suave de res"),
    Product("5", "Taco de Tripa", 30.00, ProductCategory.COMIDA, "🌮", "4.9", "Tripa bien doradita"),
    Product("6", "Taco de Cabeza", 28.00, ProductCategory.COMIDA, "🌮", "4.7", "Cabeza de res al vapor"),
    Product("7", "Mulita Chica", 45.00, ProductCategory.COMIDA, "🫓", "4.8", "Doble tortilla con queso y carne"),
    Product("8", "Mulita Grande", 60.00, ProductCategory.COMIDA, "🫓", "4.9", "Mulita grande especial con queso"),
    Product("9", "Torta de Asada", 65.00, ProductCategory.COMIDA, "🥪", "4.8", "Telera caliente con carne asada"),
    Product("10", "Torta de Adobada", 65.00, ProductCategory.COMIDA, "🥪", "4.9", "Telera caliente con adobada y queso"),
    Product("11", "Torta Mixta", 75.00, ProductCategory.COMIDA, "🥪", "5.0", "Combinación asada y adobada"),

    // --- SECCIÓN BEBIDAS ---
    Product("12", "Coca-Cola Regular", 30.00, ProductCategory.BEBIDAS, "🥤", "4.9", "Refresco 355ml"),
    Product("13", "Coca-Cola Zero", 30.00, ProductCategory.BEBIDAS, "🥤", "4.8", "Refresco sin azúcar 355ml"),
    Product("14", "Manzana", 28.00, ProductCategory.BEBIDAS, "🍏", "4.7", "Refresco de manzana 355ml"),
    Product("15", "Naranja", 28.00, ProductCategory.BEBIDAS, "🍊", "4.7", "Refresco de naranja 355ml"),
    Product("16", "Sprite", 28.00, ProductCategory.BEBIDAS, "🍋", "4.8", "Refresco lima-limón 355ml"),
    Product("17", "Fresca", 28.00, ProductCategory.BEBIDAS, "🥤", "4.7", "Refresco toronja 355ml"),
    Product("18", "Agua Cebada", 25.00, ProductCategory.BEBIDAS, "🌾", "4.9", "Agua fresca de cebada artesanal"),
    Product("19", "Agua Horchata", 25.00, ProductCategory.BEBIDAS, "🥛", "5.0", "Agua de arroz y canela casera"),
    Product("20", "Agua Jamaica", 25.00, ProductCategory.BEBIDAS, "🌺", "4.9", "Agua de flor de jamaica natural"),
    Product("21", "Agua Natural", 20.00, ProductCategory.BEBIDAS, "💧", "4.6", "Agua purificada embotellada 600ml")
)

// ==========================================
// GENERADOR DE TICKET TÉRMICO (ESC/POS)
// ==========================================

fun generateThermalReceipt(
    cartItems: List<CartItem>,
    mesa: String = "Mesa 1",
    caja: String = "Caja #1",
    isTakeout: Boolean = false,
    ticketId: Int = 104
): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
    val currentDate = dateFormat.format(Date())
    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }
    val totalItems = cartItems.sumOf { it.quantity }

    val sb = StringBuilder()
    sb.appendLine("================================")
    if (isTakeout) {
        sb.appendLine("    *** ORDEN PARA LLEVAR ***   ")
    } else {
        sb.appendLine("     *** ORDEN COCINA ***      ")
    }
    sb.appendLine("================================")
    sb.appendLine("Ticket #: $ticketId")
    sb.appendLine("Fecha: $currentDate")
    if (isTakeout) {
        sb.appendLine("TIPO: 🛍️ PARA LLEVAR")
        sb.appendLine("Origen: $mesa | $caja")
    } else {
        sb.appendLine("TIPO: 🍽️ COMER AQUÍ")
        sb.appendLine("Ubicación: $mesa | $caja")
    }
    sb.appendLine("--------------------------------")
    sb.appendLine(String.format(Locale.US, "%-4s %-17s %9s", "CANT", "PRODUCTO", "SUBTOTAL"))
    sb.appendLine("--------------------------------")

    for (item in cartItems) {
        val line = String.format(
            Locale.US,
            " %-3dx %-17.17s $%8.2f",
            item.quantity,
            item.product.name,
            item.product.price * item.quantity
        )
        sb.appendLine(line)

        if (item.formattedNotes.isNotBlank()) {
            sb.appendLine("     >> ${item.formattedNotes}")
        }
    }

    sb.appendLine("--------------------------------")
    sb.appendLine("Total Productos: $totalItems")
    sb.appendLine(String.format(Locale.US, "TOTAL A PAGAR:        $%8.2f", totalAmount))
    sb.appendLine("================================")
    if (isTakeout) {
        sb.appendLine("  ¡EMPACAR PARA LLEVAR! 🛍️     ")
    } else {
        sb.appendLine("     ¡ENVIAR A PREPARACIÓN!     ")
    }
    sb.appendLine("================================")
    sb.appendLine("[ IMPRESORA ESC/POS - CORTE DE PAPEL ]")

    return sb.toString()
}

// ==========================================
// MAIN ACTIVITY
// ==========================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            POSTacOSTheme {
                POSMainScreen()
            }
        }
    }
}

// ==========================================
// INTERFAZ PRINCIPAL POS (PANTALLA DIVIDIDA ESTILO BURGER APP CONCEPT)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun POSMainScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf(ProductCategory.TODOS) }
    var selectedTable by remember { mutableStateOf("Mesa 1") }
    var isTakeout by remember { mutableStateOf(false) }

    val cartItems = remember { mutableStateListOf<CartItem>() }

    // Estados para los diálogos
    var productForModifierDialog by remember { mutableStateOf<Product?>(null) }
    var showTableSelectionDialog by remember { mutableStateOf(false) }
    var showReceiptPreviewDialog by remember { mutableStateOf(false) }
    var generatedTicketText by remember { mutableStateOf("") }

    // Cálculo del total en tiempo real
    val cartTotal by remember {
        derivedStateOf { cartItems.sumOf { it.product.price * it.quantity } }
    }
    val cartTotalItems by remember {
        derivedStateOf { cartItems.sumOf { it.quantity } }
    }

    // Agregar producto con modificadores
    fun addToCartWithModifiers(product: Product, modifiers: List<String>, note: String) {
        val index = cartItems.indexOfFirst {
            it.product.id == product.id &&
                    it.selectedModifiers == modifiers &&
                    it.customNote.trim() == note.trim()
        }

        if (index >= 0) {
            val existing = cartItems[index]
            cartItems[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            cartItems.add(
                CartItem(
                    product = product,
                    quantity = 1,
                    selectedModifiers = modifiers,
                    customNote = note.trim()
                )
            )
        }
    }

    fun updateQuantity(itemId: String, delta: Int) {
        val index = cartItems.indexOfFirst { it.id == itemId }
        if (index >= 0) {
            val current = cartItems[index]
            val newQty = current.quantity + delta
            if (newQty > 0) {
                cartItems[index] = current.copy(quantity = newQty)
            } else {
                cartItems.removeAt(index)
            }
        }
    }

    fun removeFromCart(itemId: String) {
        cartItems.removeAll { it.id == itemId }
    }

    fun clearCart() {
        cartItems.clear()
    }

    // Acción de enviar a cocina
    fun prepareKitchenSend() {
        if (cartItems.isEmpty()) {
            Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val ticket = generateThermalReceipt(
            cartItems = cartItems,
            mesa = selectedTable,
            isTakeout = isTakeout
        )
        generatedTicketText = ticket

        Log.d("POS_TICKET", "\n$ticket")
        showReceiptPreviewDialog = true
    }

    fun confirmSendToKitchen() {
        val itemsCount = cartTotalItems
        val totalFormatted = String.format(Locale.US, "$%.2f", cartTotal)
        val typeLabel = if (isTakeout) "🛍️ PARA LLEVAR" else "🍽️ $selectedTable"
        showReceiptPreviewDialog = false
        clearCart()
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                "¡Orden enviada a cocina! ($typeLabel • $itemsCount items - $totalFormatted)"
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🌮", fontSize = 22.sp)
                                }
                            }

                            Column {
                                Text(
                                    text = "POS TacOS",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Taquería Gourmet POS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Selector de Mesa / Ubicación en TopBar
                        Surface(
                            modifier = Modifier.clickable { showTableSelectionDialog = true },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "🪑 $selectedTable • Caja #1 ▾",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ==========================================
            // COLUMNA IZQUIERDA (65%): CATEGORÍAS + GRID BURGER APP
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.65f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pills de Selección de Categoría (Estilo Burger App)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ProductCategory.entries.toTypedArray()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                }

                // Filtrar productos según categoría seleccionada
                val filteredProducts = remember(selectedCategory) {
                    if (selectedCategory == ProductCategory.TODOS) {
                        sampleProducts
                    } else {
                        sampleProducts.filter { it.category == selectedCategory }
                    }
                }

                // Grid vertical de tarjetas de productos (Estilo Burger App UI)
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val itemsOfThisProductCount = cartItems.filter { it.product.id == product.id }
                            .sumOf { it.quantity }
                        ProductCardGourmet(
                            product = product,
                            inCartQuantity = itemsOfThisProductCount,
                            onClick = {
                                productForModifierDialog = product
                            }
                        )
                    }
                }
            }

            // Divisor vertical
            VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // ==========================================
            // COLUMNA DERECHA (35%): CARRITO / ORDEN ACTUAL (ESTILO DARK SLATE)
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.35f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header del Carrito
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Orden",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Orden Actual",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (cartItems.isNotEmpty()) {
                            IconButton(onClick = { clearCart() }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Limpiar carrito",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Controles de Asignación de Mesa y Opción Para Llevar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón de Selección de Mesa
                        Surface(
                            modifier = Modifier.clickable { showTableSelectionDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🪑 $selectedTable ▾",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Toggle / Chip Para Llevar
                        FilterChip(
                            selected = isTakeout,
                            onClick = { isTakeout = !isTakeout },
                            label = {
                                Text(
                                    text = if (isTakeout) "🛍️ PARA LLEVAR" else "🍽️ COMER AQUÍ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Lista de items en la orden
                    if (cartItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "🌮",
                                    fontSize = 54.sp
                                )
                                Text(
                                    text = "Orden vacía",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Toca un producto para agregar a la orden",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(cartItems, key = { it.id }) { item ->
                                CartItemRowGourmet(
                                    item = item,
                                    onQuantityChange = { delta -> updateQuantity(item.id, delta) },
                                    onRemove = { removeFromCart(item.id) }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Resumen de Totales
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Productos ($cartTotalItems)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", cartTotal),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", cartTotal),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // BOTÓN PRINCIPAL "ENVIAR A COCINA" (ESTILO BURGER APP CONCEPT)
                        Button(
                            onClick = { prepareKitchenSend() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            enabled = cartItems.isNotEmpty()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = if (isTakeout) "ENVIAR PARA LLEVAR 🛍️" else "ENVIAR A COCINA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO SELECCIÓN DE MESA
    if (showTableSelectionDialog) {
        TableSelectionDialog(
            currentTable = selectedTable,
            onDismiss = { showTableSelectionDialog = false },
            onSelectTable = { table ->
                selectedTable = table
                showTableSelectionDialog = false
            }
        )
    }

    // DIÁLOGO DE MODIFICADORES
    productForModifierDialog?.let { product ->
        ProductModifierDialog(
            product = product,
            onDismiss = { productForModifierDialog = null },
            onConfirm = { selectedMods, customNote ->
                addToCartWithModifiers(product, selectedMods, customNote)
                productForModifierDialog = null
            }
        )
    }

    // DIÁLOGO VISTA PREVIA TICKET TÉRMICO
    if (showReceiptPreviewDialog) {
        ThermalReceiptPreviewDialog(
            ticketText = generatedTicketText,
            isTakeout = isTakeout,
            onToggleTakeout = {
                isTakeout = it
                generatedTicketText = generateThermalReceipt(cartItems, mesa = selectedTable, isTakeout = isTakeout)
            },
            onDismiss = { showReceiptPreviewDialog = false },
            onConfirmPrint = { confirmSendToKitchen() }
        )
    }
}

// ==========================================
// COMPONENTE: DIÁLOGO SELECCIÓN DE MESA
// ==========================================

@Composable
fun TableSelectionDialog(
    currentTable: String,
    onDismiss: () -> Unit,
    onSelectTable: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🪑", fontSize = 22.sp)
                    }
                }
                Text(
                    text = "Seleccionar Mesa / Ubicación",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Asigna la ubicación del cliente para esta orden:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tablesList.forEach { table ->
                        val isSelected = table == currentTable
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectTable(table) },
                            label = {
                                Text(
                                    text = table,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

// ==========================================
// COMPONENTE: TARJETA DE PRODUCTO GOURMET (INSPIRADA EN BURGER APP CONCEPT UI)
// ==========================================

@Composable
fun ProductCardGourmet(
    product: Product,
    inCartQuantity: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (inCartQuantity > 0) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Fila Superior: Badge Rating o Popular + Badge Cantidad en Carrito
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = product.rating,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (inCartQuantity > 0) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = "${inCartQuantity}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Centro: Icono/Emoji destacado con contenedor circular ambient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = product.emoji,
                                fontSize = 32.sp
                            )
                        }
                    }
                }

                // Pie de Tarjeta: Título, Precio y Botón (+)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = String.format(Locale.US, "$%.2f", product.price),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Botón de Agregar estilo Burger App
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// COMPONENTE: FILA DE ELEMENTO EN EL CARRITO GOURMET
// ==========================================

@Composable
fun CartItemRowGourmet(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Info Producto (Emoji, Nombre, Modificadores, Precio)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = item.product.emoji, fontSize = 20.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.formattedNotes.isNotBlank()) {
                        Text(
                            text = item.formattedNotes,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = String.format(Locale.US, "$%.2f c/u", item.product.price),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Controles de Cantidad (- Qty +)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onQuantityChange(-1) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onQuantityChange(1) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Total del Item y Eliminar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "$%.2f", item.product.price * item.quantity),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 6.dp)
                )
                IconButton(
                    onClick = { onRemove() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// COMPONENTE: DIÁLOGO DE MODIFICADORES GOURMET
// ==========================================

@Composable
fun ProductModifierDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (List<String>, String) -> Unit
) {
    val selectedModifiers = remember { mutableStateListOf<String>() }
    var customNote by remember { mutableStateOf("") }
    var isEditingNote by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Evitar que el teclado virtual salte automáticamente al abrir el diálogo
    LaunchedEffect(product) {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    // Solicitar foco y mostrar teclado únicamente cuando el usuario seleccione la opción de escribir
    LaunchedEffect(isEditingNote) {
        if (isEditingNote) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    // Seleccionar opciones de preparación según la categoría del producto
    val currentModifiersList = remember(product.category) {
        if (product.category == ProductCategory.BEBIDAS) {
            beverageModifiersList
        } else {
            foodModifiersList
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = product.emoji, fontSize = 24.sp)
                    }
                }
                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format(Locale.US, "$%.2f", product.price),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Opciones de preparación:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Chips seleccionables
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    currentModifiersList.forEach { modifier ->
                        val isSelected = selectedModifiers.contains(modifier)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    selectedModifiers.remove(modifier)
                                } else {
                                    selectedModifiers.add(modifier)
                                }
                            },
                            label = { Text(modifier) },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Campo / Botón para notas especiales (No abre teclado hasta que el usuario toque la opción de escribir)
                if (!isEditingNote && customNote.isBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isEditingNote = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Escribir nota",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Toca aquí para escribir una nota especial...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = customNote,
                        onValueChange = { customNote = it },
                        label = { Text("Nota especial / Comentario") },
                        placeholder = { Text("Ej. Bien dorado, poca salsa...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedModifiers.toList(), customNote) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Agregar a la Orden", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

// ==========================================
// COMPONENTE: DIÁLOGO VISTA PREVIA DE TICKET TÉRMICO
// ==========================================

@Composable
fun ThermalReceiptPreviewDialog(
    ticketText: String,
    isTakeout: Boolean,
    onToggleTakeout: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onConfirmPrint: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Comanda / Ticket",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Chip de cambio de tipo en el diálogo
                FilterChip(
                    selected = isTakeout,
                    onClick = { onToggleTakeout(!isTakeout) },
                    label = {
                        Text(
                            text = if (isTakeout) "🛍️ PARA LLEVAR" else "🍽️ COMER AQUÍ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = null
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Vista previa enviada a la impresora térmica (ESC/POS):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Simulación de papel recibo
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFAF8F5),
                    border = BorderStroke(1.dp, Color(0xFFDDD7CD))
                ) {
                    Text(
                        text = ticketText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmPrint,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("IMPRIMIR Y LIMPIAR", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun POSMainScreenTabletPreview() {
    POSTacOSTheme {
        POSMainScreen()
    }
}
