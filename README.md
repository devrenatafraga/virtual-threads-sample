# Virtual Threads Sample - Spring Boot WebFlux

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=devrenatafraga_virtual-threads-sample&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=devrenatafraga_virtual-threads-sample)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=devrenatafraga_virtual-threads-sample&metric=coverage)](https://sonarcloud.io/summary/new_code?id=devrenatafraga_virtual-threads-sample)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=devrenatafraga_virtual-threads-sample&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=devrenatafraga_virtual-threads-sample)

Este projeto demonstra o uso de **Virtual Threads** (Project Loom) em uma aplicação Spring Boot WebFlux com Java 21+.

## 🚀 Características

- **Java 21** com suporte nativo a Virtual Threads
- **Spring Boot 3.5.4** com WebFlux (Netty)
- **Scheduler customizado** para Virtual Threads no contexto reativo
- Exemplos práticos de uso e comparação de performance
- APIs REST reativas para testar diferentes cenários
- Testes unitários demonstrando funcionalidades
- **Análise de código com SonarCloud**
- **Cobertura de testes com JaCoCo**

## 📋 Pré-requisitos

- Java 21 ou superior
- Gradle 8.x

## 🛠️ Como executar

1. Clone o repositório
2. Execute a aplicação:
```bash
./gradlew bootRun
```

A aplicação estará disponível em: `http://localhost:8080`

## 🔗 Endpoints disponíveis

### Endpoints Spring MVC (Thread tradicional + Virtual Threads)
### 1. Comparação entre Virtual Threads vs Platform Threads
```
GET /api/virtual-threads/compare?tasks=100
```
Compara a performance entre Virtual Threads e Platform Threads tradicionais.

### 2. Operação bloqueante
```
GET /api/virtual-threads/blocking
```
Demonstra uma operação de I/O bloqueante executada em Virtual Thread.

### 3. Múltiplas operações bloqueantes
```
GET /api/virtual-threads/multiple-blocking?operations=10
```
Executa múltiplas operações bloqueantes concorrentemente usando Virtual Threads.

### Endpoints WebFlux + Virtual Threads (Recomendados)
### 4. Mono com operação bloqueante
```
GET /api/webflux-virtual-threads/mono-blocking
```
Demonstra um `Mono` executando operação bloqueante em Virtual Thread.

### 5. Flux com operações paralelas
```
GET /api/webflux-virtual-threads/flux-parallel?count=10
```
Executa múltiplas operações paralelas usando `Flux` e Virtual Threads.

### 6. Comparação de schedulers
```
GET /api/webflux-virtual-threads/compare-schedulers?tasks=5
```
Compara Virtual Thread Scheduler vs Scheduler padrão do Reactor.

### 7. Chamadas sequenciais
```
GET /api/webflux-virtual-threads/sequential-calls
```
Demonstra como Virtual Threads facilitam código sequencial no contexto reativo.

### 8. Tratamento de erros
```
GET /api/webflux-virtual-threads/error-handling
```
Demonstra tratamento de erros com Virtual Threads no WebFlux.

### 9. Teste de stress reativo
```
GET /api/webflux-virtual-threads/stress-test-reactive?numberOfTasks=1000
```
Executa muitas operações concorrentes de forma reativa com Virtual Threads.

### 10. Informações da thread atual (WebFlux)
```
GET /api/webflux-virtual-threads/thread-info
```
Retorna informações detalhadas sobre a Virtual Thread no contexto reativo.

## 📊 Exemplos de uso

### Comparação de Performance

Virtual Threads são especialmente eficientes para operações que envolvem I/O bloqueante:

```bash
# Teste com 100 tasks
curl "http://localhost:8080/api/virtual-threads/compare?tasks=100"

# Teste com 1000 tasks
curl "http://localhost:8080/api/virtual-threads/compare?tasks=1000"
```

### Teste de Stress

Para demonstrar a capacidade de executar milhares de operações concorrentes:

```bash
# 10.000 operações concorrentes
curl "http://localhost:8080/api/virtual-threads/stress-test?numberOfTasks=10000"
```

## 🧪 Executando os testes

```bash
./gradlew test
```

## 💡 Conceitos importantes

### Virtual Threads vs Platform Threads

**Platform Threads (tradicionais):**
- Mapeamento 1:1 com threads do OS
- Limitadas pela memória (cada thread consome ~2MB)
- Context switch caro
- Adequadas para CPU-intensive tasks

**Virtual Threads:**
- Mapeamento M:N com carrier threads
- Milhões podem ser criadas (consomem ~1KB)
- Context switch barato
- Ideais para I/O-intensive tasks

### Características das Virtual Threads

1. **Lightweight**: Consomem muito menos memória
2. **Cheap to create**: Criação e destruição são operações baratas
3. **Non-pooled**: Não precisam de pools, cada task pode ter sua própria thread
4. **Blocking is OK**: Operações bloqueantes não prejudicam a performance geral

### Quando usar Virtual Threads

✅ **Use quando:**
- Aplicações com muitas operações de I/O (database, HTTP calls, file I/O)
- Muitas operações concorrentes
- Código que atualmente usa thread pools

❌ **Evite quando:**
- CPU-intensive tasks
- Operações que fazem muito uso de synchronized blocks
- Pin carrier threads (operações nativas longas)

## 🌊 WebFlux + Virtual Threads: Melhor dos dois mundos

### Por que combinar WebFlux com Virtual Threads?

1. **Modelo reativo mantido**: Continue usando `Mono`, `Flux` e operadores reativos
2. **Código bloqueante simplificado**: Escreva código bloqueante sem perder performance
3. **Debugging facilitado**: Stack traces mais claros comparado a chains reativas complexas
4. **Migração gradual**: Mantenha APIs reativas, simplifique implementação

### Exemplo prático:

```java
// Antes: Código reativo complexo
@GetMapping("/sequential-calls-reactive")
public Mono<String> sequentialCallsReactive() {
    return callService1()
        .flatMap(result1 -> callService2(result1))
        .flatMap(result2 -> callService3(result2))
        .map(result3 -> "All completed: " + result3);
}

// Depois: Virtual Threads + WebFlux
@GetMapping("/sequential-calls-simple")
public Mono<String> sequentialCallsSimple() {
    return Mono.fromCallable(() -> {
        String result1 = callService1Blocking();
        String result2 = callService2Blocking(result1);
        String result3 = callService3Blocking(result2);
        return "All completed: " + result3;
    }).subscribeOn(virtualThreadScheduler);
}
```

## 🔧 Configurações

### application.properties
```properties
# Habilita Virtual Threads no Spring Boot
spring.threads.virtual.enabled=true

# Configurações para WebFlux (Netty)
server.port=8080
server.netty.connection-timeout=20s
server.netty.idle-timeout=60s
```

### Configuração programática para WebFlux
```java
@Configuration
@EnableAsync
public class VirtualThreadConfig {

    @Bean(name = "virtualThreadScheduler")
    public Scheduler virtualThreadScheduler() {
        return Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

### Uso no WebFlux Controller
```java
@RestController
public class WebFluxVirtualThreadController {

    @Autowired
    @Qualifier("virtualThreadScheduler")
    private Scheduler virtualThreadScheduler;

    @GetMapping("/mono-blocking")
    public Mono<String> monoWithBlockingOperation() {
        return Mono.fromCallable(() -> {
            Thread.sleep(1000); // Operação bloqueante
            return "Completed on Virtual Thread: " + Thread.currentThread().isVirtual();
        }).subscribeOn(virtualThreadScheduler);
    }
}
```

## 📈 Monitoramento

Para monitorar o uso das Virtual Threads:

1. **JFR (Java Flight Recorder):**
```bash
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=virtual-threads.jfr -jar app.jar
```

2. **JConsole/VisualVM**: Conecte para ver estatísticas de threads em tempo real

3. **Logs**: O projeto está configurado com logs debug para monitorar o comportamento

## 🎯 Benefícios observados

- **Throughput**: Aumento significativo no número de requisições concorrentes
- **Memory**: Redução drástica no uso de memória para threads
- **Scalability**: Capacidade de lidar com milhares de conexões simultâneas
- **Simplicity**: Código mais simples, sem necessidade de gerenciar thread pools

## 📚 Recursos adicionais

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [Spring Boot Virtual Threads](https://spring.io/blog/2022/10/11/embracing-virtual-threads)
- [Project Loom](https://openjdk.org/projects/loom/)

## 🔍 Análise de Código e Qualidade

Este projeto utiliza **SonarCloud** para análise contínua da qualidade do código e **JaCoCo** para cobertura de testes.

### Executar análise local

```bash
# Executar testes e gerar relatório de cobertura
./gradlew test jacocoTestReport

# Executar análise SonarQube (requer configuração do token)
./gradlew sonar
```

### Relatórios gerados

- **Cobertura de testes**: `build/reports/jacoco/test/jacocoTestReport.xml`
- **Relatório HTML**: `build/reports/jacoco/test/html/index.html`

### CI/CD Pipeline

O projeto está configurado com GitHub Actions que:

1. **Executa testes automaticamente** em push/PR
2. **Gera relatórios de cobertura** com JaCoCo
3. **Envia análise para SonarCloud** automaticamente
4. **Valida qualidade do código** em cada mudança

### Configuração SonarCloud

Para configurar o SonarCloud no seu fork:

1. Acesse [SonarCloud.io](https://sonarcloud.io)
2. Faça login com sua conta GitHub
3. Importe seu repositório
4. Configure o `SONAR_TOKEN` nas GitHub Actions Secrets
5. Ajuste as configurações no `sonar-project.properties` se necessário

---

**Desenvolvido com ❤️ usando Java 21 Virtual Threads**
