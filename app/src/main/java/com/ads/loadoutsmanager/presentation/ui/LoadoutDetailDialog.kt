package com.ads.loadoutsmanager.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.ads.loadoutsmanager.data.model.DestinyItem
import com.ads.loadoutsmanager.data.model.DestinyLoadout
import com.ads.loadoutsmanager.presentation.ui.theme.DestinyDarkGray
import com.ads.loadoutsmanager.presentation.ui.theme.DestinyGold
import com.ads.loadoutsmanager.presentation.ui.theme.DestinyMediumGray

/**
 * Dialog para exibir detalhes completos de um loadout
 * Ordem: Resumo > Subclasse > Armas (Cinética/Estase, Vácuo/Solar/Arco, Pesada) > Armadura > Bônus de conjunto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadoutDetailDialog(
    loadout: DestinyLoadout,
    onDismiss: () -> Unit,
    onEquip: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(16.dp),
            color = DestinyDarkGray
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                LoadoutDetailHeader(
                    loadoutName = loadout.name,
                    onDismiss = onDismiss
                )

                // Content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumo completo do loadout
                    item {
                        LoadoutSummarySection(
                            loadout = loadout
                        )
                    }

                    // Subclasse (em breve)
                    item {
                        SubclassSection(
                            subclass = loadout.subclass
                        )
                    }

                    // Slot 1: Cinética, Estase, Filamento
                    item {
                        WeaponSlotSection(
                            title = "CINÉTICA / ESTASE / FILAMENTO",
                            slotType = WeaponSlotType.KINETIC,
                            items = loadout.equipment
                        )
                    }

                    // Slot 2: Vácuo, Solar, Arco
                    item {
                        WeaponSlotSection(
                            title = "VÁCUO / SOLAR / ARCO",
                            slotType = WeaponSlotType.ENERGY,
                            items = loadout.equipment
                        )
                    }

                    // Slot 3: Munição Pesada
                    item {
                        WeaponSlotSection(
                            title = "MUNIÇÃO PESADA",
                            slotType = WeaponSlotType.POWER,
                            items = loadout.equipment
                        )
                    }

                    // 5 slots de armadura
                    item {
                        ArmorSection(
                            items = loadout.equipment
                        )
                    }

                    // Bônus de conjunto (se houver)
                    item {
                        ArmorSetBonusSection(
                            items = loadout.equipment
                        )
                    }
                }

                // Action buttons
                LoadoutDetailActions(
                    isEquipped = loadout.isEquipped,
                    onEquip = onEquip,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun LoadoutDetailHeader(
    loadoutName: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DestinyMediumGray,
                        DestinyDarkGray
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = loadoutName,
                style = MaterialTheme.typography.headlineMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun LoadoutSummarySection(
    loadout: DestinyLoadout
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "RESUMO DO CONJUNTO",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold
            )

            loadout.description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "30",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mobilidade",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Column {
                    Text(
                        text = "42",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Resistência",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Column {
                    Text(
                        text = "186",
                        style = MaterialTheme.typography.headlineSmall,
                        color = DestinyGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Recuperação",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "146",
                        style = MaterialTheme.typography.headlineSmall,
                        color = DestinyGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Disciplina",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Column {
                    Text(
                        text = "49",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Intelecto",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Column {
                    Text(
                        text = "45",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Força",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun SubclassSection(
    subclass: com.ads.loadoutsmanager.data.model.SubclassInfo?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "SUBCLASSE",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (subclass != null) {
                Text(
                    text = "Andarilho do Vácuo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            } else {
                Text(
                    text = "Em breve",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

enum class WeaponSlotType {
    KINETIC, ENERGY, POWER
}

@Composable
private fun WeaponSlotSection(
    title: String,
    slotType: WeaponSlotType,
    items: List<DestinyItem>
) {
    // Bucket hashes para cada tipo de arma
    val bucketHash = when (slotType) {
        WeaponSlotType.KINETIC -> 1498876634L
        WeaponSlotType.ENERGY -> 2465295065L
        WeaponSlotType.POWER -> 953998645L
    }
    
    val weapon = items.firstOrNull { it.bucketHash == bucketHash }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = DestinyGold,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (weapon != null) {
                ItemRow(item = weapon)
            } else {
                Text(
                    text = "Nenhuma arma equipada",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ArmorSection(
    items: List<DestinyItem>
) {
    val armorBuckets = listOf(
        3448274439L to "CAPACETE",
        3551918588L to "MANOPLAS",
        14239492L to "PEITORAL",
        20886954L to "PERNEIRAS",
        1585787867L to "INSÍGNIA DE CLASSE"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DestinyMediumGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ARMADURA",
                style = MaterialTheme.typography.titleMedium,
                color = DestinyGold,
                fontWeight = FontWeight.Bold
            )
            
            armorBuckets.forEach { (bucketHash, slotName) ->
                val armor = items.firstOrNull { it.bucketHash == bucketHash }
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = slotName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                    
                    if (armor != null) {
                        ItemRow(item = armor)
                    } else {
                        Text(
                            text = "Vazio",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: DestinyItem
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DestinyDarkGray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone do item
        if (item.iconUrl != null) {
            AsyncImage(
                model = "https://www.bungie.net${item.iconUrl}",
                contentDescription = "Item icon",
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            )
        }
        
        // Nome do item (placeholder - seria buscado da API)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Item ${item.itemHash}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Power: ???",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ArmorSetBonusSection(
    items: List<DestinyItem>
) {
    // Placeholder - lógica de detecção de bônus seria implementada
    val hasSetBonus = false
    
    if (hasSetBonus) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = DestinyMediumGray
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "BÔNUS DE CONJUNTO",
                    style = MaterialTheme.typography.titleMedium,
                    color = DestinyGold,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Nenhum bônus de conjunto ativo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun LoadoutDetailActions(
    isEquipped: Boolean,
    onEquip: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DestinyMediumGray,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
            ) {
                Text("EXCLUIR")
            }
            
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DestinyGold
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DestinyGold)
            ) {
                Text("EDITAR")
            }
            
            Button(
                onClick = onEquip,
                enabled = !isEquipped,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DestinyGold,
                    contentColor = Color.Black
                )
            ) {
                Text(if (isEquipped) "EQUIPADO" else "EQUIPAR")
            }
        }
    }
}
