# Exemplos de Uso - Componentes UI

## 1. ItemCard - Cartão de Item Expansível

### Uso Básico
```kotlin
@Composable
fun MyScreen() {
    val item = DestinyItem(
        itemInstanceId = "6917529876543210987",
        itemHash = 1234567890,
        bucketHash = 1498876634,  // Kinetic Weapons
        location = ItemLocation.INVENTORY,
        perks = listOf(123, 456, 789),
        stats = mapOf(
            "Impact" to 70,
            "Range" to 62,
            "Stability" to 54,
            "Reload Speed" to 48
        ),
        cosmetics = ItemCosmetics(
            ornamentHash = 111222333,
            shaderHash = 444555666
        ),
        characterId = "2305843009876543210"
    )
    
    ItemCard(
        item = item,
        itemIconUrl = null,  // Será usado com Bungie API
        itemName = "Ace of Spades"
    )
}
```

### Com Ícone do Bungie
```kotlin
ItemCard(
    item = item,
    itemIconUrl = "https://www.bungie.net/common/destiny2_content/icons/abc123.jpg",
    itemName = getItemNameFromManifest(item.itemHash)
)
```

## 2. LoadoutDetailScreen - Tela de Detalhes

### Navegação para Detalhes
```kotlin
@Composable
fun LoadoutListScreen(
    viewModel: LoadoutViewModel,
    onLoadoutClick: (DestinyLoadout) -> Unit
) {
    LazyColumn {
        items(viewModel.loadouts) { loadout ->
            LoadoutCard(
                loadout = loadout,
                onClick = { onLoadoutClick(loadout) }
            )
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    
    NavHost(navController, startDestination = "loadouts") {
        composable("loadouts") {
            LoadoutListScreen(
                viewModel = viewModel(),
                onLoadoutClick = { loadout ->
                    navController.navigate("loadout/${loadout.id}")
                }
            )
        }
        
        composable("loadout/{loadoutId}") { backStackEntry ->
            val loadoutId = backStackEntry.arguments?.getString("loadoutId")
            val loadout = viewModel.getLoadoutById(loadoutId)
            
            if (loadout != null) {
                LoadoutDetailScreen(
                    loadout = loadout,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
```

## 3. Sistema de Temas Dinâmico

### Tema Padrão (Sci-Fi Dark)
```kotlin
@Composable
fun App() {
    LoadoutsManagerTheme {  // Usa tema padrão
        MainScreen()
    }
}
```

### Tema por Subclasse
```kotlin
@Composable
fun LoadoutScreen(loadout: DestinyLoadout) {
    val theme = getThemeForSubclass(loadout.subclassHash)
    
    LoadoutsManagerTheme(theme = theme) {
        LoadoutDetailScreen(
            loadout = loadout,
            onBack = { }
        )
    }
}
```

### Alternar Tema Manualmente
```kotlin
@Composable
fun ThemeSelector() {
    var selectedTheme by remember { mutableStateOf(LoadoutTheme.Default) }
    
    Column {
        Button(onClick = { selectedTheme = LoadoutTheme.Solar }) {
            Text("Solar")
        }
        Button(onClick = { selectedTheme = LoadoutTheme.Arc }) {
            Text("Arc")
        }
        Button(onClick = { selectedTheme = LoadoutTheme.Void }) {
            Text("Void")
        }
        
        LoadoutsManagerTheme(theme = selectedTheme) {
            MyContent()
        }
    }
}
```

## 4. BungieItemIcon - Carregar Ícones

### Ícone de Item
```kotlin
@Composable
fun WeaponIcon(weapon: DestinyItem, weaponDefinition: ItemDefinition) {
    BungieItemIcon(
        iconPath = weaponDefinition.displayProperties.icon,
        contentDescription = weaponDefinition.displayProperties.name,
        size = 80.dp
    )
}
```

### Grid de Armas
```kotlin
@Composable
fun WeaponsGrid(weapons: List<DestinyItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(weapons) { weapon ->
            BungieItemIcon(
                iconPath = getIconPath(weapon.itemHash),
                contentDescription = getItemName(weapon.itemHash)
            )
        }
    }
}
```

### Emblema de Personagem
```kotlin
@Composable
fun CharacterHeader(character: DestinyCharacter) {
    Box(modifier = Modifier.fillMaxWidth()) {
        BungieEmblem(
            emblemPath = character.emblemPath,
            contentDescription = "Guardian emblem",
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
        ) {
            Text(
                text = character.classType,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Text(
                text = "Light ${character.light}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
```

## 5. EquipmentSearchService - Buscar Itens

### Buscar Um Item
```kotlin
val searchService = EquipmentSearchService()

suspend fun findWeapon(weaponHash: Long, targetCharacterId: String) {
    val allInventories = mapOf(
        "char1" to char1Inventory,
        "char2" to char2Inventory,
        "char3" to char3Inventory
    )
    
    val result = searchService.findItem(
        itemHash = weaponHash,
        targetCharacterId = targetCharacterId,
        allCharacterInventories = allInventories,
        vaultInventory = vaultItems
    )
    
    when (result?.sourceLocation) {
        SearchLocation.TARGET_CHARACTER -> {
            println("Item já está no personagem alvo!")
            equipItem(result.item)
        }
        SearchLocation.OTHER_CHARACTER -> {
            println("Item encontrado em outro personagem")
            transferItem(result.item, targetCharacterId)
            equipItem(result.item)
        }
        SearchLocation.VAULT -> {
            println("Item encontrado no cofre")
            transferFromVault(result.item, targetCharacterId)
            equipItem(result.item)
        }
        else -> {
            println("Item não encontrado!")
        }
    }
}
```

