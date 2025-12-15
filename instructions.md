
````markdown
# Guia de Integração: API Destiny 2 para Loadouts Manager

Este documento traduz o "Relatório Técnico Abrangente" em instruções práticas de engenharia para o projeto `Loadoutsmanager`. Ele identifica as lacunas entre a implementação atual e as melhores práticas da Bungie, fornecendo o roteiro para uma integração robusta.

---

## 1. Arquitetura de Rede e Protocolo (Camada Base)

### O Padrão "Envelope"
A implementação atual do `BungieApiService.kt` define modelos de resposta genéricos (`DestinyProfileResponse`). É necessário padronizar o tratamento do "Envelope" para capturar erros de lógica de negócio (ErrorCode != 1) antes de processar os dados.

**Ação Recomendada:**
Refatore os modelos de resposta em `com.ads.loadoutsmanager.data.api` para usar uma classe selada ou genérica que force a verificação do `ErrorCode`.

```kotlin
// Recomendação para data/api/BungieResponse.kt
open class BungieResponse<T>(
    val Response: T? = null,
    val ErrorCode: Int = 0,
    val ThrottleSeconds: Int = 0,
    val ErrorStatus: String? = null,
    val Message: String? = null
) {
    val isSuccess: Boolean get() = ErrorCode == 1
}
````

### Cabeçalhos e Identificação

O arquivo `NetworkModule.kt` já injeta a `X-API-Key`. Certifique-se de que o `User-Agent` também seja definido para facilitar o suporte da Bungie caso necessário.

-----

## 2\. Autenticação e Segurança (OAuth 2.0)

Seu projeto já possui `OAuth2Manager.kt` e `AuthenticationScreen.kt`. O relatório destaca a criticidade do *Refresh Token*.

**Instruções de Integração:**

1.  **Persistência Segura**: Não armazene tokens em `SharedPreferences` simples. Utilize `EncryptedSharedPreferences` (Jetpack Security) para guardar `access_token` e `refresh_token`.
2.  **Interceptor de Renovação**: No `NetworkModule.kt`, o `OkHttpClient` deve ter um `Authenticator` que detecta erros 401 (Unauthorized), usa o `refresh_token` para obter novas credenciais silenciosamente e retenta a requisição original. Isso evita que o usuário seja deslogado no meio de uma transferência de item.

-----

## 3\. O Manifesto (Lacuna Crítica)

O repositório atual **não possui** implementação para o Manifesto. Como detalhado no relatório, a API retorna hashes (`itemHash: 69420`), não nomes. Sem o manifesto, a UI mostrará apenas números.

**Roteiro de Implementação do Manifesto:**

1.  **Endpoint**: Adicione `GET /Platform/Destiny2/Manifest/` ao `BungieApiService`.
2.  **Download e Descompactação**:
      * Crie um `ManifestRepository`.
      * Baixe o arquivo ZIP do caminho `mobileWorldContentPaths.pt-br` (para suporte em português).
      * Descompacte o arquivo SQLite localmente no armazenamento do app.
3.  **Acesso Local**:
      * Use a biblioteca Room (já configurada no projeto) para conectar ao banco de dados externo ou copie os dados necessários para o seu `LoadoutsDatabase`.
      * *Nota de Performance*: Não faça queries SQL na UI thread. Use Coroutines.

-----

## 4\. Estratégia de Dados do Jogador (Componentes)

Atualmente, `BungieApiService.kt` usa strings mágicas ("200", "205") nos parâmetros `components`. Isso é frágil.

**Instruções:**

1.  Crie um Enum ou Objeto de Constantes para os componentes em `data/model/DestinyComponents.kt`:
    ```kotlin
    object DestinyComponents {
        const val PROFILES = 100
        const val CHARACTERS = 200
        const val CHARACTER_INVENTORIES = 201
        const val CHARACTER_EQUIPMENT = 205
        const val ITEM_INSTANCES = 300
        const val ITEM_SOCKETS = 305 // Crucial para Mods e Perks
    }
    ```
2.  Ao carregar um Loadout, sua chamada deve solicitar `Components: 200,201,205,300,305`.
      * **Por que 305?** O relatório enfatiza que "Loadouts" reais dependem de Mods e Perks. Sem o componente `ItemSockets`, você não saberá quais mods de armadura o usuário tem, tornando o loadout incompleto.

-----

## 5\. Lógica de Inventário e Cross-Save

### Resolução de Cross-Save

O `LoadoutRepository` aceita `membershipId`, mas não valida se é o ID correto do Cross-Save.

**Correção:**
Antes de qualquer operação, chame `getProfile` ou `LinkedProfiles`. Se `crossSaveOverride` \> 0, substitua o `membershipType` e `membershipId` atuais pelos valores retornados nesse campo. Caso contrário, você pode estar tentando equipar itens em uma conta "fantasma" da PSN quando o usuário joga na Steam.

### Fluxo de Transferência (Char A -\> Cofre -\> Char B)

Seu `LoadoutRepository.kt` já tem um esqueleto para `transferItemBetweenCharacters`. O relatório confirma que transferências diretas entre personagens são impossíveis.

**Refinamento da Lógica `equipLoadout`:**

1.  **Verificação de Espaço**: Antes de mover itens, verifique se o personagem destino tem slots livres (9 slots por bucket). A API falhará com `DestinyNoRoomInDestination` se estiver cheio.
2.  **Tratamento de Exóticos**: A lógica deve verificar se o loadout contém mais de uma arma exótica ou armadura exótica. A API bloqueará a ação de equipar, mas você deve prevenir isso na UI ou tratar o erro `DestinyItemUnequippable`.

-----

## 6\. Sockets e Plugs (Visualização Realista)

Para cumprir a intenção de "Loadouts com Skins e Shaders" (mencionado no `IMPLEMENTATION_SUMMARY.md`), você precisa processar os `ItemSockets`.

**Instruções:**

1.  No modelo `DestinyItem`, o campo `cosmetics` deve ser populado lendo os sockets do item.
2.  **Mapeamento**:
      * Itere sobre os sockets retornados pelo componente `305`.
      * Identifique sockets de categoria "Cosmetic" (Definição no Manifesto).
      * O `plugHash` ativo nesse socket é o hash do Ornamento ou Shader.
      * Use esse hash para buscar o ícone correto no Manifesto.

-----

## 7\. Checklist de Ação Imediata

Para alinhar o repositório `ggxrc/loadoutsmanager` com o relatório técnico:

  - [ ] **Segurança**: Mover o armazenamento de tokens para `EncryptedSharedPreferences`.
  - [ ] **Infra**: Implementar a classe `BungieResponse` (Envelope Pattern).
  - [ ] **Dados**: Criar o serviço de download e leitura do Manifesto SQLite.
  - [ ] **Lógica**: Implementar verificação de `crossSaveOverride` no login.
  - [ ] **API**: Adicionar o componente `305 (ItemSockets)` às chamadas de perfil para ler mods e cosméticos corretamente.
  - [ ] **Resiliência**: Tratar o ErrorCode `5 (SystemDisabled)` globalmente para avisar o usuário sobre manutenções.

<!-- end list -->

```
```