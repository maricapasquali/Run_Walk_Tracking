# Run/Walk Tracking Server

A PHP/MySQL server-side component for data synchronization through RESTful APIs
for the **Run/Walk Tracking** mobile application.

---

## Technologies Used

- **PHP 7.4+**
- **MySQL** managed via **phpMyAdmin**
- **Composer**
- **Docker** and **Docker Compose**

---

## Structure repository

📁 server  
├── 📁 apache/  # Configuration file for Apache HTTP Server  
├── 📁 config/  # General configuration files (e.g. database, mailer, server)  
├── 📁 mysql/ # SQL scripts and configurations for MySQL DB  
├── 📁 public/  # Public directory  
├── 📁 src/ # Application source code  
├── 📄 composer.json  # Project's PHP dependencies, class autoloading, and the configurations  
├── 📄 docker-compose.dev.yml  #Defining Docker services in development  
├── 📄 docker-compose.prod.yml  #Defining Docker services in production  
├── 📄 Dockerfile # Building a custom Docker image  
└── 📄 README.md

---

## Documentation of ENDPOINTS

The [Swagger OpenAPI](https://swagger.io/specification/) standard was used.  
Full documentation available [here](./public/docs/endpoints-docs.yaml) or the following url: http://localhost:80/docs 

---

## Configuration

Before deploying the project, make sure to create the following .env files inside the config folder:
`database.env`, `mailer.env`, and `server.env`.

Each file must include the respective environment variables as shown below.

### database.env

```
MYSQL_ROOT_PASSWORD=
MYSQL_HOST=      # Default: localhost
MYSQL_DATABASE=  # Default: runwalktracking
MYSQL_USER=
MYSQL_PASSWORD=
```

### mailer.env

```
MAILER_HOST=   # Default: smtp.gmail.com
MAILER_PORT=   # Default: 587
MAILER_USER=
MAILER_PASSWORD=
```

### server.env
```
SERVER_URL=   # Default: http://localhost:80
```

---

## Deploy with Docker Compose

Clone the repository, navigate to the server directory and deploy with `docker compose`:

```bash
git clone https://github.com/maricapasquali/Run_Walk_Tracking.git
cd run_walk_tracking/server
docker compose -f docker-compose.prod.yml up -d
```

---
