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
* **Microserviços:** Arquitetura desacoplada com serviços de `Auth` (8081) e `Core` (8080).
* **Mensageria (RabbitMQ):** Processamento assíncrono de eventos de criação de tarefas.
* **Armazenamento S3:** Upload de avatar de usuário integrado com **MinIO** (S3 Compatible).
* **Internacionalização (i18n):** Suporte completo a **Português** e **Inglês** no Frontend.
* **Qualidade:** Cobertura de testes unitários no Backend (**JUnit**) e Frontend (**Vitest**).

---

## Tecnologias

### Backend (API REST)
* **Linguagem:** Java 21 (LTS)
* **Framework:** Spring Boot 3.4 (Web, Security, Validation)
* **Banco de Dados:** Spring Data Redis (Repositório NoSQL)
* **Testes:** JUnit 5 + Mockito
* **Infra:** Docker (Multi-stage build)
* **Documentação:** OpenAPI / Swagger
* **Mensageria:** RabbitMQ
* **Testes:**: JUnit

### Frontend (Web/Mobile)
* **Framework:** Vue.js 3 (Composition API + Script Setup)
* **UI Toolkit:** Ionic 7
* **Linguagem:** TypeScript
* **Estilização:** SASS/SCSS (Modular)
* **HTTP Client:** Axios com Interceptors
* **Testes:**: Vitest
* **Internacionalização:**: Vue I18n

---

## Como Rodar o Projeto

### Pré-requisitos
* Docker & Docker Compose instalados.

### Modo Produção
Execute a aplicação completa (Banco + API) com um único comando na raiz:

```bash
docker-compose up --build -d
```

### Painéis de Acesso
| Serviço | URL | Credenciais |
| :--- | :--- | :--- |
| **App Frontend** | [http://localhost:8100](http://localhost:8100) | - |
| **Swagger (Core)** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | - |
| **Swagger (Auth)** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | - |
| **RabbitMQ** | [http://localhost:15672](http://localhost:15672) | `guest` / `guest` |
| **MinIO (S3)** | [http://localhost:9001](http://localhost:9001) | `polaris_admin` / `polaris_password` |
| **Redis Commander** | [http://localhost:8082](http://localhost:8082) | - |

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


## Deploy na Nuvem (AWS EC2)

O projeto contém scripts de automação para deploy rápido em instâncias AWS EC2.

### Arquitetura de Deploy
A aplicação foi desenhada para ser agnóstica à nuvem (Cloud Agnostic), rodando inteiramente em containers Docker. A estratégia recomendada é utilizar uma instância **EC2 (t3.small)** com **Ubuntu Server**.

### Como realizar o deploy
1.  Crie uma instância EC2 no console da AWS.
2.  Nas configurações avançadas ("Advanced Details"), cole o conteúdo do arquivo [`deploy/ec2-setup.sh`](deploy/ec2-setup.sh) no campo **User Data**.
3.  Libere as portas no Security Group:
    * `80` (HTTP)
    * `8080` (Core API)
    * `8081` (Auth API)
    * `8100` (Frontend)
4.  Inicie a instância. O script fará a instalação do Docker, clone do repositório e execução dos containers automaticamente.

> **Nota:** Devido a restrições de conta AWS no momento do desenvolvimento, a infraestrutura foi validada localmente simulando o ambiente de produção via Docker Compose.

---

## Autor

Roberlan Carvalho Desenvolvedor Fullstack & Especialista em IA

www.roberlancarvalho.com