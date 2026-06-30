package com.grinch.rivo4.view.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.collectAsState
import com.grinch.rivo4.controller.util.BlockedNumberMatchType
import com.grinch.rivo4.controller.util.BlockedNumberRule
import com.grinch.rivo4.controller.util.PreferenceManager
import com.grinch.rivo4.view.components.RivoExpressiveCard
import com.grinch.rivo4.view.components.RivoConfirmationDialog
import com.grinch.rivo4.view.components.RivoSelectListItem
import com.grinch.rivo4.view.components.RivoSwitchListItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun BlockedNumbersScreen(
    navigator: DestinationsNavigator
) {
    val prefs = koinInject<PreferenceManager>()
    val settingsState by prefs.settingsChanged.collectAsState()
    
    var blockMethod by remember(settingsState) { mutableStateOf(prefs.getInt(PreferenceManager.KEY_BLOCK_METHOD, 0)) }
    var logVisibility by remember(settingsState) { mutableStateOf(prefs.getInt(PreferenceManager.KEY_BLOCK_LOG_VISIBILITY, 0)) }
    var blockNotification by remember(settingsState) { mutableStateOf(prefs.getBoolean(PreferenceManager.KEY_BLOCK_NOTIFICATION, true)) }
    val blockedRules = remember(settingsState) { prefs.getBlockedNumberRules() }

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var rulePattern by rememberSaveable { mutableStateOf("") }
    var ruleType by rememberSaveable { mutableStateOf(BlockedNumberMatchType.STARTS_WITH.name) }
    var ruleToDelete by remember { mutableStateOf<BlockedNumberRule?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blocked Numbers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                RivoExpressiveCard {
                    RivoSelectListItem(
                        headline = "Block method",
                        supporting = "How blocked calls are handled",
                        leadingIcon = Icons.Outlined.Gavel,
                        options = listOf(
                            "Decline automatically" to 0,
                            "Ring silently" to 1
                        ),
                        selectedValue = blockMethod,
                        onValueChange = {
                            blockMethod = it
                            prefs.setInt(PreferenceManager.KEY_BLOCK_METHOD, it)
                        }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    RivoSelectListItem(
                        headline = "Log visibility",
                        supporting = "Show or hide blocked calls in recents",
                        leadingIcon = Icons.Outlined.Visibility,
                        options = listOf(
                            "Hide from logs" to 0,
                            "Show in logs" to 1
                        ),
                        selectedValue = logVisibility,
                        onValueChange = {
                            logVisibility = it
                            prefs.setInt(PreferenceManager.KEY_BLOCK_LOG_VISIBILITY, it)
                        }
                    )
                }
            }

            item {
                RivoExpressiveCard {
                    RivoSwitchListItem(
                        headline = "Block notifications",
                        supporting = "Show a low-priority alert for blocked calls",
                        leadingIcon = Icons.Outlined.NotificationsPaused,
                        checked = blockNotification,
                        onCheckedChange = {
                            blockNotification = it
                            prefs.setBoolean(PreferenceManager.KEY_BLOCK_NOTIFICATION, it)
                        }
                    )
                }
            }

            item {
                RivoExpressiveCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "App blocked rules",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Add prefixes like 0850 to block matching calls",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledTonalButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add")
                        }
                    }

                    if (blockedRules.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                            blockedRules.forEach { rule ->
                                Surface(
                                    onClick = { },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(44.dp),
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Outlined.Block, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                            }
                                        }
                                        Spacer(Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = rule.pattern,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = if (rule.matchType == BlockedNumberMatchType.STARTS_WITH) {
                                                    "Starts with"
                                                } else {
                                                    "Exact number"
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = {
                                            ruleToDelete = rule
                                            showDeleteDialog = true
                                        }) {
                                            Icon(Icons.Outlined.Delete, contentDescription = "Delete rule")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No app block rules yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Create a prefix rule to block entire number groups, such as 0850.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                rulePattern = ""
                ruleType = BlockedNumberMatchType.STARTS_WITH.name
            },
            title = { Text("Add block rule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = rulePattern,
                        onValueChange = { rulePattern = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Number or prefix") },
                        placeholder = { Text("0850") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            capitalization = KeyboardCapitalization.None
                        ),
                        singleLine = true
                    )

                    Text("Match type", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = ruleType == BlockedNumberMatchType.STARTS_WITH.name,
                            onClick = { ruleType = BlockedNumberMatchType.STARTS_WITH.name },
                            label = { Text("Starts with") }
                        )
                        FilterChip(
                            selected = ruleType == BlockedNumberMatchType.EXACT.name,
                            onClick = { ruleType = BlockedNumberMatchType.EXACT.name },
                            label = { Text("Exact") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                    prefs.addBlockedNumberRule(
                        BlockedNumberRule(
                            pattern = rulePattern,
                            matchType = BlockedNumberMatchType.valueOf(ruleType)
                        )
                    )
                    showAddDialog = false
                    rulePattern = ""
                    ruleType = BlockedNumberMatchType.STARTS_WITH.name
                    },
                    enabled = rulePattern.trim().isNotEmpty()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    rulePattern = ""
                    ruleType = BlockedNumberMatchType.STARTS_WITH.name
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog && ruleToDelete != null) {
        RivoConfirmationDialog(
            onDismissRequest = {
                showDeleteDialog = false
                ruleToDelete = null
            },
            onConfirm = {
                ruleToDelete?.let { prefs.removeBlockedNumberRule(it) }
                ruleToDelete = null
            },
            title = "Delete rule",
            message = "Remove this block rule from the app?",
            confirmLabel = "Delete",
            isDestructive = true,
            icon = Icons.Outlined.Delete
        )
    }
}
