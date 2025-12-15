# 🔍 Guia de Depuração - Erro HTTP 404

## 📱 Como Verificar se a Correção Funcionou

### 1. Limpar o Projeto

```bash
# No terminal do Android Studio
./gradlew clean
./gradlew assembleDebug
```

Ou no IDE:
- Build → Clean Project
- Build → Rebuild Project

### 2. Desinstalar App Antigo

```bash
adb uninstall com.ads.loadoutsmanager
```

Ou manualmente no dispositivo.

### 3. Instalar e Executar

```bash
./gradlew installDebug
```

---

## 🔎 Verificar Logs HTTP

### Ativar Logcat no Android Studio

1. Abra a aba **Logcat**
2. Filtre por: `tag:OkHttp` ou `tag:HttpLoggingInterceptor`

### O que você deve ver:

#### ✅ Headers Corretos (Após Autenticação)

```
D/OkHttp: --> GET https://www.bungie.net/Platform/User/GetCurrentUser/
D/OkHttp: X-API-Key: 1758c151a739409fb9bbf116ec7c2cf9
D/OkHttp: Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
D/OkHttp: --> END GET
D/OkHttp: <-- 200 OK https://www.bungie.net/Platform/User/GetCurrentUser/
```

#### ❌ Se ainda der 404

```
D/OkHttp: <-- 404 Not Found https://www.bungie.net/Platform/User/GetCurrentUser/
```

**Possíveis causas:**
1. Token de acesso inválido/expirado
2. Endpoint incorreto
3. API Key inválida

---

## 🧪 Teste Manual das Credenciais

### Teste 1: Verificar API Key

Adicione temporariamente em `BungieConfig.kt`:

```kotlin
object BungieConfig {
    init {
        android.util.Log.d("BungieConfig", "=== CREDENTIALS CHECK ===")
        android.util.Log.d("BungieConfig", "API Key: ${apiKey.take(20)}...")
        android.util.Log.d("BungieConfig", "Client ID: $clientId")
        android.util.Log.d("BungieConfig", "Is Configured: ${isOAuth2Configured()}")
    }
    
    // ...existing code...
}
```

**Verifique no Logcat:**
```
D/BungieConfig: === CREDENTIALS CHECK ===
D/BungieConfig: API Key: 1758c151a739409fb9bb...
D/BungieConfig: Client ID: 51108
D/BungieConfig: Is Configured: true
```

### Teste 2: Verificar Token Storage

Adicione em `SecureTokenStorage.kt`:

```kotlin
fun saveTokens(...) {
    // ...existing code...
    
    android.util.Log.d("TokenStorage", "=== TOKEN SAVED ===")
    android.util.Log.d("TokenStorage", "Access Token: ${accessToken.take(30)}...")
    android.util.Log.d("TokenStorage", "Expires In: $expiresIn seconds")
}

fun getAccessToken(): String? {
    val token = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    android.util.Log.d("TokenStorage", "Getting token: ${token?.take(30) ?: "NULL"}")
    return token
}
```

**Verifique no Logcat:**
```
D/TokenStorage: === TOKEN SAVED ===
D/TokenStorage: Access Token: eyJhbGciOiJSUzI1NiIsInR5cC...
D/TokenStorage: Expires In: 3600 seconds

// Depois, ao fazer request:
D/TokenStorage: Getting token: eyJhbGciOiJSUzI1NiIsInR5cC...
```

### Teste 3: Verificar Network Interceptor

Adicione em `NetworkModule.kt`:

```kotlin
val authInterceptor = Interceptor { chain ->
    val originalRequest = chain.request()
    val requestBuilder = originalRequest.newBuilder()
        .header("X-API-Key", BungieConfig.apiKey)
    
    getAccessToken()?.let { token ->
        requestBuilder.header("Authorization", "Bearer $token")
        android.util.Log.d("NetworkModule", "Adding Bearer token: ${token.take(30)}...")
    }
    
    val request = requestBuilder.build()
    android.util.Log.d("NetworkModule", "=== REQUEST ===")
    android.util.Log.d("NetworkModule", "URL: ${request.url}")
    android.util.Log.d("NetworkModule", "Headers: ${request.headers}")
    
    chain.proceed(request)
}
```

---

## 🐛 Cenários de Erro e Soluções

### Cenário 1: "X-API-Key header não aparece nos logs"

