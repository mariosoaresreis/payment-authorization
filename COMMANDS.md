# 📚 Commands Reference

Todos os comandos úteis para este projeto.

## 🚀 Quick Commands

```bash
# Start everything (RECOMENDADO)
./start.sh

# OR use docker-compose directly
docker-compose up -d --build

# View logs
docker-compose logs -f payment-service

# Stop
docker-compose down

# Clean everything (volumes, images, etc)
docker-compose down -v
```

## 🧪 Development

```bash
# Run tests
mvn test

# Run specific test class
mvn test -Dtest=TransactionValidatorChainTest

# Run with coverage
mvn test jacoco:report
# Open: target/site/jacoco/index.html

# Development mode (watch)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Check code quality
mvn checkstyle:check pmd:check spotbugs:check
```

## 🔨 Build & Package

```bash
# Build
mvn clean package -DskipTests

# Build with tests
mvn clean package

# Build Docker image
docker build -t payment-authorization:latest .

# Push to registry
docker tag payment-authorization:latest registry.example.com/payment-authorization:latest
docker push registry.example.com/payment-authorization:latest
```

## 🔍 Quick Checks

```bash
# API health
curl http://localhost:8080/actuator/health

# API info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Open API docs
open http://localhost:8080/swagger-ui.html

# Kafka UI
open http://localhost:8888
```

## 🗄️ Database

```bash
# Connect to database
PGPASSWORD=payment_password psql -h localhost -U payment_user -d payment_db

# Run migrations (automatic on startup)
# Or manually with Flyway:
mvn flyway:migrate

# View schema
SELECT * FROM information_schema.tables WHERE table_schema = 'public';
```

## 🐳 Docker Commands

```bash
# View running containers
docker-compose ps

# View service logs
docker-compose logs -f payment-service
docker-compose logs -f postgres
docker-compose logs -f kafka

# Execute command in container
docker-compose exec payment-service bash

# Restart service
docker-compose restart payment-service

# View resource usage
docker stats

# Prune (clean up unused resources)
docker system prune -a
```

## 🔄 Kafka Commands

```bash
# List topics
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Create topic
docker-compose exec kafka kafka-topics \
  --create \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# Consume messages
docker-compose exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic transactions.authorized \
  --from-beginning

# Producer test
docker-compose exec kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic test-topic
```

## ☸️ Kubernetes Commands

```bash
# Apply manifests
kubectl apply -f k8s/deployment.yml -n payment-service

# View pods
kubectl get pods -n payment-service

# View services
kubectl get svc -n payment-service

# View logs
kubectl logs -f deployment/payment-authorization -n payment-service

# Port forward
kubectl port-forward svc/payment-authorization 8080:8080 -n payment-service

# Describe pod
kubectl describe pod <pod-name> -n payment-service

# Delete deployment
kubectl delete -f k8s/deployment.yml -n payment-service

# Scale deployment
kubectl scale deployment payment-authorization --replicas=5 -n payment-service
```

## 📊 Using Makefile

```bash
make help                 # Show all targets
make build               # Build Maven project
make test                # Run tests
make coverage            # Generate coverage report
make docker-build        # Build Docker image
make docker-compose-up   # Start docker-compose
make docker-compose-down # Stop docker-compose
make clean               # Clean everything
make health              # Check service health
make api-docs            # Open Swagger UI
```

## 🔐 Security Checks

```bash
# Check dependencies for CVEs
mvn org.owasp:dependency-check-maven:check

# Check for code vulnerabilities
# npm install -g snyk  # if using vulnerable npm packages
snyk test

# Scan Docker image
docker scan payment-authorization:latest

# OWASP Dependency Check (more detailed)
mvn dependency-check:aggregate
```

## 🆘 Troubleshooting

```bash
# Services not starting?
docker-compose logs

# Database connection error?
PGPASSWORD=payment_password psql -h localhost -U payment_user -d payment_db -c "SELECT 1"

# Port already in use?
lsof -i :8080
kill -9 <PID>

# Rebuild from scratch
docker-compose down -v && docker-compose up --build

# Clear Maven cache
rm -rf ~/.m2/repository
mvn clean package

# Check Java version
java -version  # Should be Java 17+
```

## 💡 Pro Tips

```bash
# Watch tests during development
watch -n 2 'mvn test'

# Build in background
mvn clean package -DskipTests &

# Follow latest logs
docker-compose logs -f --tail=100

# Quick health check script
while true; do
  curl -s http://localhost:8080/actuator/health | jq .
  sleep 5
done

# Save API response to file
curl http://localhost:8080/api/v1/transactions/authorize \
  -H "Content-Type: application/json" \
  -d '{...}' > response.json
```

## 📖 Documentation Files

- `README.md` - Full documentation
- `QUICKSTART.md` - 5-minute setup
- `ARCHITECTURE.md` - Deep dive into design patterns
- `DEPLOYMENT.md` - Production deployment guide
- `CONTRIBUTING.md` - Contributing guidelines
- `PROJECT_SUMMARY.md` - Project overview

---

**Last updated:** May 9, 2026

