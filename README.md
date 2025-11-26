# Polaris Task Manager

> Sistema de gerenciamento de tarefas Fullstack de alta performance, desenvolvido com Clean Architecture, persistência NoSQL e foco em escalabilidade.

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-green?style=for-the-badge&logo=spring)
![Redis](https://img.shields.io/badge/Redis-Primary_DB-red?style=for-the-badge&logo=redis)
![Vue.js](https://img.shields.io/badge/Vue.js-3-4FC08D?style=for-the-badge&logo=vue.js)
![Ionic](https://img.shields.io/badge/Ionic-7-3880FF?style=for-the-badge&logo=ionic)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)

## Sobre o Projeto

O **Polaris** é uma solução arquitetural robusta desenvolvida para demonstrar excelência técnica em engenharia de software moderna. O projeto vai além de um CRUD simples, implementando padrões de mercado para sistemas distribuídos e seguros.

### Destaques Arquiteturais
* **Clean Architecture:** Isolamento total do domínio (regras de negócio) de frameworks e banco de dados.
* **Redis as Primary Database:** Uso estratégico de NoSQL para performance extrema (leitura/escrita em memória com persistência AOF).
* **Segurança RBAC:** Controle de acesso baseado em papéis (Gerente/Desenvolvedor) via JWT Stateless.
* **Frontend Híbrido:** Interface responsiva (Mobile/Desktop) com Vue.js + Ionic, com suporte nativo a Dark/Light mode.

---

## Tecnologias

### Backend (API REST)
* **Linguagem:** Java 21 (LTS)
* **Framework:** Spring Boot 3.4 (Web, Security, Validation)
* **Banco de Dados:** Spring Data Redis (Repositório NoSQL)
* **Testes:** JUnit 5 + Mockito
* **Infra:** Docker (Multi-stage build)
* **Documentação:** OpenAPI / Swagger

### Frontend (Web/Mobile)
* **Framework:** Vue.js 3 (Composition API + Script Setup)
* **UI Toolkit:** Ionic 7
* **Linguagem:** TypeScript
* **Estilização:** SASS/SCSS (Modular)
* **HTTP Client:** Axios com Interceptors

---

## Como Rodar o Projeto

### Pré-requisitos
* Docker & Docker Compose instalados.

### Modo Produção
Execute a aplicação completa (Banco + API) com um único comando na raiz:

```bash
docker-compose up --build -d
```

* **API & Swagger**: http://localhost:8080/swagger-ui.html

* **Redis (Banco)**: Porta 6379

### Modo Desenvolvimento

Backend:

```bash
cd polaris-backend
./mvnw spring-boot:run
```

Frontend:

```bash
cd polaris-frontend
npm install
ionic serve
```

Acesse em: http://localhost:8100

---

## Credenciais de Acesso

O sistema inicializa sem usuários. Utilize o Postman ou Swagger para registrar o primeiro administrador.

Admin (Gerente):

* Email: admin@polaris.com

* Senha: 123

User (Desenvolvedor):

* Email: dev@polaris.com

* Senha: 123

---

## Autor

Roberlan Carvalho Desenvolvedor Fullstack & Especialista em IA

www.roberlancarvalho.com