### Buscar Múltiplos Itens (Loadout Completo)
```kotlin
suspend fun prepareLoadout(loadout: DestinyLoadout) {
    val itemHashes = listOf(
        loadout.equipment.kineticWeapon?.itemHash,
        loadout.equipment.energyWeapon?.itemHash,
        loadout.equipment.powerWeapon?.itemHash,
        loadout.equipment.helmet?.itemHash,
        loadout.equipment.gauntlets?.itemHash,
        loadout.equipment.chestArmor?.itemHash,
        loadout.equipment.legArmor?.itemHash,
        loadout.equipment.classItem?.itemHash
    ).filterNotNull()
    
    val results = searchService.findMultipleItems(
        itemHashes = itemHashes,
        targetCharacterId = loadout.characterId,
        allCharacterInventories = getAllInventories(),
        vaultInventory = getVaultInventory()
    )
    
    results.forEach { (hash, result) ->
        if (result?.requiresTransfer == true) {
            println("Transferindo item $hash...")
            transferItem(result.item, loadout.characterId)
        }
    }
}
```

## 6. Criando um Loadout Completo

### ViewModel
```kotlin
class LoadoutViewModel : ViewModel() {
    private val _loadouts = MutableStateFlow<List<DestinyLoadout>>(emptyList())
    val loadouts: StateFlow<List<DestinyLoadout>> = _loadouts.asStateFlow()
    
    fun createLoadout(
        name: String,
        characterId: String,
        subclassHash: Long,
        selectedItems: Map<String, DestinyItem>  // slot -> item
    ) {
        val loadout = DestinyLoadout(
            id = UUID.randomUUID().toString(),
            name = name,
            characterId = characterId,
            subclassHash = subclassHash,
            equipment = LoadoutEquipment(
                kineticWeapon = selectedItems["kinetic"],
                energyWeapon = selectedItems["energy"],
                powerWeapon = selectedItems["power"],
                helmet = selectedItems["helmet"],
                gauntlets = selectedItems["gauntlets"],
                chestArmor = selectedItems["chest"],
                legArmor = selectedItems["legs"],
                classItem = selectedItems["classItem"]
            )
        )
        
        viewModelScope.launch {
            repository.saveLoadout(loadout)
            _loadouts.value = _loadouts.value + loadout
        }
    }
}
```

### UI de Criação
```kotlin
@Composable
fun CreateLoadoutScreen(viewModel: LoadoutViewModel) {
    var loadoutName by remember { mutableStateOf("") }
    val selectedItems = remember { mutableStateMapOf<String, DestinyItem>() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = loadoutName,
            onValueChange = { loadoutName = it },
            label = { Text("Loadout Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Text("Select Weapons", style = MaterialTheme.typography.titleMedium)
        
        ItemSlotSelector(
            slotName = "Kinetic",
            selectedItem = selectedItems["kinetic"],
            onItemSelected = { selectedItems["kinetic"] = it }
        )
        
        ItemSlotSelector(
            slotName = "Energy",
            selectedItem = selectedItems["energy"],
            onItemSelected = { selectedItems["energy"] = it }
        )
        
        // ... mais slots
        
        Button(
            onClick = {
                viewModel.createLoadout(
                    name = loadoutName,
                    characterId = currentCharacterId,
                    subclassHash = currentSubclassHash,
                    selectedItems = selectedItems
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Loadout")
        }
    }
}
```

## 7. Animações e Transições

### Crossfade entre Estados
```kotlin
@Composable
fun AdaptiveLoadoutView(loadout: DestinyLoadout) {
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = { viewMode = ViewMode.GRID }) {
            Icon(Icons.Default.GridView, "Grid")
        }
        IconButton(onClick = { viewMode = ViewMode.LIST }) {
            Icon(Icons.Default.List, "List")
        }
    }
    
    Crossfade(targetState = viewMode) { mode ->
        when (mode) {
            ViewMode.GRID -> GridView(loadout)
            ViewMode.LIST -> ListView(loadout)
        }
    }
}
```

### Loading State
```kotlin
@Composable
fun LoadoutScreen(viewModel: LoadoutViewModel) {
    val loadouts by viewModel.loadouts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            LazyColumn {
                items(loadouts) { loadout ->
                    ItemCard(loadout)
                }
            }
        }
    }
}
```

## 8. Boas Práticas

### Gerenciamento de Estado
```kotlin
// ViewModel com estado
class LoadoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoadoutUiState())
    val uiState: StateFlow<LoadoutUiState> = _uiState.asStateFlow()
    
    fun loadLoadouts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val loadouts = repository.getLoadouts()
                _uiState.update { 
                    it.copy(
                        loadouts = loadouts,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}

data class LoadoutUiState(
    val loadouts: List<DestinyLoadout> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Tratamento de Erros
```kotlin
@Composable
fun LoadoutScreen(viewModel: LoadoutViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    when {
        uiState.error != null -> {
            ErrorView(
                message = uiState.error!!,
                onRetry = { viewModel.loadLoadouts() }
            )
        }
        uiState.isLoading -> {
            LoadingView()
        }
        uiState.loadouts.isEmpty() -> {
            EmptyView(
                message = "No loadouts yet",
                onCreateNew = { /* Navigate to create */ }
            )
        }
        else -> {
            LoadoutList(loadouts = uiState.loadouts)
        }
    }
}
```

---

**Nota**: Estes exemplos assumem que a integração com a API do Bungie já está completa. Consulte `BUNGIE_API_INTEGRATION.md` para detalhes da implementação da API.
