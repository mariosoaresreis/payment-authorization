# Deployment Guide

Este documento descreve como fazer deploy do microsserviço de autorização de pagamentos.

## 📋 Índice

1. [Local Development](#local-development)
2. [Docker](#docker)
3. [Kubernetes](#kubernetes)
4. [AWS ECS](#aws-ecs)
5. [OpenShift](#openshift)
6. [Checklist de Deploy](#checklist-de-deploy)

---

## Local Development

### Setup Inicial

```bash
# Clone repositório
git clone <repo>
cd payment-authorization

# Instale Java 17
# macOS: brew install openjdk@17
# Ubuntu: sudo apt-get install openjdk-17-jdk

# Instale Maven
# macOS: brew install maven
# Ubuntu: sudo apt-get install maven

# Verifique versões
java -version      # openjdk version "17.0.x"
mvn --version      # Apache Maven 3.8+
```

### Rodar com Docker Compose

```bash
# Build e inicia tudo
docker-compose up --build

# Logs da aplicação
docker-compose logs -f payment-service

# Stop
docker-compose down

# Clean volumes
docker-compose down -v
```

### Rodar sem Docker

```bash
# Terminal 1: PostgreSQL
docker run -d \
  --name payment-postgres \
  -e POSTGRES_DB=payment_db \
  -e POSTGRES_USER=payment_user \
  -e POSTGRES_PASSWORD=payment_password \
  -p 5432:5432 \
  postgres:15-alpine

# Terminal 2: Kafka + Zookeeper
docker-compose up zookeeper kafka

# Terminal 3: Aplicação
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## Docker

### Build Local

```bash
# Build imagem
docker build -t fiserv/payment-authorization:1.0.0 .

# Tag para registry
docker tag fiserv/payment-authorization:1.0.0 \
  registry.example.com/fiserv/payment-authorization:1.0.0

# Push para registry
docker push registry.example.com/fiserv/payment-authorization:1.0.0
```

### Verificar Imagem

```bash
# Inspect imagem
docker image inspect fiserv/payment-authorization:1.0.0

# Ver layers
docker history fiserv/payment-authorization:1.0.0

# Scan vulnerabilidades
docker scan fiserv/payment-authorization:1.0.0
```

### Run Container

```bash
# Rodar isolado
docker run -d \
  --name payment-app \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/payment_db \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  -p 8080:8080 \
  fiserv/payment-authorization:1.0.0

# Testar
curl http://localhost:8080/actuator/health
```

---

## Kubernetes

### Prerequisites

```bash
# Instale kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl

# Instale Helm (opcional)
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

### Deploy Manual (Manifests)

```bash
# Create namespace
kubectl create namespace payment-service

# Create ConfigMap
kubectl apply -f k8s/configmap.yml -n payment-service

# Create Secret
kubectl create secret generic payment-secrets \
  --from-literal=db-password=secret_password \
  --from-literal=jwt-secret=jwt_secret_key \
  -n payment-service

# Deploy PostgreSQL (opcional - usar managed DB em produção)
kubectl apply -f k8s/postgres-statefulset.yml -n payment-service

# Deploy Kafka (opcional - usar managed Kafka)
kubectl apply -f k8s/kafka-statefulset.yml -n payment-service

# Deploy aplicação
kubectl apply -f k8s/deployment.yml -n payment-service

# Create Service
kubectl apply -f k8s/service.yml -n payment-service

# Create Ingress
kubectl apply -f k8s/ingress.yml -n payment-service
```

### Helm (Recomendado)

```bash
# Create values file
cat > k8s/values.yml << EOF
replicaCount: 3

image:
  repository: registry.example.com/fiserv/payment-authorization
  tag: '1.0.0'
  pullPolicy: IfNotPresent

service:
  type: ClusterIP
  port: 8080

ingress:
  enabled: true
  hostname: payment-api.example.com

resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"

autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80
EOF

# Install Helm chart
helm install payment-auth ./k8s/helm \
  -f k8s/values.yml \
  -n payment-service \
  --create-namespace

# Upgrade
helm upgrade payment-auth ./k8s/helm \
  -f k8s/values.yml \
  -n payment-service

# Uninstall
helm uninstall payment-auth -n payment-service
```

### Verificar Deploy

```bash
# Status dos pods
kubectl get pods -n payment-service

# Logs
kubectl logs -f deployment/payment-auth -n payment-service

# Port forward
kubectl port-forward svc/payment-auth 8080:8080 -n payment-service

# Testar
curl http://localhost:8080/swagger-ui.html
```

---

## AWS ECS

### Prerequisites

```bash
# Configure AWS CLI
aws configure
export AWS_PROFILE=payment-dev

# Criar ECR repository
aws ecr create-repository \
  --repository-name fiserv/payment-authorization \
  --region us-east-1
```

### Build e Push para ECR

```bash
# Login ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789.dkr.ecr.us-east-1.amazonaws.com

# Build
docker build -t payment-authorization:1.0.0 .

# Tag
docker tag payment-authorization:1.0.0 \
  123456789.dkr.ecr.us-east-1.amazonaws.com/fiserv/payment-authorization:1.0.0

# Push
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/fiserv/payment-authorization:1.0.0
```

### Deploy ECS

```bash
# Create Fargate task definition
aws ecs register-task-definition \
  --cli-input-json file://ecs/task-definition.json \
  --region us-east-1

# Create ECS service
aws ecs create-service \
  --cluster payment-prod \
  --service-name payment-auth \
  --task-definition payment-auth:1 \
  --desired-count 3 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={
    subnets=[subnet-xxx,subnet-yyy],
    securityGroups=[sg-xxx],
    assignPublicIp=DISABLED
  }" \
  --region us-east-1
```

---

## OpenShift

### Deploy via OpenShift CLI

```bash
# Login
oc login https://openshift.example.com --token=<token>

# Create project
oc new-project payment-service

# Create app from Dockerfile
oc new-app --docker-image=registry.example.com/fiserv/payment-authorization:1.0.0 \
  --name=payment-auth

# Expose service
oc expose svc/payment-auth \
  --hostname=payment-api.apps.example.com

# Create route
oc create route edge payment-auth \
  --service=payment-auth \
  --hostname=payment-api.apps.example.com

# Scale
oc scale deployment payment-auth --replicas=3

# View logs
oc logs -f deployment/payment-auth

# Port forward
oc port-forward svc/payment-auth 8080:8080
```

---

## Checklist de Deploy

### Pré-Deploy

- [ ] Code review concluído
- [ ] Testes passando: `mvn test`
- [ ] SonarQube score OK
- [ ] Segurança: OWASP top 10 verificado
- [ ] Dependências sem CVEs: `mvn org.owasp:dependency-check-maven:check`
- [ ] Documentação atualizada
- [ ] ADRs documentados
- [ ] Changelog atualizado

### Build & Push

- [ ] Imagem Docker construída
- [ ] Imagem scaneada por vulnerabilidades
- [ ] Imagem pushada para registry
- [ ] Tag de versão criada em Git

### Deploy em Staging

- [ ] Variáveis de ambiente configuradas
- [ ] ConfigMaps/Secrets criados
- [ ] Banco de dados migrado (Flyway/Liquibase)
- [ ] Health checks passando
- [ ] Smoke tests executados
- [ ] Logs verificados

### Deploy em Produção

- [ ] Backup do banco de dados realizado
- [ ] Rollback plan testado
- [ ] Feature flags preparados (se necessário)
- [ ] Canary deployment (5% → 25% → 100%) ou Blue-Green
- [ ] Monitoramento ativado
- [ ] Alertas configurados
- [ ] Post-deploy verification executado
- [ ] Release notes publicadas

### Pós-Deploy

- [ ] Métricas normais (latência, erro rate)
- [ ] Logs sem exceções críticas
- [ ] Alertas de performance não disparados
- [ ] Usuarios reportam funcionalidade OK
- [ ] On-call engineer monitorando

---

## Helm Chart Exemplo

**k8s/helm/values.yml:**

```yaml
replicaCount: 3

image:
  repository: registry.example.com/fiserv/payment-authorization
  tag: '1.0.0'
  pullPolicy: IfNotPresent

service:
  type: ClusterIP
  port: 8080

resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"

autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 80

env:
  SPRING_PROFILES_ACTIVE: prod
  FRAUD_STRATEGY: basic

secrets:
  DATABASE_URL: "encoded_secret"
  DATABASE_PASSWORD: "encoded_secret"
  KAFKA_SERVERS: "kafka:9092"
```

---

## Troubleshooting Comum

### Container não inicia

```bash
# Ver logs
docker logs <container>

# Verificar entrada do container
docker inspect <container> | grep Cmd

# Check environment
docker inspect <container> | grep Env

# Debug interativo
docker run -it --entrypoint /bin/bash image:tag
```

### Aplicação não conecta ao banco

```bash
# Verificar conectividade
kubectl run -it --rm debug --image=busybox --restart=Never -- \
  sh -c "nc -zv postgres.default.svc.cluster.local 5432"

# Verificar ConfigMap
kubectl get configmap payment-config -o yaml

# Logs de erro
kubectl logs <pod> | grep -i database
```

### Kafka consumers não processando

```bash
# Verificar consumer lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group payment-auth-group --describe

# Reset offset (cuidado!)
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group payment-auth-group --reset-offsets \
  --to-earliest --execute --topic transactions.authorized
```

---

## Links Úteis

- [Kubernetes Deployment Best Practices](https://kubernetes.io/docs/)
- [Helm Charts](https://helm.sh/docs/)
- [AWS ECS Best Practices](https://docs.aws.amazon.com/AmazonECS/)
- [OpenShift Documentation](https://docs.openshift.com/)

---

**Última atualização:** Maio 9, 2026