**Problema:** BungieConfig não está sendo usado
**Solução:** 
1. Verificar se NetworkModule importa BungieConfig
2. Rebuild do projeto
3. Limpar cache do Gradle

### Cenário 2: "Token é null ao fazer request"

**Problema:** Token não foi salvo ou SharedPreferences não persiste
**Solução:**
1. Verificar se `commit()` está sendo chamado (não `apply()`)
2. Verificar se mesmo contexto é usado
3. Tentar SharedPreferences regular (não Encrypted)

### Cenário 3: "404 em GetCurrentUser"

**Problema:** Endpoint ou autenticação inválidos
**Solução:**
1. Verificar se endpoint é exatamente `User/GetCurrentUser/`
2. Confirmar que token OAuth foi obtido com sucesso
3. Testar endpoint manualmente:

```bash
curl -H "X-API-Key: 1758c151a739409fb9bbf116ec7c2cf9" \
     -H "Authorization: Bearer SEU_TOKEN" \
     https://www.bungie.net/Platform/User/GetCurrentUser/
```

### Cenário 4: "401 Unauthorized"

**Problema:** Token inválido ou expirado
**Solução:**
1. Limpar tokens: Settings → Apps → Loadouts Manager → Clear Data
2. Fazer novo login OAuth
3. Verificar se refresh token está funcionando

---

## 📊 Checklist de Depuração

Execute na ordem:

```
✅ [ ] Projeto compilou sem erros
✅ [ ] App foi desinstalado e reinstalado
✅ [ ] BungieConfig mostra credenciais corretas nos logs
✅ [ ] OAuth redirect funcionou (app abriu após login Bungie)
✅ [ ] Token foi salvo (visto nos logs de TokenStorage)
✅ [ ] NetworkModule adiciona X-API-Key header
✅ [ ] NetworkModule adiciona Authorization header
✅ [ ] GetCurrentUser retorna 200 OK
✅ [ ] GetLinkedProfiles retorna 200 OK
✅ [ ] AuthState muda para Authenticated
```

---

## 🎯 Endpoint Correto da Bungie API

### GetCurrentUser (para obter Bungie Membership ID)

```http
GET /Platform/User/GetCurrentUser/ HTTP/1.1
Host: www.bungie.net
X-API-Key: 1758c151a739409fb9bbf116ec7c2cf9
Authorization: Bearer {access_token}
```

**Resposta esperada:**
```json
{
  "Response": {
    "membershipId": "21535332",
    "uniqueName": "username#1234",
    "displayName": "Username",
    ...
  },
  "ErrorCode": 1,
  "Message": "Ok"
}
```

### GetLinkedProfiles (para resolver Cross-Save)

```http
GET /Platform/Destiny2/254/Profile/21535332/LinkedProfiles/ HTTP/1.1
Host: www.bungie.net
X-API-Key: 1758c151a739409fb9bbf116ec7c2cf9
Authorization: Bearer {access_token}
```

**Nota:** `254` é o membershipType para BungieNext (conta Bungie)

---

## 🚨 Se AINDA der erro 404

### Última opção: Teste direto via cURL

1. **Faça login OAuth manualmente** e copie o access token dos logs

2. **Teste GetCurrentUser:**
```bash
curl -v \
  -H "X-API-Key: 1758c151a739409fb9bbf116ec7c2cf9" \
  -H "Authorization: Bearer SEU_ACCESS_TOKEN_AQUI" \
  https://www.bungie.net/Platform/User/GetCurrentUser/
```

3. **Se retornar 200:** O problema está no app (provavelmente token não está sendo passado)

4. **Se retornar 404:** O problema está nas credenciais ou na conta Bungie

---

## 📞 Informações de Suporte

### Suas Credenciais (de local.properties):
```
API Key: 1758c151a739409fb9bbf116ec7c2cf9
Client ID: 51108
Client Secret: wo1gggUyWAg-eXRK1Xj4lIAZaGs1hZfEK1j83m8Cqi4
```

### URLs Importantes:
- **Gerenciar App:** https://www.bungie.net/en/Application/Detail/51108
- **Documentação:** https://bungie-net.github.io/multi/index.html
- **Fórum:** https://www.bungie.net/en/Forums/Topics?pNumber=0&tSort=1&tType=0&tg=BungieNetPlatform

### Erros Comuns:
- **404:** Endpoint errado ou recurso não existe
- **401:** Não autenticado ou token inválido
- **403:** Autenticado mas sem permissão
- **500:** Erro do servidor Bungie (retry depois)

