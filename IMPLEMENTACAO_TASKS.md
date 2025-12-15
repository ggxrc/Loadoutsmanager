# 🧪 Testes de Acesso à API Bungie - Personagens, Inventário e Cofre

## ✅ Task 1: Navegação Implementada

A navegação após autenticação foi implementada:

### Arquivos Criados/Modificados:
- ✅ `MainScreen.kt` - Tela principal do app (vazia por enquanto)
- ✅ `MainActivity.kt` - Navegação entre LoginScreen e MainScreen
- ✅ `AuthViewModel.logout()` - Já existente e funcional

### Fluxo:
```
Login OAuth → Autenticação bem-sucedida → MainScreen
MainScreen → Botão Logout → LoginScreen
```

---

## ✅ Task 2: Banco de Dados Vazio

O banco de dados Room já vem vazio por padrão. Não há dados pré-populados.

**Verificado em:** `LoadoutsDatabase.kt`
- ✅ Sem callbacks de prepopulate
- ✅ Sem dados iniciais
- ✅ App começa limpo

---

## 🧪 Task 3: Verificar Acesso a Personagens, Inventários e Cofre

### Endpoints Disponíveis:

#### 1. **GetProfile** - Perfil completo do usuário
```kotlin
@GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/")
suspend fun getProfile(
    @Path("membershipType") membershipType: Int,
    @Path("destinyMembershipId") destinyMembershipId: String,
    @Query("components") components: String
): BungieResponse<ProfileData>
```

**Componentes disponíveis:**
- `100` - Profiles
- `102` - ProfileInventories (Cofre)
- `200` - Characters
- `201` - CharacterInventories
- `205` - CharacterEquipment
- `300` - ItemInstances
- `305` - ItemSockets (para subclasse)

#### 2. **GetCharacter** - Equipamento de um personagem específico
```kotlin
@GET("Destiny2/{membershipType}/Profile/{destinyMembershipId}/Character/{characterId}/")
suspend fun getCharacter(
    @Path("membershipType") membershipType: Int,
    @Path("destinyMembershipId") destinyMembershipId: String,
    @Path("characterId") characterId: String,
    @Query("components") components: String
): BungieResponse<CharacterData>
```

### Como Testar:

#### Teste 1: Obter Personagens

```kotlin
// Chamar GetProfile com component 200 (Characters)
val response = bungieApiService.getProfile(
    membershipType = primaryMembership.membershipType,
    destinyMembershipId = primaryMembership.membershipId,
    components = "200"
)

// Resposta esperada:
{
  "Response": {
    "characters": {
      "data": {
        "2305843009301234567": {
          "membershipId": "...",
          "membershipType": 3,
          "characterId": "2305843009301234567",
          "classType": 0,  // Titan
          "raceType": 0,
          "genderType": 0,
          "light": 1810,
          "stats": {...}
        }
      }
    }
  }
}
```

#### Teste 2: Obter Inventário do Personagem

```kotlin
// Chamar GetProfile com components 201 + 300
val response = bungieApiService.getProfile(
    membershipType = primaryMembership.membershipType,
    destinyMembershipId = primaryMembership.membershipId,
    components = "201,300"
)

// Resposta inclui:
// - characterInventories: itens não equipados
// - itemInstances: detalhes dos itens
```

#### Teste 3: Obter Equipamento do Personagem

```kotlin
// Chamar GetProfile com component 205
val response = bungieApiService.getProfile(
    membershipType = primaryMembership.membershipType,
    destinyMembershipId = primaryMembership.membershipId,
    components = "205,300"
)

// Resposta inclui:
// - characterEquipment: itens equipados
// - itemInstances: detalhes (luz, stats)
```

#### Teste 4: Obter Cofre (Vault)

```kotlin
// Chamar GetProfile com component 102
val response = bungieApiService.getProfile(
    membershipType = primaryMembership.membershipType,
    destinyMembershipId = primaryMembership.membershipId,
    components = "102,300"
)

// Resposta inclui:
// - profileInventory: itens no cofre
// - itemInstances: detalhes dos itens
```

---

## 🎯 Task 4: Verificar Montagem de Build de Subclasse

### Componentes Necessários:

