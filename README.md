# cms Cinema Backend

## Stack
- Java 17 + Spring Boot 4.0.4
- MySQL 8.0
- Redis 7.2
- JWT (JJWT 0.12.6)
- Elastic Bean Search
- Cloudinary
- Docker + Docker Compose

## Run with Docker
```bash
docker-compose up --build
```

## API Base URL
http://localhost:8080/api

## Auth Endpoints
| Method | Path           | Description           |
|--------|----------------|-----------------------|
| POST   | /auth/register | Đăng ký               |
| POST   | /auth/login    | Đăng nhập (LOCAL/GOOGLE) |
| POST   | /auth/refresh  | Refresh token         |
| POST   | /auth/logout   | Đăng xuất             |
| GET    | /auth/me       | Thông tin user        |

Sử dụng @CurrentUser truyền vào param method của API để lấy user hiện tại
## Login Examples

### Local Login
```json
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "secret123",
  "provider": "LOCAL"
}
```

### Google Login
```json
POST /api/auth/login
{
  "idToken": "<google-id-token>",
  "provider": "GOOGLE"
}
```

Flow backend
Controller -> Interface -> Service -> Repository
