# BabyPal Backend API

A comprehensive baby tracking REST API built with Spring Boot that allows parents and caregivers to monitor and record their baby's development, activities, and health metrics.

## 🏗️ Infrastructure Overview

- **Database**: PostgreSQL hosted on [Neon](https://neon.tech/) (Serverless PostgreSQL)
- **Application Hosting**: AWS Elastic Beanstalk
- **Content Delivery**: AWS CloudFront CDN
- **Container Registry**: AWS Elastic Container Registry (ECR)
- **CI/CD Pipeline**: CircleCI with automated testing and deployment

## Features

### Core Functionality
- **User Management**: Secure user authentication and authorization with JWT tokens
- **Baby Profiles**: Create and manage multiple baby profiles per user
- **Activity Records**: Track daily activities (feeding, sleeping, diaper changes, etc.)
- **Growth Measurements**: Record and monitor weight, height, and head circumference
- **Growth Guides**: Access development milestones and guidance
- **Multi-User Support**: Multiple caregivers can manage the same baby profile

### Security Features
- **JWT-based authentication** with secure token management
- **Two-Factor Authentication (2FA)** with Google Authenticator support
- **Role-based access control** (User and Admin roles)
- **Password reset functionality** with secure email tokens
- **Credentials update** with automatic expiry extension
- **CSRF protection** for secure form submissions
- **Comprehensive logging** for authentication and security events
- **Secure password validation** and encryption

## Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 21 (Eclipse Temurin)
- **Database**: PostgreSQL hosted on Neon (Production), H2 (Testing)
- **Security**: Spring Security with JWT + 2FA
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven 3.6+
- **Containerization**: Docker with multi-stage builds
- **Cloud Platform**: AWS (Elastic Beanstalk, CloudFront, ECR)
- **Additional Libraries**:
  - Lombok for boilerplate code reduction
  - Jackson for JSON processing
  - Jakarta Validation for input validation
  - Spring Boot Mail for email functionality
  - Google Authenticator for 2FA implementation
  - Warrenstrange GoogleAuth for TOTP generation

## API Endpoints

### Authentication
- `POST /api/auth/public/signin` - User login
- `POST /api/auth/public/signup` - User registration  
- `POST /api/auth/signout` - User logout
- `GET /api/auth/user` - Get current user details
- `GET /api/auth/username` - Get current username
- `POST /api/auth/public/forgot-password` - Request password reset
- `POST /api/auth/public/reset-password` - Reset password with token
- `POST /api/auth/update-credentials` - Update email and password

### Two-Factor Authentication
- `POST /api/auth/enable-2fa` - Generate QR code for 2FA setup
- `POST /api/auth/verify-2fa` - Verify and enable 2FA
- `POST /api/auth/disable-2fa` - Disable 2FA for user
- `GET /api/auth/user/2fa-status` - Check 2FA status
- `POST /api/auth/public/verify-2fa-login` - Verify 2FA during login

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
- `DELETE /api/users/profile` - Delete user account

### Admin Functions
- `GET /api/admin/users` - Get all users (Admin only)
- `POST /api/admin/users/{userId}/lock` - Lock/unlock user account
- `POST /api/admin/users/{userId}/expire` - Set account expiry status
- `POST /api/admin/users/{userId}/enable` - Enable/disable user account
- `POST /api/admin/users/{userId}/credentials-expiry` - Set credentials expiry
- `POST /api/admin/users/{userId}/update-role` - Update user role
- `POST /api/admin/users/{userId}/update-password` - Admin password reset

### Utility Endpoints
- `GET /api/csrf` - Get CSRF token
- `GET /api/sample/all` - Public test endpoint

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
- **Java 21** (Eclipse Temurin JDK)
- **Maven 3.6+**
- **Docker & Docker Compose** (for local development)
- **Neon PostgreSQL Database** (for production)
- **Git** (for version control)

### Environment Variables
Create a `.env` file in the root directory with:
```bash
# Database Configuration (Neon PostgreSQL)
DATABASE_URL=postgresql://username:password@ep-hostname.region.aws.neon.tech/dbname?sslmode=require

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_minimum_256_bits

# Email Configuration
MAIL_USERNAME=your_smtp_username
MAIL_PASSWORD=your_smtp_app_password

# Application Configuration
FRONTEND_URL=http://localhost:3000
```

### Neon Database Setup
1. Create account at [Neon](https://console.neon.tech/)
2. Create new PostgreSQL database
3. Copy connection string to `DATABASE_URL` environment variable
4. Database tables will be auto-created via JPA/Hibernate

### Running the Application

#### Option 1: Local Development with Maven
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd babypal-backend
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Install dependencies**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at `http://localhost:8080`

#### Option 2: Docker Development
1. **Using Docker Compose**
   ```bash
   docker-compose up --build
   ```
   
2. **Using Docker directly**
   ```bash
   docker build -t babypal-backend .
   docker run -p 8080:8080 --env-file .env babypal-backend
   ```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run with coverage
mvn test jacoco:report
```

## Database Configuration

### Neon PostgreSQL (Production)
- **Provider**: [Neon](https://neon.tech/) - Serverless PostgreSQL
- **Features**: 
  - Auto-scaling compute
  - Instant branching for development
  - Point-in-time recovery
  - SSL/TLS encryption
- **Connection**: SSL required for security
- **Schema Management**: Auto-created via JPA/Hibernate

### Development & Testing
- **Testing**: H2 in-memory database for unit tests
- **Local Development**: Configurable via environment variables
- **Docker**: PostgreSQL container via docker-compose

### Database Schema
Database tables are automatically created using JPA annotations with proper:
- Foreign key relationships
- Indexes for performance
- Validation constraints
- Audit timestamps (createdAt, updatedAt)

## Security Configuration

### Authentication & Authorization
- **JWT tokens** for stateless authentication with configurable expiration
- **Two-Factor Authentication** using Google Authenticator (TOTP)
- **Role-based access control** with USER and ADMIN roles
- **Password encryption** using BCrypt with salt rounds
- **Secure endpoints** with proper authorization checks

### Security Features
- **CORS configuration** for cross-origin resource sharing
- **CSRF protection** with token-based validation
- **Password policies** with strength requirements
- **Account lockout** mechanism for failed login attempts
- **Session management** with automatic timeout
- **Audit logging** for all authentication events

### Infrastructure Security
- **SSL/TLS encryption** for database connections
- **Environment variable** based configuration (no hardcoded secrets)
- **Docker security** with non-root user execution
- **AWS security groups** and VPC configuration

## Development Features

- **Spring Boot DevTools** for hot reloading during development
- **Comprehensive validation** on all input data
- **Automatic timestamps** for all entities
- **Lombok integration** for clean, readable code
- **Detailed error handling** with proper HTTP status codes

## Deployment & Infrastructure

### AWS Elastic Beanstalk Hosting
- **Application Platform**: Docker platform on Elastic Beanstalk
- **Auto Scaling**: Automatically scales based on CPU/memory usage
- **Load Balancing**: Application Load Balancer with health checks
- **Rolling Deployments**: Zero-downtime deployment strategy
- **Environment Management**: Separate staging/production environments

### AWS CloudFront CDN
- **Global Content Delivery**: Edge locations worldwide for low latency
- **SSL/TLS Termination**: Automatic HTTPS certificate management
- **Caching Strategy**: Optimized caching for API responses
- **Origin Protection**: CloudFront-to-ALB secure communication

### AWS Elastic Container Registry (ECR)
- **Private Registry**: Secure Docker image storage
- **Image Scanning**: Automatic vulnerability scanning
- **Lifecycle Policies**: Automated image cleanup and retention
- **IAM Integration**: Fine-grained access control

### CI/CD Pipeline (CircleCI)
The deployment pipeline includes:
- **Security Scanning**: GitLeaks for secret detection
- **Code Quality**: Snyk SAST for vulnerability analysis  
- **Container Security**: Docker image vulnerability scanning
- **Automated Testing**: Maven test suite execution
- **Multi-Stage Build**: Optimized Docker image creation
- **Automated Deployment**: Direct deployment to AWS Elastic Beanstalk

### Infrastructure Components
- **Database**: Neon PostgreSQL (Serverless)
- **Application**: AWS Elastic Beanstalk
- **CDN**: AWS CloudFront
- **Container Registry**: AWS ECR
- **CI/CD**: CircleCI
- **Monitoring**: AWS CloudWatch (built-in)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the terms specified in the LICENSE file.

## API Documentation

For detailed API documentation with request/response examples, refer to:
- **Controller Classes**: `src/main/java/com/babypal/controllers/`
- **DTOs & Models**: `src/main/java/com/babypal/models/`
- **Security Configuration**: `src/main/java/com/babypal/security/`

### Sample API Usage

#### Authentication Flow
```bash
# 1. User Registration
curl -X POST http://localhost:8080/api/auth/public/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"SecurePass123!"}'

# 2. User Login
curl -X POST http://localhost:8080/api/auth/public/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"SecurePass123!"}'

# 3. Access Protected Endpoint
curl -X GET http://localhost:8080/api/auth/user \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Performance & Monitoring

### Application Metrics
- **Health Checks**: `/actuator/health` endpoint
- **Application Info**: `/actuator/info` endpoint  
- **Metrics Collection**: Micrometer integration ready
- **Logging**: Structured logging with correlation IDs

### Database Performance
- **Connection Pooling**: HikariCP for optimal database connections
- **Query Optimization**: JPA/Hibernate query optimization
- **Index Strategy**: Strategic database indexing for performance

## Documentation

For comprehensive documentation, see:
- **[DEVOPS.md](DEVOPS.md)** - DevOps implementation details
- **API Controllers** - In-code documentation and examples
- **Security Guide** - Authentication and authorization details