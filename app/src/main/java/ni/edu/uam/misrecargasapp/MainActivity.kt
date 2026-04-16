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
import androidx.compose.material.icons.filled.AttachMoney
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisRecargasScreen() {

    var telefono by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var companiaSeleccionada by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val opcionesCompania = listOf("Claro", "Tigo", "Movistar")

    // Validaciones
    val telefonoValido = telefono.length == 8
    val montoValido = monto.isNotEmpty() && monto.toIntOrNull() != null && monto.toInt() > 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mis Recargas", fontWeight = FontWeight.ExtraBold)
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


            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 8) {
                        telefono = it
                    }
                },
                label = { Text("Número de teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                isError = telefono.isNotEmpty() && !telefonoValido,
                supportingText = {
                    if (telefono.isNotEmpty() && !telefonoValido) {
                        Text("Debe tener 8 dígitos")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )


            OutlinedTextField(
                value = monto,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        monto = it
                    }
                },
                label = { Text("Monto de la recarga") },
                prefix = { Text("C$ ") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
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
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = companiaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Compañía Telefónica") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
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


            Button(
                onClick = {
                    mostrarConfirmacion = true
                },
                enabled = telefonoValido && montoValido && companiaSeleccionada.isNotEmpty(),
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
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("¡Recarga Registrada!", fontWeight = FontWeight.Bold)
                            Text("Teléfono: $telefono")
                            Text("Monto: C$ $monto")
                            Text("Compañía: $companiaSeleccionada")
                        }
                    }
                }
            }
        }
    }
}