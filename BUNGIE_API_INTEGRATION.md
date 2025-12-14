# Guia de Integração - API do Bungie

## 1. Configuração Inicial

### API Key
1. Coloque sua API Key em `local.properties`:
```properties
bungie.api.key=SUA_API_KEY_AQUI
```

2. A chave é automaticamente injetada via BuildConfig:
```kotlin
BuildConfig.BUNGIE_API_KEY
```

## 2. Autenticação

### Apenas API Key (Modo Atual)
Todas as requisições incluem o header:
```
X-API-Key: SUA_API_KEY
```

### OAuth2 (Futuro)
Para operações de escrita (transferir, equipar), será necessário:
- Client ID
- Client Secret
- OAuth2 Authorization Flow

## 3. Endpoints Principais

### Base URL
```
https://www.bungie.net/Platform
```

### Buscar Perfil do Jogador
```
GET /Destiny2/{membershipType}/Profile/{membershipId}/
```

**Query Parameters:**
- `components`: Lista de componentes a retornar (separados por vírgula)

**Componentes Importantes:**
- `100` - Profiles
- `102` - Characters
- `103` - Character Inventories
- `201` - Character Equipment
- `205` - Character Loadouts (se disponível)
- `102` - Profile Inventory (Vault)

**Exemplo:**
```kotlin
@GET("Destiny2/{membershipType}/Profile/{membershipId}/")
suspend fun getProfile(
    @Path("membershipType") membershipType: Int,
    @Path("membershipId") membershipId: String,
    @Query("components") components: String = "100,102,103,201,205"
): Response<BungieResponse<DestinyProfileResponse>>
```

### Buscar Personagem Específico
```
GET /Destiny2/{membershipType}/Profile/{membershipId}/Character/{characterId}/
```

**Componentes:**
- `200` - Character Activities
- `201` - Character Equipment
- `205` - Character Loadouts

### Transferir Item
```
POST /Destiny2/Actions/Items/TransferItem/
```

**Request Body:**
```json
{
  "itemReferenceHash": 123456789,
  "stackSize": 1,
  "transferToVault": false,
  "itemId": "6917529876543210987",
  "characterId": "2305843009876543210",
  "membershipType": 3
}
```

### Equipar Item
```
POST /Destiny2/Actions/Items/EquipItem/
```

**Request Body:**
```json
{
  "itemId": "6917529876543210987",
  "characterId": "2305843009876543210",
  "membershipType": 3
}
```

## 4. Manifesto (Definições)

### Buscar Manifesto
```
GET /Destiny2/Manifest/
```

Retorna URLs para baixar o banco de dados SQLite com todas as definições.

### Definições Importantes
- **InventoryItemDefinition**: Nomes, descrições, ícones de itens
- **StatDefinition**: Nomes dos stats
- **PerkDefinition**: Descrição de perks
- **SocketCategoryDefinition**: Categorias de slots

### Exemplo de Uso
```kotlin
// 1. Baixar manifesto
val manifest = api.getManifest()

// 2. Baixar banco SQLite
val dbUrl = manifest.mobileWorldContentPaths["pt-br"] // ou "en"
downloadAndExtractDb(dbUrl)

// 3. Consultar definição
val itemDef = db.query("SELECT json FROM DestinyInventoryItemDefinition WHERE id = ?", itemHash)
```

## 5. Estrutura de Resposta

### BungieResponse Wrapper
```kotlin
data class BungieResponse<T>(
    val Response: T?,
    val ErrorCode: Int,
    val ThrottleSeconds: Int,
    val ErrorStatus: String,
    val Message: String,
    val MessageData: Map<String, String>?
)
```

### Profile Response
```kotlin
data class DestinyProfileResponse(
    val profile: SingleComponentResponse<DestinyProfileComponent>?,
    val characters: DictionaryComponentResponse<DestinyCharacterComponent>?,
    val characterInventories: DictionaryComponentResponse<DestinyInventoryComponent>?,
    val characterEquipment: DictionaryComponentResponse<DestinyInventoryComponent>?,
    val profileInventory: SingleComponentResponse<DestinyInventoryComponent>?, // Vault
    val itemComponents: ItemComponentsResponse?
)
```

## 6. Membership Types

```kotlin
enum class BungieMembershipType(val value: Int) {
    NONE(0),
    XBOX(1),
    PSN(2),
    STEAM(3),
    BLIZZARD(4),
    STADIA(5),
    EGS(6),  // Epic Games Store
    BUNGIE_NEXT(254),
    ALL(-1)
}
```