Para obter configuração completa da subclasse (super, habilidades, aspectos, fragmentos):

```kotlin
// Component 305 = ItemSockets (sockets de aspectos/fragmentos)
// Component 300 = ItemInstances
// Component 205 = CharacterEquipment (para ver subclasse equipada)

val response = bungieApiService.getProfile(
    membershipType = primaryMembership.membershipType,
    destinyMembershipId = primaryMembership.membershipId,
    components = "205,300,305"
)
```

### Estrutura da Subclasse:

A subclasse é um item equipado no slot de subclasse. Os sockets contêm:

1. **Super** - Socket 0
2. **Habilidade de Classe** - Socket 1 (ex: Barricade, Rift, Dodge)
3. **Habilidade de Movimento** - Socket 2 (ex: Jump, Lift, Glide)
4. **Grenade** - Socket 3
5. **Melee** - Socket 4
6. **Aspectos** - Sockets 5-6 (dependendo da subclasse)
7. **Fragmentos** - Sockets 7+ (dependendo dos aspectos)

### Exemplo de Resposta:

```json
{
  "itemSockets": {
    "data": {
      "1234567890": {  // Item instance ID da subclasse
        "sockets": [
          { "plugHash": 2842471112 },  // Super
          { "plugHash": 3208062880 },  // Class Ability
          { "plugHash": 2869569095 },  // Movement
          { "plugHash": 3552801891 },  // Grenade
          { "plugHash": 2182321039 },  // Melee
          { "plugHash": 3523574122 },  // Aspecto 1
          { "plugHash": 3748140693 },  // Aspecto 2
          { "plugHash": 2979132321 },  // Fragmento 1
          { "plugHash": 2979132322 }   // Fragmento 2
        ]
      }
    }
  }
}
```

---

## 📝 Próximos Passos para Implementação

### 1. Criar Modelos de Dados Corretos

Atualmente, `ProfileData` e `CharacterData` usam `Map<String, Any>`. Precisamos criar models adequados:

```kotlin
data class ProfileResponse(
    val characters: CharactersComponent?,
    val characterInventories: InventoriesComponent?,
    val characterEquipment: EquipmentComponent?,
    val profileInventory: ProfileInventoryComponent?,
    val itemComponents: ItemComponentsSet?
)

data class CharactersComponent(
    val data: Map<String, DestinyCharacter>
)

data class DestinyCharacter(
    val membershipId: String,
    val membershipType: Int,
    val characterId: String,
    val dateLastPlayed: String,
    val minutesPlayedThisSession: Long,
    val minutesPlayedTotal: Long,
    val light: Int,
    val stats: Map<Int, Int>,
    val raceType: Int,
    val genderType: Int,
    val classType: Int,
    val emblemPath: String,
    val emblemBackgroundPath: String,
    val emblemHash: Long,
    val titleRecordHash: Long?
)
```

### 2. Implementar Parsing dos Componentes

Cada componente retornado pela API precisa ser parseado corretamente.

### 3. Testar Endpoints

Adicionar testes na MainScreen para verificar:
- ✅ Acesso a personagens
- ✅ Acesso a inventário
- ✅ Acesso ao cofre
- ✅ Acesso a configuração de subclasse

---

## 🚀 Status das Tasks

| Task | Status | Observação |
|------|--------|------------|
| Navegação pós-auth | ✅ CONCLUÍDO | MainScreen implementada |
| DB vazio | ✅ CONCLUÍDO | Já vem vazio por padrão |
| Acesso a personagens | ⏳ API PRONTA | Testar parsing |
| Acesso a inventário | ⏳ API PRONTA | Testar parsing |
| Acesso ao cofre | ⏳ API PRONTA | Testar parsing |
| Build de subclasse | ⏳ API PRONTA | Testar parsing de sockets |

---

## 🧪 Como Testar no App

1. **Compile e execute** o app
2. **Faça login** com OAuth
3. **Verifique** se navega para MainScreen após autenticação
4. **Adicione botões de teste** na MainScreen para chamar:
   - `loadoutRepository.getProfile()` com diferentes components
   - Verificar resposta nos logs

**Todos os endpoints estão disponíveis e prontos para uso!** 🎉

