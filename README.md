# Spring Boot JSON Streaming API

A memory-efficient Spring Boot application that streams large JSON data from PostgreSQL using NIO channels and GZIP compression.

## Features

- Memory-efficient streaming of large JSON data
- Direct streaming from PostgreSQL using NIO channels
- GZIP compression for reduced network transfer
- Zero-copy operations with DirectByteBuffer
- Proper resource cleanup and error handling

## Technical Stack

- Java 17
- Spring Boot 3.2.2
- PostgreSQL (with JSONB support)
- Spring Data JPA
- Hibernate
- Maven

## Prerequisites

- JDK 17 or later
- PostgreSQL 12 or later
- Maven 3.6 or later

## Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE foo_db;
```

2. Create a user with appropriate permissions:
```sql
CREATE USER foo_user WITH PASSWORD 'S3cret';
GRANT ALL PRIVILEGES ON DATABASE foo_db TO foo_user;
```

3. Create the table (automatically done by JPA, or use the provided SQL):
```sql
CREATE TABLE IF NOT EXISTS data_table (
    id BIGINT PRIMARY KEY,
    json_data JSONB
);
```

## Configuration

The application uses the following default configuration in `application.properties`:

```properties
server.port=8090
spring.datasource.url=jdbc:postgresql://localhost:5432/foo_db
spring.datasource.username=foo_user
spring.datasource.password=S3cret
```

## Building and Running

1. Build the project:
```bash
mvn clean install
```

2. Run with specific heap size:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms512m -Xmx1g"
```

## API Endpoints

### Get JSON Data by ID

```
GET /api/data/{id}
```

- Streams JSON data directly from the database
- Returns GZIP compressed response
- Memory-efficient for large JSON objects

Example:
```bash
curl -H "Accept-Encoding: gzip" http://localhost:8090/api/data/1
```

## Performance Characteristics

- Uses 8KB direct buffer per request
- Minimal memory footprint
- Efficient for parallel requests
- GZIP compression reduces network bandwidth

## Memory Usage Guidelines

- Base memory: ~100MB
- Per request: ~8KB buffer
- Recommended heap: 1GB for high concurrency

## Load Testing

Use Apache Bench for load testing:

```bash
ab -n 200 -c 10 http://localhost:8090/api/data/1
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 