# Vehicle Management System

A Spring Boot application for managing vehicles, owners, and plate numbers.

## Features

- User authentication with JWT
- Vehicle management
- Owner management
- Plate number management
- Ownership transfer
- Audit logging
- File uploads
- Email notifications
- Rate limiting
- Dockerized deployment
- Load balancing

## CRUD Operations

The application provides CRUD operations for various entities:

### Owners
- **Create**: POST `/api/v1/owners/add`
- **Read**: 
  - GET `/api/v1/owners/get-all` (paginated)
  - GET `/api/v1/owners/{id}` (by ID)
  - GET `/api/v1/owners/search?keyword=value` (search)
- **Update**: PUT `/api/v1/owners/{id}`
- **Delete**: DELETE `/api/v1/owners/{id}`

## Prerequisites

- Docker and Docker Compose
- Java 17 (for local development)
- Maven (for local development)

## Running the Application

### Using Docker Compose

1. Clone the repository
2. Navigate to the project directory
3. Build and start the containers:

```bash
docker-compose up -d
```

This will start:
- The Spring Boot application
- PostgreSQL database
- Redis cache
- Nginx load balancer

The application will be accessible at http://localhost:80

### Scaling the Application

To scale the application horizontally for load balancing:

#### On Linux/Mac:

```bash
chmod +x scale.sh
./scale.sh <number_of_instances>
```

#### On Windows:

```cmd
scale.bat <number_of_instances>
```

Replace `<number_of_instances>` with the desired number of application instances.

## Local Development

### Prerequisites

- Java 17
- Maven
- PostgreSQL
- Redis

### Configuration

Update the database and Redis configuration in `src/main/resources/application-dev.properties` for local development.

### Building and Running

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will be accessible at http://localhost:9094

## API Documentation

The API documentation is available at http://localhost:80/swagger-ui.html when the application is running.

## Environment Variables

The following environment variables can be set in the Docker environment:

- `SPRING_PROFILES_ACTIVE`: The active Spring profile (default: prod)
- `SPRING_DATASOURCE_URL`: The JDBC URL for the database
- `SPRING_DATASOURCE_USERNAME`: The database username
- `SPRING_DATASOURCE_PASSWORD`: The database password
- `SPRING_DATA_REDIS_HOST`: The Redis host
- `SPRING_DATA_REDIS_PORT`: The Redis port

## License

This project is licensed under the MIT License - see the LICENSE file for details.