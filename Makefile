.PHONY: help build test run docker clean fmt lint security

help:
	@echo "Payment Authorization Microservice - Makefile"
	@echo ""
	@echo "Available targets:"
	@echo "  build               - Build the application"
	@echo "  test                - Run unit tests"
	@echo "  test-integration    - Run integration tests"
	@echo "  clean               - Clean build artifacts"
	@echo "  fmt                 - Format code (not standard, for reference)"
	@echo "  lint                - Run code quality checks"
	@echo "  security            - Check for security vulnerabilities"
	@echo "  docker-build        - Build Docker image"
	@echo "  docker-run          - Run container locally"
	@echo "  docker-compose-up   - Start all services with docker-compose"
	@echo "  docker-compose-down - Stop all services"
	@echo "  run                 - Run application locally"
	@echo "  dev                 - Run in development mode"
	@echo "  logs                - View application logs"
	@echo "  health              - Check service health"
	@echo "  api-docs            - Open API documentation"
	@echo "  coverage            - Generate test coverage report"

# Build targets
build: clean
	@echo "🔨 Building application..."
	mvn clean package -DskipTests
	@echo "✅ Build complete"

build-with-tests: clean
	@echo "🔨 Building application with tests..."
	mvn clean package
	@echo "✅ Build complete with tests"

# Test targets
test:
	@echo "🧪 Running unit tests..."
	mvn test
	@echo "✅ Tests complete"

test-integration: docker-compose-up
	@echo "🧪 Running integration tests..."
	mvn test -Dgroups=integration
	@echo "✅ Integration tests complete"

coverage:
	@echo "📊 Generating test coverage report..."
	mvn test jacoco:report
	@open target/site/jacoco/index.html 2>/dev/null || xdg-open target/site/jacoco/index.html 2>/dev/null || echo "Coverage report generated at target/site/jacoco/index.html"

# Code quality targets
fmt:
	@echo "🎨 Formatting code..."
	# Note: Java doesn't have a standard formatter like Go's gofmt
	# This would be a placeholder for IDE auto-formatting
	@echo "📌 Use IDE auto-format (IntelliJ: Ctrl+Alt+L, VSCode: Shift+Alt+F)"

lint:
	@echo "🔍 Running code quality checks..."
	mvn checkstyle:check pmd:check spotbugs:check
	@echo "✅ Code quality checks complete"

security:
	@echo "🔒 Checking dependencies for vulnerabilities..."
	mvn org.owasp:dependency-check-maven:check
	@echo "✅ Security check complete"

# Docker targets
docker-build:
	@echo "🐳 Building Docker image..."
	docker build -t fiserv/payment-authorization:latest .
	docker tag fiserv/payment-authorization:latest fiserv/payment-authorization:1.0.0
	@echo "✅ Docker image built"

docker-run: docker-build
	@echo "🚀 Running Docker container..."
	docker run -d \
		--name payment-app \
		-e SPRING_PROFILES_ACTIVE=dev \
		-p 8080:8080 \
		fiserv/payment-authorization:latest
	@echo "✅ Container running at http://localhost:8080"

docker-stop:
	@echo "🛑 Stopping Docker container..."
	docker stop payment-app || true
	docker rm payment-app || true
	@echo "✅ Container stopped"

# Docker Compose targets
docker-compose-up:
	@echo "🐳 Starting services with Docker Compose..."
	docker-compose up -d
	@echo "⏳ Waiting for services to be ready..."
	@sleep 5
	@echo "✅ Services started"
	@echo "   API: http://localhost:8080"
	@echo "   Swagger: http://localhost:8080/swagger-ui.html"
	@echo "   Kafka UI: http://localhost:8888"
	@echo "   PostgreSQL: localhost:5432"

docker-compose-down:
	@echo "🛑 Stopping services..."
	docker-compose down
	@echo "✅ Services stopped"

docker-compose-logs:
	@echo "📋 Showing Docker Compose logs..."
	docker-compose logs -f

docker-compose-clean:
	@echo "🧹 Cleaning Docker Compose (removing volumes)..."
	docker-compose down -v
	@echo "✅ Cleaned"

# Application run targets
run: build
	@echo "🚀 Running application..."
	java -jar target/payment-authorization-*.jar

dev:
	@echo "🚀 Running in development mode..."
	mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Utility targets
logs:
	@echo "📋 Showing application logs..."
	docker-compose logs -f payment-service

health:
	@echo "🏥 Checking service health..."
	@curl -s http://localhost:8080/actuator/health | jq . || echo "Service not responding"

api-docs:
	@echo "📚 Opening API documentation..."
	@open http://localhost:8080/swagger-ui.html 2>/dev/null || xdg-open http://localhost:8080/swagger-ui.html 2>/dev/null || echo "Open http://localhost:8080/swagger-ui.html in your browser"

clean:
	@echo "🧹 Cleaning build artifacts..."
	mvn clean
	docker-compose down -v 2>/dev/null || true
	rm -rf logs/
	@echo "✅ Cleaned"

# Full workflow
all: clean build test docker-compose-up health
	@echo "🎉 Full workflow complete!"

# CI/CD targets
ci: security lint test build
	@echo "✅ CI pipeline complete"

cicd: ci docker-build
	@echo "✅ CI/CD pipeline complete"

# Version targets
version:
	@echo "Payment Authorization Service"
	@mvn -q help:describe -Dartifact=com.fiserv.payment:payment-authorization:1.0.0 2>/dev/null || echo "Version: 1.0.0"
	@echo "Java: $$(java -version 2>&1 | head -1)"
	@echo "Maven: $$(mvn --version | head -1)"

