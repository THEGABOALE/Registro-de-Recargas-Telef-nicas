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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
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
        "Claro" to listOf("Saldo", "MegaPack", "Internet", "Minutos", "SMS"),
        "Tigo" to listOf("Saldo", "SuperPack", "Internet", "Minutos", "Combo"),
        "Movistar" to listOf("Saldo", "Paquete de datos", "Minutos", "SMS", "Combo")
    )

    var paisSeleccionado by remember { mutableStateOf(paises[0]) }
    var telefono by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var companiaSeleccionada by remember { mutableStateOf("") }
    var tipoRecargaSeleccionado by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    var expandedPais by remember { mutableStateOf(false) }
    var expandedCompania by remember { mutableStateOf(false) }
    var expandedTipoRecarga by remember { mutableStateOf(false) }

    val tiposDisponibles = tiposPorCompania[companiaSeleccionada] ?: emptyList()

    val telefonoValido = telefono.length == paisSeleccionado.longitud
    val montoValido = monto.toIntOrNull()?.let { it > 0 } == true

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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Registra tus recargas y lleva control del dinero invertido.",
                style = MaterialTheme.typography.bodyMedium
            )

            // Selector de país
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

            // Número con código automático
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

            // Monto
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

            // Selector de compañía
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
                                expandedCompania = false
                            }
                        )
                    }
                }
            }

            // Selector de tipo de recarga
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    mostrarConfirmacion = true
                },
                enabled = telefonoValido &&
                        montoValido &&
                        companiaSeleccionada.isNotEmpty() &&
                        tipoRecargaSeleccionado.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("REGISTRAR RECARGA", fontWeight = FontWeight.Bold)
            }

            AnimatedVisibility(
                visible = mostrarConfirmacion,
                enter = fadeIn() + expandVertically()
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "¡Recarga registrada exitosamente!",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        HorizontalDivider()

                        Text("País: ${paisSeleccionado.nombre}")
                        Text("Número: ${paisSeleccionado.codigo} $telefono")
                        Text("Monto: C$ $monto")
                        Text("Compañía: $companiaSeleccionada")
                        Text("Tipo de recarga: $tipoRecargaSeleccionado")
                    }
                }
            }
        }
    }
}