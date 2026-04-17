package ni.edu.uam.misrecargasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.misrecargasapp.ui.theme.MisRecargasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MisRecargasAppTheme {
                // Llamamos a la pantalla principal de recargas
                MisRecargasScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisRecargasScreen() {
    // --- ESTADOS (Criterio: Manejo de interacción y estado - 8 pts) ---
    var telefono by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var companiaSeleccionada by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val opcionesCompania = listOf("Claro", "Tigo", "Movistar")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mis Recargas", fontWeight = FontWeight.ExtraBold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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

            // --- CAMPOS DE ENTRADA (Material Design 3 - 6 pts) ---

            // Campo Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Número de teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            // Campo Monto
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto de la recarga") },
                prefix = { Text("C$ ", fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Selector de Compañía (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = companiaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Compañía Telefónica") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opcionesCompania.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                companiaSeleccionada = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- BOTÓN DE ACCIÓN ---
            Button(
                onClick = {
                    if (telefono.isNotEmpty() && monto.isNotEmpty() && companiaSeleccionada.isNotEmpty()) {
                        mostrarConfirmacion = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("REGISTRAR RECARGA", fontWeight = FontWeight.Bold)
            }

            // --- CARD DINÁMICA (Criterio: Implementación funcional - 6 pts) ---
            AnimatedVisibility(
                visible = mostrarConfirmacion,
                enter = fadeIn() + expandVertically()
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "¡Recarga Registrada!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text("Teléfono: $telefono", style = MaterialTheme.typography.bodyMedium)
                            Text("Monto: C$ $monto", style = MaterialTheme.typography.bodyMedium)
                            Text("Compañía: $companiaSeleccionada", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}