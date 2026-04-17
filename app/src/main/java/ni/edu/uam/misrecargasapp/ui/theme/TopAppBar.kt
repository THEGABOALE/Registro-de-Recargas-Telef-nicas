
package ni.edu.uam.misrecargasapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ni.edu.uam.misrecargasapp.VistaActual

// ── Tipos de recarga (chips bajo el contenido) ────────────────────────────────

enum class TipoRecargaTab(
    val etiqueta: String,
    val icono: ImageVector,
    val color: Color
) {
    TODO      ("Todo",      Icons.Default.GridView,     RechargeBlue40),
    SALDO     ("Saldo",     Icons.Default.AttachMoney,  ColorSaldo),
    INTERNET  ("Internet",  Icons.Default.Wifi,         ColorInternet),
    MINUTOS   ("Minutos",   Icons.Default.Phone,        ColorMinutos),
    SMS       ("SMS",       Icons.Default.Sms,          ColorSMS),
    COMBO     ("Combo",     Icons.Default.AllInclusive, ColorCombo),
    PROMOCION ("Promoción", Icons.Default.LocalOffer,   ColorPromocion)
}

// ── TopAppBar: título + navegación Registrar / Historial / Promociones ────────

@Composable
fun RecargasTopAppBar(
    vistaActual: VistaActual,
    onVistaSeleccionada: (VistaActual) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier        = modifier.fillMaxWidth(),
        color           = MaterialTheme.colorScheme.primary,
        shadowElevation = 6.dp
    ) {
        Column {
            // Título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Bolt,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onPrimary,
                    modifier           = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Mis Recargas",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color      = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Navegación principal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val items = listOf(
                    Triple(VistaActual.REGISTRO,    "Registrar",   Icons.Default.AttachMoney),
                    Triple(VistaActual.HISTORIAL,   "Historial",   Icons.Default.History),
                    Triple(VistaActual.PROMOCIONES, "Promociones", Icons.Default.LocalOffer)
                )
                items.forEach { (vista, label, icono) ->
                    val sel = vistaActual == vista
                    FilterChip(
                        selected    = sel,
                        onClick     = { onVistaSeleccionada(vista) },
                        label       = {
                            Text(label, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                        },
                        leadingIcon = {
                            Icon(icono, contentDescription = label, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor   = MaterialTheme.colorScheme.onPrimary,
                            selectedLabelColor       = MaterialTheme.colorScheme.primary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            containerColor           = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            labelColor               = MaterialTheme.colorScheme.onPrimary,
                            iconColor                = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled             = true,
                            selected            = sel,
                            borderColor         = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.35f),
                            selectedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}
