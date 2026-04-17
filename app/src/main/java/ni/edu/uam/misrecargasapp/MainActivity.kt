package ni.edu.uam.misrecargasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ni.edu.uam.misrecargasapp.ui.theme.MisRecargasAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MisRecargasAppTheme {
                MisRecargasScreen()
            }
        }
    }
}

data class PaisTelefono(
    val nombre: String,
    val codigo: String,
    val longitud: Int,
    val ejemplo: String
)

data class PromocionRecarga(
    val id: Int,
    val compania: String,
    val nombre: String,
    val precio: Int,
    val categoria: String,
    val descripcion: String,
    val vigencia: String
)

data class HistorialRecarga(
    val telefonoCompleto: String,
    val pais: String,
    val compania: String,
    val tipo: String,
    val monto: Int,
    val detalle: String,
    val fecha: String
)

enum class VistaActual {
    REGISTRO, HISTORIAL, PROMOCIONES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisRecargasScreen() {

    val paises = listOf(
        PaisTelefono("Nicaragua", "+505", 8, "88887777"),
        PaisTelefono("Honduras", "+504", 8, "99998888"),
        PaisTelefono("Costa Rica", "+506", 8, "88887777")
    )

    val opcionesCompania = listOf("Claro", "Tigo", "Movistar")

    val tiposPorCompania = mapOf(
        "Claro" to listOf("Saldo", "Superpack", "Internet", "Minutos", "SMS"),
        "Tigo" to listOf("Saldo", "MegaPack", "Internet", "Minutos", "Combo"),
        "Movistar" to listOf("Saldo", "Paquete de datos", "Minutos", "SMS", "Combo")
    )

    // Promociones inspiradas en catálogos prepago reales vistos públicamente en Nicaragua
    val promociones = listOf(
        PromocionRecarga(
            id = 1,
            compania = "Claro",
            nombre = "Superpack 4 días",
            precio = 65,
            categoria = "Internet + SMS",
            descripcion = "4 GB + 100 SMS a Claro y otros operadores",
            vigencia = "4 días"
        ),
        PromocionRecarga(
            id = 2,
            compania = "Claro",
            nombre = "Superpack 6 días",
            precio = 90,
            categoria = "Internet + SMS",
            descripcion = "5 GB + 1000 SMS a Claro y otros operadores",
            vigencia = "6 días"
        ),
        PromocionRecarga(
            id = 3,
            compania = "Tigo",
            nombre = "MEGA2",
            precio = 40,
            categoria = "Internet + Apps",
            descripcion = "1.3 GB + apps incluidas",
            vigencia = "2 días"
        ),
        PromocionRecarga(
            id = 4,
            compania = "Tigo",
            nombre = "MEGA4",
            precio = 65,
            categoria = "Internet + Minutos",
            descripcion = "3 GB + 80 minutos + apps incluidas",
            vigencia = "4 días"
        ),
        PromocionRecarga(
            id = 5,
            compania = "Tigo",
            nombre = "MEGA6",
            precio = 90,
            categoria = "Internet + Minutos",
            descripcion = "4 GB + 10 minutos multiusos",
            vigencia = "6 días"
        ),
        PromocionRecarga(
            id = 6,
            compania = "Tigo",
            nombre = "MEGA7",
            precio = 120,
            categoria = "Internet + Minutos",
            descripcion = "6 GB + 15 minutos multiusos",
            vigencia = "7 días"
        ),
        PromocionRecarga(
            id = 7,
            compania = "Tigo",
            nombre = "MEGA15",
            precio = 220,
            categoria = "Internet + Minutos",
            descripcion = "8 GB + 20 minutos multiusos",
            vigencia = "15 días"
        ),
        // Para Movistar lo dejamos como ejemplos de demo, sin afirmar oferta oficial actual
        PromocionRecarga(
            id = 8,
            compania = "Movistar",
            nombre = "Combo Datos 3 días",
            precio = 50,
            categoria = "Internet + Minutos",
            descripcion = "1.5 GB + minutos nacionales",
            vigencia = "3 días"
        ),
        PromocionRecarga(
            id = 9,
            compania = "Movistar",
            nombre = "Combo Full 7 días",
            precio = 100,
            categoria = "Internet + Minutos + SMS",
            descripcion = "4 GB + minutos + SMS",
            vigencia = "7 días"
        )
    )

    var vistaActual by remember { mutableStateOf(VistaActual.REGISTRO) }

    var paisSeleccionado by remember { mutableStateOf(paises[0]) }
    var telefono by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var companiaSeleccionada by remember { mutableStateOf("") }
    var tipoRecargaSeleccionado by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var promocionSeleccionada by remember { mutableStateOf<PromocionRecarga?>(null) }

    var historial by remember { mutableStateOf(listOf<HistorialRecarga>()) }

    var expandedPais by remember { mutableStateOf(false) }
    var expandedCompania by remember { mutableStateOf(false) }
    var expandedTipoRecarga by remember { mutableStateOf(false) }

    val tiposDisponibles = tiposPorCompania[companiaSeleccionada] ?: emptyList()
    val promocionesFiltradas = if (companiaSeleccionada.isBlank()) {
        promociones
    } else {
        promociones.filter { it.compania == companiaSeleccionada }
    }

    val telefonoValido = telefono.length == paisSeleccionado.longitud
    val montoValido = monto.toIntOrNull()?.let { it > 0 } == true
    val usandoPromocion = promocionSeleccionada != null

    val puedeRegistrar = if (usandoPromocion) {
        telefonoValido &&
                companiaSeleccionada.isNotEmpty() &&
                promocionSeleccionada != null
    } else {
        telefonoValido &&
                montoValido &&
                companiaSeleccionada.isNotEmpty() &&
                tipoRecargaSeleccionado.isNotEmpty()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mis Recargas",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = vistaActual == VistaActual.REGISTRO,
                    onClick = { vistaActual = VistaActual.REGISTRO },
                    label = { Text("Registrar") },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    }
                )

