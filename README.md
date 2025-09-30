# PsicoAgenda API

## Descrição do Projeto

O **PsicoAgenda API** é um sistema de backend, desenvolvido em Spring Boot, para a gestão e agendamento de consultas psicológicas. A aplicação é segmentada para atender as necessidades de dois perfis de usuário principais: **Psicólogos** e **Pacientes**. O sistema gerencia perfis de usuário, horários de disponibilidade.

## Funcionalidades Principais

* **Autenticação JWT:** Sistema seguro de login e controle de acesso baseado em JWT.
* **Gestão de Perfis:** Cadastro e manipulação de perfis de Psicólogos e Pacientes, com validação e autorização baseada em *role*.
* **Gestão de Disponibilidade:** Permite que Psicólogos cadastrem horários recorrentes ou pontuais.
    * A disponibilidade é validada automaticamente antes da criação de qualquer novo agendamento.
* **Fluxo de Agendamento:**
    * Controle de Status do Agendamento: `SCHEDULED`, `CANCELED`, `COMPLETED`, `NO_SHOW`.
    * Regras de Autorização: Pacientes só podem cancelar seus agendamentos, enquanto Psicólogos podem alterar o status para qualquer opção.
* **Tratamento de Exceções:** Manipulação centralizada de erros (Validação, Not Found, Acesso Negado, etc.) com respostas padronizadas em JSON.

## ️ Tecnologias Utilizadas

| Categoria | Tecnologia | Detalhes |
| :--- | :--- | :--- |
| **Backend** | Java 21 | Linguagem principal |
| **Framework** | Spring Boot 3.3.3 | Core da aplicação |
| **Dados** | Spring Data JPA | Para acesso e persistência de dados |
| **Banco de Dados** | PostgreSQL | Utilizado como SGBD |
| **Segurança** | Spring Security | Controle de autenticação e autorização |
| **Token** | JJWT 0.11.5 | Geração e validação de tokens JWT |
| **Criptografia** | BCryptPasswordEncoder | Para o hash seguro de senhas |
| **Documentação** | Springdoc OpenAPI | Geração automática da interface Swagger UI |


##  Como Rodar o Projeto

### Pré-requisitos

Para executar a aplicação localmente, você precisa ter instalado:

* **Java Development Kit (JDK) 21**
* **Docker** e **Docker Compose**
* **Maven** (O projeto inclui o Maven Wrapper, `mvnw`)

### 1. Configuração do Banco de Dados

A configuração do banco de dados está definida no `docker-compose.yml`, utilizando a imagem oficial do PostgreSQL 14.

1.  **Inicie o Contêiner do Banco de Dados:**

    ```bash
    docker-compose up -d db
    ```

    **Credenciais do PostgreSQL (padrão):**
    * **URL:** `jdbc:postgresql://localhost:5432/psicoagenda_db`
    * **User:** `postgres`
    * **Password:** `postgres`

### 2. Execução

Use o Maven Wrapper (`mvnw`) para construir e executar a aplicação:

1.  **Construa o projeto:**
    ```bash
    ./mvnw clean install
    ```
2.  **Inicie a aplicação Spring Boot:**
    ```bash
    ./mvnw spring-boot:run
    # Ou, alternativamente, execute o JAR gerado:
    # java -jar target/psicoagendaapi-0.0.1-SNAPSHOT.jar
    ```

A API estará acessível em `http://localhost:8080`.

## Endpoints da API

A documentação completa da API (Swagger UI) está disponível em: `http://localhost:8080/swagger-ui.html`.

| Recurso | Método | Rota | Autorização | Restrições de Acesso/Serviço |
| :--- | :--- | :--- | :--- | :--- |
| **Autenticação** | `POST` | `/auth/login` | `permitAll` | Retorna um `LoginResponseDTO` contendo o JWT. |
| **Psicólogos** | `POST` | `/psicologos` | `permitAll` | Cria o perfil de Psicólogo e a conta de Usuário. |
| | `GET` | `/psicologos` | `permitAll` | Lista todos os psicólogos (permite filtro por nome). |
| | `DELETE`| `/psicologos/{id}` | `ROLE_PSICOLOGO` | Só permite soft delete do próprio perfil e realiza soft delete em cascata (Disponibilidades, Agendamentos, User). |
| **Pacientes** | `POST` | `/pacientes` | `permitAll` | Cria o perfil de Paciente e a conta de Usuário. |
| | `GET` | `/pacientes` | `ROLE_PSICOLOGO` | Lista todos os pacientes cadastrados. |
| | `GET` | `/pacientes/{id}` | `ROLE_PSICOLOGO`, `ROLE_PACIENTE` | Paciente só pode visualizar o próprio perfil. |
| **Disponibilidades** | `POST` | `/disponibilidades` | `ROLE_PSICOLOGO` | O psicólogo só pode cadastrar disponibilidades para seu próprio ID. |
| | `GET` | `/disponibilidades/psicologo/{id}` | `permitAll` | Lista horários disponíveis para um psicólogo. |
| **Agendamentos** | `POST` | `/agendamentos` | `ROLE_PSICOLOGO`, `ROLE_PACIENTE` | É validado se o `PsicologoId` e `PacienteId` pertencem ao usuário autenticado (ou é um Psicólogo). **Valida se há disponibilidade antes de salvar**. |
| | `GET` | `/agendamentos` | `ROLE_PSICOLOGO`, `ROLE_PACIENTE` | Lista agendamentos do usuário autenticado (como prestador ou paciente). |
| | `PATCH` | `/agendamentos/{id}` | `ROLE_PSICOLOGO`, `ROLE_PACIENTE` | **Regra de Status:** Pacientes só podem alterar o status para `CANCELED`. |

