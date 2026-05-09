#!/bin/bash

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}"
echo "╔═══════════════════════════════════════���═══════════════════════╗"
echo "║   Payment Authorization Microservice - Quick Start             ║"
echo "║   Fiserv Interview Project                                    ║"
echo "╚═════════════════════════��═════════════════════════════════════╝"
echo -e "${NC}"

# Check dependencies
echo -e "\n${YELLOW}Checking dependencies...${NC}"

if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker not found. Please install Docker.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker${NC}"

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose not found. Please install Docker Compose.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker Compose${NC}"

if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java not found. Please install Java 17+.${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
echo -e "${GREEN}✅ Java ($JAVA_VERSION)${NC}"

if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}⚠️  Maven not found (optional, using Maven wrapper).${NC}"
else
    echo -e "${GREEN}✅ Maven${NC}"
fi

# Menu
echo -e "\n${BLUE}Choose an option:${NC}"
echo "1) Start with Docker Compose (Recommended - includes all services)"
echo "2) Build Project (Maven)"
echo "3) Run Tests"
echo "4) View API Documentation"
echo "5) Stop all services"
echo "6) Clean everything"
echo "0) Exit"

read -p "Enter your choice [0-6]: " choice

case $choice in
    1)
        echo -e "\n${BLUE}Starting services with Docker Compose...${NC}"
        docker-compose up -d --build

        echo -e "\n${YELLOW}Waiting for services to be healthy...${NC}"
        sleep 5

        # Check if payment service is running
        if docker-compose ps | grep -q "payment-service.*Up"; then
            echo -e "\n${GREEN}✅ All services started successfully!${NC}"
            echo -e "\n${BLUE}Available endpoints:${NC}"
            echo "   🌐 API:        http://localhost:8080"
            echo "   📚 Swagger:    http://localhost:8080/swagger-ui.html"
            echo "   📊 Kafka UI:   http://localhost:8888"
            echo "   🗄️  PostgreSQL: localhost:5432 (user: payment_user, pass: payment_password)"
            echo "   💬 Kafka:      localhost:9092"
            echo -e "\n${BLUE}Try an example request:${NC}"
            echo 'curl -X POST http://localhost:8080/api/v1/transactions/authorize \'
            echo '  -H "Content-Type: application/json" \'
            echo '  -d '"'"'{
            echo '    "card_number": "4111111111111111",
            echo '    "card_holder": "John Doe",
            echo '    "bin": "411111",
            echo '    "expiry_month": "12",
            echo '    "expiry_year": "2025",
            echo '    "merchant_id": "MERCHANT123",
            echo '    "amount": 150.50,
            echo '    "currency": "BRL"
            echo '  }'"'"
            echo -e "\n${YELLOW}View logs:${NC}"
            echo "   docker-compose logs -f payment-service"
            echo -e "\n${YELLOW}To stop services:${NC}"
            echo "   docker-compose down"
        else
            echo -e "${RED}❌ Failed to start services. Check logs with: docker-compose logs${NC}"
            exit 1
        fi
        ;;

    2)
        echo -e "\n${BLUE}Building project...${NC}"
        ./mvnw clean package -DskipTests
        echo -e "${GREEN}✅ Build complete!${NC}"
        echo "JAR file: target/payment-authorization-*.jar"
        ;;

    3)
        echo -e "\n${BLUE}Running tests...${NC}"
        ./mvnw test
        ;;

    4)
        echo -e "\n${BLUE}Opening API documentation...${NC}"
        URL="http://localhost:8080/swagger-ui.html"

        if command -v xdg-open &> /dev/null; then
            xdg-open "$URL"
        elif command -v open &> /dev/null; then
            open "$URL"
        else
            echo -e "${YELLOW}Open this URL in your browser: $URL${NC}"
        fi
        ;;

    5)
        echo -e "\n${BLUE}Stopping services...${NC}"
        docker-compose down
        echo -e "${GREEN}✅ Services stopped${NC}"
        ;;

    6)
        echo -e "\n${YELLOW}This will remove all containers, volumes, and build artifacts.${NC}"
        read -p "Are you sure? (y/n): " confirm
        if [[ $confirm == "y" ]]; then
            echo -e "${BLUE}Cleaning...${NC}"
            docker-compose down -v
            ./mvnw clean
            rm -rf logs/
            echo -e "${GREEN}✅ Cleaned${NC}"
        else
            echo -e "${YELLOW}Cancelled${NC}"
        fi
        ;;

    0)
        echo -e "${YELLOW}Exiting...${NC}"
        exit 0
        ;;

    *)
        echo -e "${RED}Invalid option${NC}"
        exit 1
        ;;
esac