                FilterChip(
                    selected = vistaActual == VistaActual.HISTORIAL,
                    onClick = { vistaActual = VistaActual.HISTORIAL },
                    label = { Text("Historial") },
                    leadingIcon = {
                        Icon(Icons.Default.History, contentDescription = null)
                    }
                )

                FilterChip(
                    selected = vistaActual == VistaActual.PROMOCIONES,
                    onClick = { vistaActual = VistaActual.PROMOCIONES },
                    label = { Text("Promociones") },
                    leadingIcon = {
                        Icon(Icons.Default.LocalOffer, contentDescription = null)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (vistaActual) {
                VistaActual.REGISTRO -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (usandoPromocion)
                                "Registrá una promoción seleccionada."
                            else
                                "Registrá tus recargas y lleva control del dinero invertido.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedPais,
                            onExpandedChange = { expandedPais = !expandedPais },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = paisSeleccionado.nombre,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("País") },
                                leadingIcon = {
                                    Icon(Icons.Default.Public, contentDescription = null)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPais)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = expandedPais,
                                onDismissRequest = { expandedPais = false }
                            ) {
                                paises.forEach { pais ->
                                    DropdownMenuItem(
                                        text = { Text("${pais.nombre} (${pais.codigo})") },
                                        onClick = {
                                            paisSeleccionado = pais
                                            telefono = ""
                                            mostrarConfirmacion = false
                                            expandedPais = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = telefono,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() } && it.length <= paisSeleccionado.longitud) {
                                    telefono = it
                                    mostrarConfirmacion = false
                                }
                            },
                            label = { Text("Número de teléfono") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null)
                            },
                            prefix = {
                                Text("${paisSeleccionado.codigo} ")
                            },
                            isError = telefono.isNotEmpty() && !telefonoValido,
                            supportingText = {
                                if (telefono.isEmpty()) {
                                    Text("Formato esperado: ${paisSeleccionado.ejemplo}")
                                } else if (!telefonoValido) {
                                    Text("Debe tener ${paisSeleccionado.longitud} dígitos")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandedCompania,
                            onExpandedChange = { expandedCompania = !expandedCompania },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = companiaSeleccionada,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Compañía Telefónica") },
                                leadingIcon = {
                                    Icon(Icons.Default.SimCard, contentDescription = null)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCompania)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                singleLine = true
                            )

                            ExposedDropdownMenu(
                                expanded = expandedCompania,
                                onDismissRequest = { expandedCompania = false }
                            ) {
                                opcionesCompania.forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            companiaSeleccionada = opcion
                                            tipoRecargaSeleccionado = ""
                                            mostrarConfirmacion = false

                                            if (promocionSeleccionada != null &&
                                                promocionSeleccionada?.compania != opcion
                                            ) {
                                                promocionSeleccionada = null
                                                monto = ""
                                            }

                                            expandedCompania = false
                                        }
                                    )
                                }
                            }
                        }

                        if (!usandoPromocion) {
                            OutlinedTextField(
                                value = monto,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() }) {
                                        monto = it
                                        mostrarConfirmacion = false
                                    }
                                },
                                label = { Text("Monto de la recarga") },
                                leadingIcon = {
                                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                                },
                                prefix = { Text("C$ ") },
                                isError = monto.isNotEmpty() && !montoValido,
                                supportingText = {
                                    if (monto.isNotEmpty() && !montoValido) {
                                        Text("Ingrese un monto válido")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            ExposedDropdownMenuBox(
                                expanded = expandedTipoRecarga,
                                onExpandedChange = {
                                    if (companiaSeleccionada.isNotEmpty()) {
                                        expandedTipoRecarga = !expandedTipoRecarga
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = tipoRecargaSeleccionado,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Tipo de recarga") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Wifi, contentDescription = null)
                                    },
                                    placeholder = {
                                        if (companiaSeleccionada.isEmpty()) {
                                            Text("Seleccione primero la compañía")
                                        }
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipoRecarga)
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    enabled = companiaSeleccionada.isNotEmpty(),
                                    singleLine = true
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedTipoRecarga,
                                    onDismissRequest = { expandedTipoRecarga = false }
                                ) {
                                    tiposDisponibles.forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text(tipo) },
                                            onClick = {
                                                tipoRecargaSeleccionado = tipo
                                                mostrarConfirmacion = false
                                                expandedTipoRecarga = false
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Promoción seleccionada",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Compañía: ${promocionSeleccionada?.compania}")
                                    Text("Promo: ${promocionSeleccionada?.nombre}")
                                    Text("Precio: C$ ${promocionSeleccionada?.precio}")
                                    Text("Incluye: ${promocionSeleccionada?.descripcion}")
                                    Text("Vigencia: ${promocionSeleccionada?.vigencia}")

                                    TextButton(
                                        onClick = {
                                            promocionSeleccionada = null
                                            monto = ""
                                            tipoRecargaSeleccionado = ""
                                            mostrarConfirmacion = false
                                        }
                                    ) {
                                        Text("Quitar promoción")
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val fechaActual = SimpleDateFormat(
                                    "dd/MM/yyyy hh:mm a",
                                    Locale.getDefault()
                                ).format(Date())

                                val detalle = if (usandoPromocion) {
                                    "${promocionSeleccionada?.nombre} - ${promocionSeleccionada?.descripcion}"
                                } else {
                                    tipoRecargaSeleccionado
                                }

                                val montoFinal = if (usandoPromocion) {
                                    promocionSeleccionada?.precio ?: 0
                                } else {
                                    monto.toIntOrNull() ?: 0
                                }

                                val tipoFinal = if (usandoPromocion) {
                                    "Promoción"
                                } else {
                                    tipoRecargaSeleccionado
                                }

                                historial = listOf(
                                    HistorialRecarga(
                                        telefonoCompleto = "${paisSeleccionado.codigo} $telefono",
                                        pais = paisSeleccionado.nombre,
                                        compania = companiaSeleccionada,
                                        tipo = tipoFinal,
                                        monto = montoFinal,
                                        detalle = detalle,
                                        fecha = fechaActual
                                    )
                                ) + historial

                                mostrarConfirmacion = true

                                telefono = ""
                                monto = ""
                                tipoRecargaSeleccionado = ""
                                promocionSeleccionada = null
                            },
                            enabled = puedeRegistrar,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                if (usandoPromocion) "REGISTRAR PROMOCIÓN"
                                else "REGISTRAR RECARGA",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        AnimatedVisibility(
                            visible = mostrarConfirmacion,
                            enter = fadeIn() + expandVertically()
                        ) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "¡Registro realizado con éxito!",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            if (usandoPromocion)
                                                "La promoción fue registrada correctamente."
                                            else
                                                "La recarga fue registrada correctamente."
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                VistaActual.HISTORIAL -> {
                    if (historial.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null)
                                Text(
                                    text = "Aún no hay recargas registradas.",
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Cuando registres una recarga o promoción, aparecerá aquí.")
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(historial) { item ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.History,
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = item.tipo,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }

                                        HorizontalDivider()

                                        Text("Número: ${item.telefonoCompleto}")
                                        Text("País: ${item.pais}")
                                        Text("Compañía: ${item.compania}")
                                        Text("Monto: C$ ${item.monto}")
                                        Text("Detalle: ${item.detalle}")

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(item.fecha)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                VistaActual.PROMOCIONES -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Seleccioná una promoción para registrarla más rápido.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (companiaSeleccionada.isBlank()) {
                            AssistChip(
                                onClick = {},
                                label = { Text("Mostrando promociones de todas las compañías") }
                            )
                        } else {
                            AssistChip(
                                onClick = {},
                                label = { Text("Filtrando por: $companiaSeleccionada") }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(promocionesFiltradas) { promo ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = promo.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("Compañía: ${promo.compania}")
                                        Text("Categoría: ${promo.categoria}")
                                        Text("Precio: C$ ${promo.precio}")
                                        Text("Incluye: ${promo.descripcion}")
                                        Text("Vigencia: ${promo.vigencia}")

                                        Button(
                                            onClick = {
                                                promocionSeleccionada = promo
                                                companiaSeleccionada = promo.compania
                                                monto = promo.precio.toString()
                                                tipoRecargaSeleccionado = "Promoción"
                                                mostrarConfirmacion = false
                                                vistaActual = VistaActual.REGISTRO
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Seleccionar promoción")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}