## 7. Exemplo Completo

### Buscar Inventário de um Personagem
```kotlin
suspend fun getCharacterInventory(
    membershipType: Int,
    membershipId: String,
    characterId: String
): Result<List<DestinyItem>> {
    return try {
        val response = api.getProfile(
            membershipType = membershipType,
            membershipId = membershipId,
            components = "103,201" // Inventory + Equipment
        )
        
        if (response.isSuccessful && response.body()?.ErrorCode == 1) {
            val profile = response.body()!!.Response
            val inventory = profile.characterInventories?.data?.get(characterId)
            val equipment = profile.characterEquipment?.data?.get(characterId)
            
            val allItems = mutableListOf<DestinyItem>()
            
            inventory?.items?.forEach { item ->
                allItems.add(
                    DestinyItem(
                        itemInstanceId = item.itemInstanceId,
                        itemHash = item.itemHash,
                        bucketHash = item.bucketHash,
                        location = ItemLocation.INVENTORY,
                        characterId = characterId
                    )
                )
            }
            
            equipment?.items?.forEach { item ->
                allItems.add(
                    DestinyItem(
                        itemInstanceId = item.itemInstanceId,
                        itemHash = item.itemHash,
                        bucketHash = item.bucketHash,
                        location = ItemLocation.EQUIPPED,
                        characterId = characterId
                    )
                )
            }
            
            Result.success(allItems)
        } else {
            Result.failure(Exception("Bungie API Error: ${response.body()?.Message}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

## 8. Fluxo de Equipar Loadout

```kotlin
suspend fun equipLoadout(
    loadout: DestinyLoadout,
    membershipType: Int,
    membershipId: String
) {
    val targetCharacterId = loadout.characterId
    
    // 1. Buscar todos os inventários
    val allInventories = getAllCharacterInventories(membershipType, membershipId)
    val vaultInventory = getVaultInventory(membershipType, membershipId)
    
    // 2. Para cada item do loadout
    loadout.equipment.getAllItems().forEach { requiredItem ->
        // 3. Buscar item usando EquipmentSearchService
        val searchResult = equipmentSearchService.findItem(
            itemHash = requiredItem.itemHash,
            targetCharacterId = targetCharacterId,
            allCharacterInventories = allInventories,
            vaultInventory = vaultInventory
        )
        
        // 4. Transferir se necessário
        if (searchResult?.requiresTransfer == true) {
            transferItem(
                itemId = searchResult.item.itemInstanceId,
                targetCharacterId = targetCharacterId,
                fromVault = searchResult.sourceLocation == SearchLocation.VAULT
            )
        }
        
        // 5. Equipar
        equipItem(
            itemId = searchResult!!.item.itemInstanceId,
            characterId = targetCharacterId
        )
    }
}
```

## 9. Rate Limits

A API do Bungie tem rate limits:
- **25 requisições por segundo** por IP
- **200 requisições por 10 segundos** por aplicação

### Estratégia de Throttling
```kotlin
class BungieApiThrottler {
    private val requestTimes = mutableListOf<Long>()
    private val maxRequestsPerSecond = 20 // Margem de segurança
    
    suspend fun throttle() {
        val now = System.currentTimeMillis()
        requestTimes.removeAll { it < now - 1000 }
        
        if (requestTimes.size >= maxRequestsPerSecond) {
            val waitTime = 1000 - (now - requestTimes.first())
            delay(waitTime)
        }
        
        requestTimes.add(System.currentTimeMillis())
    }
}
```

## 10. Tratamento de Erros

### Error Codes Comuns
- `1` - Success
- `5` - SystemDisabled
- `1618` - DestinyItemNotFound
- `1619` - DestinyCharacterNotLoggedInJoinAllowed
- `1623` - DestinyItemActionForbidden (item locked)
- `1627` - DestinyNoRoomInDestination

### Implementação
```kotlin
sealed class BungieApiResult<T> {
    data class Success<T>(val data: T) : BungieApiResult<T>()
    data class Error<T>(val code: Int, val message: String) : BungieApiResult<T>()
    data class NetworkError<T>(val exception: Exception) : BungieApiResult<T>()
}
```

## 11. Recursos Úteis

- **Documentação Oficial**: https://bungie-net.github.io/
- **API Explorer**: https://destinydevs.github.io/BungieNetPlatform/
- **Destiny Data Explorer**: https://data.destinysets.com/
- **Manifest Explorer**: https://lowlidev.com.au/destiny/api

---

**Próximo Passo**: Implementar `BungieApiService` com os endpoints documentados acima.
