# MSA PDF Generator

A reactive microservice for PDF generation using Spring WebFlux.

## Features

- Generate PDF reports based on date ranges
- Returns PDF as Base64 encoded string
- Reactive programming with Spring WebFlux
- OpenAPI 3.0 specification
- Built with Java 21 and Spring Boot 3

## API Endpoints

### Generate PDF Report
- **POST** `/api/pdf/generate`
- Generates a PDF report for the specified date range
- Returns the PDF as Base64 encoded content

#### Request Body
```json
{
  "fromDate": "2024-01-01T00:00:00Z",
  "toDate": "2024-12-31T23:59:59Z",
  "title": "Financial Report"
}
```

#### Response
```json
{
  "pdfBase64": "JVBERi0xLjQKMSAwIG9iago8PAovVHlwZSAvQ2F0YWxvZwovUGFnZXMgMiAwIFIKPj4KZW5kb2JqCg==",
  "fileName": "report_2024-01-01_to_2024-12-31.pdf",
  "generatedAt": "2024-09-14T18:45:00Z",
  "size": 102400
}
```

## Running the Application

1. Build the project:
   ```bash
   ./gradlew build
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The application will start on port 8081.

## API Documentation

Once the application is running, you can access the Swagger UI at:
- http://localhost:8081/swagger-ui.html

## Technologies Used

- Java 21
- Spring Boot 3.5.5
- Spring WebFlux
- iText 7 for PDF generation
- OpenAPI Generator
- Lombok
