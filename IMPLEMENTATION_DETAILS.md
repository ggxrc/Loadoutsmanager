# Destiny 2 Loadouts Manager - Especificações de Implementação

## 1. Autenticação

### API Key
- **Localização**: `local.properties`
- **Formato**: `bungie.api.key=YOUR_API_KEY_HERE`
- **Uso**: Apenas API Key (sem OAuth2 Client ID/Secret por enquanto)
- **BuildConfig**: A chave é carregada automaticamente via `BuildConfigField`

### Fluxo de Autenticação
```kotlin
// Interceptor para adicionar API Key em todas as requisições
class BungieApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-Key", apiKey)
            .build()
        return chain.proceed(request)
    }
}
```

## 2. Estrutura de Dados

### Loadouts
Cada loadout pertence a um **personagem específico** e contém:

```kotlin
data class DestinyLoadout(
    val id: String,
    val name: String,
    val description: String?,
    val characterId: String,           // ID do personagem
    val subclassHash: Long,            // Para temas dinâmicos futuros
    val equipment: LoadoutEquipment,
    val isEquipped: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

data class LoadoutEquipment(
    val kineticWeapon: DestinyItem?,
    val energyWeapon: DestinyItem?,
    val powerWeapon: DestinyItem?,
    val helmet: DestinyItem?,
    val gauntlets: DestinyItem?,
    val chestArmor: DestinyItem?,
    val legArmor: DestinyItem?,
    val classItem: DestinyItem?
)
```

### Itens
```kotlin
data class DestinyItem(
    val itemInstanceId: String,
    val itemHash: Long,
    val bucketHash: Long,
    val location: ItemLocation,
    val perks: List<Long>,              // IDs dos perks ativos
    val stats: Map<String, Int>,        // Stats do item
    val cosmetics: ItemCosmetics?,      // Skins e shaders
    val characterId: String?            // Personagem que possui o item
)

data class ItemCosmetics(
    val ornamentHash: Long?,            // Skin da arma/armadura
    val shaderHash: Long?               // Shader aplicado
)
```

## 3. Fluxo de Busca de Equipamento

### Ordem de Prioridade
1. **Inventário do personagem alvo** - Primeira verificação
2. **Inventário de outros personagens** - Se não encontrado
3. **Cofre** - Última verificação

```kotlin
class EquipmentSearchService {
    fun findItem(
        itemHash: Long,
        targetCharacterId: String,
        allCharacterInventories: Map<String, List<DestinyItem>>,
        vaultInventory: List<DestinyItem>
    ): ItemSearchResult?
}
```

### Resultado da Busca
```kotlin
data class ItemSearchResult(
    val item: DestinyItem,
    val sourceLocation: SearchLocation,  // TARGET_CHARACTER, OTHER_CHARACTER, VAULT
    val sourceCharacterId: String?,
    val requiresTransfer: Boolean        // true se precisa transferir
)
```

## 4. Estratégia de Cache

### Loadouts
- **Cache Local**: Todos os loadouts salvos ficam armazenados localmente
- **Sincronização**: Carregados ao iniciar o app

### Itens do Cofre
- **Sempre da Internet**: Itens mostrados do cofre são sempre buscados da API
- **Sem Cache**: Garantir dados atualizados

### Manifesto
- Consultar documentação: https://bungie-net.github.io/
- Verificar melhor estratégia de cache para definições de itens

## 5. Sistema de Temas

### Tema Padrão - Sci-Fi Dark
```kotlin
LoadoutTheme.Default {
    primary = SciFiCyan (#00E5FF)
    background = SciFiDarkBackground (#0A0E1A)
    surface = SciFiDarkSurface (#151B2D)
    surfaceVariant = SciFiDarkSurfaceVariant (#1E2739)
}
```

### Temas por Subclasse (Futuro)
```kotlin
sealed class LoadoutTheme {
    object Default  // Sci-Fi Cyan/Dark
    object Solar    // Orange/Fire
    object Arc      // Blue/Electric
    object Void     // Purple/Dark
    object Stasis   // Cyan/Ice
    object Strand   // Green/Psychic
}
```

### Implementação Dinâmica
```kotlin
@Composable
fun getThemeForSubclass(subclassHash: Long?): LoadoutTheme {
    // Mapeamento futuro de subclass hashes
    return when (subclassHash) {
        // TODO: Mapear hashes reais da API
        else -> LoadoutTheme.Default
    }
}
```

## 6. Interface do Usuário

### Layout Principal
**Estados alternáveis:**
- **Lista**: Visualização em lista dos equipamentos
- **Grade**: Visualização em grade/grid

### Cartão de Item
**Estado Compacto** (Padrão):
- Foto do item (64x64dp)
- Contorno colorido (2dp, cor primária do tema)
- Nome do item

**Estado Expandido** (Ao clicar):
- Perks equipados
- Stats do item (com barras de progresso)
- Cosméticos:
  - Ornament/Skin
  - Shader
- Informações do item (ID, localização)

```kotlin
@Composable
fun ItemCard(
    item: DestinyItem,
    itemIconUrl: String?,
    itemName: String
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        onClick = { isExpanded = !isExpanded }
    ) {
        // Conteúdo compacto
        ItemIcon() + ItemName()
        
        // Detalhes expandíveis
        AnimatedVisibility(visible = isExpanded) {
            ItemDetails(item)
        }
    }
}
```

### Responsividade
- Adaptação automática para diferentes tamanhos de tela
- Grid responsivo para tablets
- Tipografia escalável

## 7. Próximos Passos

### Implementações Prioritárias
1. ✅ Modelos de dados atualizados (Loadout + Item + Cosmetics)
2. ✅ Sistema de temas dinâmico
3. ✅ Serviço de busca de equipamento
4. ✅ UI de cartão de item com expansão
5. ✅ Tela de detalhes de loadout
6. ⏳ Integração com API do Bungie
7. ⏳ Sistema de transferência de itens
8. ⏳ Banco de dados local (Room)
9. ⏳ Sincronização de loadouts

### Dependências Adicionadas
- Coil (2.5.0) - Para carregamento de imagens da API do Bungie
- Material3 - Para componentes modernos
- Compose Animation - Para transições suaves

## 8. Recursos da API do Bungie

### Endpoints Principais
- `/Platform/Destiny2/{membershipType}/Profile/{membershipId}/` - Perfil do jogador
- `/Platform/Destiny2/{membershipType}/Profile/{membershipId}/Character/{characterId}/` - Personagem específico
- `/Platform/Destiny2/Actions/Items/TransferItem/` - Transferir item
- `/Platform/Destiny2/Actions/Items/EquipItem/` - Equipar item

### Documentação
- Site oficial: https://bungie-net.github.io/
- Endpoint base: https://www.bungie.net/Platform

## 9. Arquitetura

```
app/
├── data/
│   ├── model/           # DestinyLoadout, DestinyItem, etc.
│   ├── api/             # BungieApiService, Interceptors
│   ├── repository/      # LoadoutRepository, BungieRepository
│   └── local/           # Room Database (futuro)
├── presentation/
│   ├── ui/              # Screens e Components
│   └── viewmodel/       # ViewModels
└── ui/
    └── theme/           # Temas, Cores, Tipografia
```

---

**Nota**: Este documento será atualizado conforme o desenvolvimento progride.
