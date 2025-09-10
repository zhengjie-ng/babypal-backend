# BabyPal Backend API

A comprehensive baby tracking REST API built with Spring Boot that allows parents and caregivers to monitor and record their baby's development, activities, and health metrics.

## Features

### Core Functionality
- **User Management**: Secure user authentication and authorization with JWT tokens
- **Baby Profiles**: Create and manage multiple baby profiles per user
- **Activity Records**: Track daily activities (feeding, sleeping, diaper changes, etc.)
- **Growth Measurements**: Record and monitor weight, height, and head circumference
- **Growth Guides**: Access development milestones and guidance
- **Multi-User Support**: Multiple caregivers can manage the same baby profile

### Security Features
- JWT-based authentication
- Role-based access control (User and Admin roles)
- Password reset functionality
- CSRF protection
- Secure password validation

## Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 17
- **Database**: PostgreSQL (Production), H2 (Testing)
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA
- **Build Tool**: Maven
- **Additional Libraries**:
  - Lombok for boilerplate code reduction
  - Jackson for JSON processing
  - Jakarta Validation for input validation
  - Spring Boot Mail for email functionality

## API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration
- `POST /api/auth/signout` - User logout

### Baby Management
- `GET /api/babies` - Get all babies for authenticated user
- `POST /api/babies` - Create a new baby profile
- `GET /api/babies/{babyId}` - Get specific baby details
- `PUT /api/babies/{babyId}` - Update baby information
- `DELETE /api/babies/{babyId}` - Delete baby profile

### Activity Records
- `GET /api/records` - Get all records for user's babies
- `POST /api/records` - Create a new activity record
- `PUT /api/records/{recordId}` - Update existing record
- `DELETE /api/records/{recordId}` - Delete activity record

### Growth Measurements
- `GET /api/measurements` - Get all measurements for user's babies
- `POST /api/measurements` - Add new measurement
- `PUT /api/measurements/{measurementId}` - Update measurement
- `DELETE /api/measurements/{measurementId}` - Delete measurement

### Growth Guides
- `GET /api/growth-guides` - Get development guides and milestones

### User Management
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile

### Admin Functions
- `GET /api/admin/users` - Get all users (Admin only)
- Various admin management endpoints

## Data Models

### Baby
```java
- id: Long
- name: String (required, 3-100 characters)
- gender: String
- dateOfBirth: LocalDateTime
- weight: Double
- height: Double
- headCircumference: Double
- caregivers: List<String>
- owner: String
- createdAt/updatedAt: LocalDateTime
```

### Record
```java
- id: Long
- author: String
- type: String (required) // feeding, sleeping, diaper, etc.
- subType: String
- note: String
- startTime/endTime: LocalDateTime
- baby: Baby (Many-to-One relationship)
```

### Measurement
```java
- id: Long
- author: String
- time: LocalDateTime
- weight: Double
- height: Double
- headCircumference: Double
- baby: Baby (Many-to-One relationship)
```

## Setup and Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (for production)

### Environment Variables
Create a `.env` file in the root directory with:
```
DATABASE_URL=your_postgresql_url
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret_key
MAIL_USERNAME=your_email_username
MAIL_PASSWORD=your_email_password
```

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd babypal-backend
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at `http://localhost:8080`

### Running Tests
```bash
mvn test
```

## Database Configuration

The application supports multiple database configurations:
- **Production**: PostgreSQL
- **Testing**: H2 in-memory database
- **Development**: Configurable via environment variables

Database tables are automatically created using JPA annotations.

## Security Configuration

- JWT tokens for stateless authentication
- Password encryption using BCrypt
- CORS configuration for frontend integration
- Role-based access control with USER and ADMIN roles
- Secure endpoints with proper authorization checks

## Development Features

- **Spring Boot DevTools** for hot reloading during development
- **Comprehensive validation** on all input data
- **Automatic timestamps** for all entities
- **Lombok integration** for clean, readable code
- **Detailed error handling** with proper HTTP status codes

## Deployment

The application includes:
- Docker configuration (`Dockerfile`)
- AWS Elastic Beanstalk configuration (`.ebextensions/`)
- CircleCI pipeline configuration (`.circleci/`)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the terms specified in the LICENSE file.

## API Documentation

For detailed API documentation with request/response examples, consider integrating Swagger/OpenAPI documentation or refer to the controller classes in `src/main/java/com/babypal/controllers/`.