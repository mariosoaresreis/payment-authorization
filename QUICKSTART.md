# ⚡ Quick Start (5 Minutos)

## Prerequisites

- **Docker** (https://docker.com/products/docker-desktop)
- **Git** (https://git-scm.com/)

That's it! ⭐ Não precisa Java, Maven, PostgreSQL, Kafka localmente!

## 🚀 Start (1 comando)

```bash
git clone <repo-url>
cd payment-authorization
docker-compose up --build
```

Aguarde ~2-3 minutos na primeira vez (baixando imagens + building).

## ✨ Pronto! Acesse

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **API** | http://localhost:8080 | Health check |
| **Docs** | http://localhost:8080/swagger-ui.html | OpenAPI Interactive Docs |
| **Kafka UI** | http://localhost:8888 | Monitor Kafka |
| **Postgres** | localhost:5432 | DB credentials: user=`payment_user`, pass=`payment_password` |

## 📝 Test (2 segundos)

Abra uma aba nova em seu terminal:

```bash
curl -X POST http://localhost:8080/api/v1/transactions/authorize \
  -H "Content-Type: application/json" \
  -d '{
    "card_number": "4111111111111111",
    "card_holder": "John Doe",
    "bin": "411111",
    "expiry_month": "12",
    "expiry_year": "2025",
    "merchant_id": "MERCHANT123",
    "amount": 150.50,
    "currency": "BRL"
  }'
```

**Expected Response (200 OK):**
```json
{
  "transaction_id": "TXN_...",
  "status": "APPROVED",
  "authorization_code": "VISA_...",
  "fraud_score": "LOW",
  "amount": "150.50",
  "currency": "BRL"
}
```

## 🛑 Stop

```bash
docker-compose down
```

## 📚 More

- Full docs: `README.md`
- Architecture: `ARCHITECTURE.md`
- Deployment: `DEPLOYMENT.md`

---

**That's it! 🎉 Você tem um microsserviço profissional rodando localmente.**

