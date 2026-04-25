# Tài liệu API (API Documentation)

Đây là tài liệu chi tiết toàn bộ các API của hệ thống CMS Backend. Tiền tố API (API Prefix) mặc định cho các mô-đun chính là `/api/v1`.
**Lưu ý:** API liên quan đến Xác thực (Authentication) có tiền tố là `/auth`.

---

## 1. Authentication API (`/auth`)
Các API liên quan đến xác thực, đăng nhập, đăng ký và quản lý phiên của người dùng.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Đăng ký tài khoản Customer mới bằng email/password. |
| `POST` | `/auth/login` | Đăng nhập bằng tài khoản nội bộ (LOCAL) hoặc Google (GOOGLE). |
| `POST` | `/auth/refresh` | Làm mới Access Token, sử dụng Refresh Token. |
| `POST` | `/auth/logout` | Đăng xuất hiện tại và hủy token. |
| `GET` | `/auth/me` | Lấy thông tin của người dùng đang đăng nhập (dựa trên JWT). |

---

## 2. Movie API (`/api/v1/movies`)
Các API dùng để quản lý hệ thống phim.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/movies` | Lấy danh sách tất cả phim. |
| `GET` | `/api/v1/movies/{id}` | Lấy thông tin chi tiết phim theo ID. |
| `GET` | `/api/v1/movies/now-showing` | Lấy danh sách các phim hiện đang chiếu. |
| `GET` | `/api/v1/movies/coming-soon` | Lấy danh sách các phim sắp chiếu. |
| `GET` | `/api/v1/movies/search?name={name}` | Tìm kiếm phim theo tên. |
| `GET` | `/api/v1/movies/genre/{genre}`| Lấy danh sách phim theo thể loại nhất định. |
| `POST` | `/api/v1/movies` | Tạo mới thông tin một bộ phim. |
| `PUT` | `/api/v1/movies/{id}` | Cập nhật thông tin của phim. |
| `DELETE` | `/api/v1/movies/{id}` | Xóa bỏ một bộ phim. |

---

## 3. Catalog API (`/api/v1/catalog`)
Các API quản lý danh mục (Thể loại, Định dạng, và Diễn viên).

### 3.1. Thể loại (Genre)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/catalog/genres` | Lấy danh sách tất cả các thể loại. |
| `POST` | `/api/v1/catalog/genres?genre={name}` | Tạo mới một thể loại. |
| `DELETE` | `/api/v1/catalog/genres/{genre}` | Xóa một thể loại cụ thể. |

### 3.2. Định dạng (Format)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/catalog/formats` | Lấy danh sách tất cả định dạng phim. |
| `POST` | `/api/v1/catalog/formats?fName={name}` | Tạo mới một định dạng phim. |
| `DELETE` | `/api/v1/catalog/formats/{fName}` | Xóa một định dạng phim cụ thể. |

### 3.3. Diễn viên (Actor)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/catalog/actors` | Lấy danh sách tất cả các diễn viên. |
| `POST` | `/api/v1/catalog/actors?fullName={name}` | Tạo mới một diễn viên. |
| `DELETE` | `/api/v1/catalog/actors/{fullName}` | Xóa một diễn viên. |

---

## 4. Branch API (`/api/v1/branches`)
Các API dùng để quản lý chi nhánh và hệ thống phòng chiếu của chi nhánh.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/branches` | Lấy danh sách tất cả chi nhánh. |
| `GET` | `/api/v1/branches/{id}` | Lấy thông tin chi nhánh theo ID. |
| `GET` | `/api/v1/branches/search?name={name}` | Tìm kiếm chi nhánh theo tên. |
| `POST` | `/api/v1/branches` | Tạo mới một chi nhánh. |
| `PUT` | `/api/v1/branches/{id}` | Cập nhật thông tin chi nhánh. |
| `DELETE` | `/api/v1/branches/{id}` | Xóa một chi nhánh. |
| `GET` | `/api/v1/branches/{branchId}/rooms` | Lấy danh sách tất cả phòng chiếu trong chi nhánh. |
| `GET` | `/api/v1/branches/{branchId}/rooms/{roomId}`| Lấy thông tin chi tiết một phòng chiếu ở một chi nhánh.|
| `POST` | `/api/v1/branches/{branchId}/rooms` | Tạo mới một phòng chiếu trong chi nhánh. |
| `PUT` | `/api/v1/branches/{branchId}/rooms/{roomId}`| Cập nhật thông tin phòng chiếu. |
| `DELETE` | `/api/v1/branches/{branchId}/rooms/{roomId}`| Xóa một phòng chiếu. |
| `GET` | `/api/v1/branches/{branchId}/rooms/{roomId}/seats` | Lấy danh sách tất cả ghế trong phòng chiếu. |
| `POST` | `/api/v1/branches/{branchId}/rooms/{roomId}/seats` | Tạo mới một ghế. |
| `POST` | `/api/v1/branches/{branchId}/rooms/{roomId}/seats/bulk` | Tạo nhiều ghế cùng lúc. |
| `PUT` | `/api/v1/branches/{branchId}/rooms/{roomId}/seats/{sRow}/{sColumn}` | Cập nhật thông tin ghế. |
| `DELETE` | `/api/v1/branches/{branchId}/rooms/{roomId}/seats/{sRow}/{sColumn}` | Xóa một ghế. |

---

## 5. Showtime API (`/api/v1/showtimes`)
Các API để quản lý các suất chiếu phim.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/showtimes` | Lấy danh sách tất cả suất chiếu. |
| `GET` | `/api/v1/showtimes/{id}` | Lấy thông tin chi tiết suất chiếu theo ID. |
| `GET` | `/api/v1/showtimes/movie/{movieId}` | Lấy danh sách suất chiếu theo ID phim. |
| `GET` | `/api/v1/showtimes/movie/{movieId}/day?day={date}`| Lấy danh sách suất chiếu của phim vào ngày cụ thể. |
| `GET` | `/api/v1/showtimes/branch/{branchId}/day?day={date}`| Lấy danh sách suất chiếu tại một chi nhánh ở ngày. |
| `POST` | `/api/v1/showtimes` | Tạo mới một suất chiếu. |
| `PUT` | `/api/v1/showtimes/{id}` | Cập nhật thông tin suất chiếu. |
| `PATCH` | `/api/v1/showtimes/{id}/status?status={st}` | Cập nhật trạng thái suất chiếu. |
| `DELETE` | `/api/v1/showtimes/{id}` | Xóa một suất chiếu. |
| `GET` | `/api/v1/showtimes/{id}/seats` | Lấy danh sách ghế và trạng thái đặt chỗ của một suất chiếu. |

---

## 6. Food & Drink API (`/api/v1/food-drinks`)
Các API quản lý sản phẩm bắp nước.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/food-drinks` | Lấy danh sách tất cả sản phẩm bắp và nước. |
| `GET` | `/api/v1/food-drinks/{id}` | Lấy thông tin chi tiết sản phẩm theo ID. |
| `GET` | `/api/v1/food-drinks/type/{type}` | Lấy danh sách theo phân loại sản phẩm. |
| `GET` | `/api/v1/food-drinks/search?name={name}` | Tìm kiếm sản phẩm theo tên. |
| `POST` | `/api/v1/food-drinks` | Tạo mới một sản phẩm bắp, nước. |
| `PUT` | `/api/v1/food-drinks/{id}` | Cập nhật thông tin của sản phẩm. |
| `DELETE` | `/api/v1/food-drinks/{id}` | Xóa một sản phẩm. |

---

## 7. Merchandise API (`/api/v1/merchandise`)
Các API quản lý quà tặng, vật phẩm lưu niệm.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/merchandise` | Lấy danh sách tất cả vật phẩm. |
| `GET` | `/api/v1/merchandise/{id}` | Lấy thông tin vật phẩm theo ID. |
| `GET` | `/api/v1/merchandise/search?name={name}` | Tìm kiếm vật phẩm theo tên. |
| `POST` | `/api/v1/merchandise` | Tạo mới một vật phẩm. |
| `PUT` | `/api/v1/merchandise/{id}` | Cập nhật thông tin vật phẩm. |
| `DELETE` | `/api/v1/merchandise/{id}` | Xóa một vật phẩm. |

---

## 8. Coupon API (`/api/v1/coupons`)
Các API quản lý mã giảm giá.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/coupons` | Lấy danh sách tất cả coupon. |
| `GET` | `/api/v1/coupons/{id}` | Lấy thông tin coupon theo ID. |
| `POST` | `/api/v1/coupons` | Tạo mới một coupon. |
| `PUT` | `/api/v1/coupons/{id}` | Cập nhật thông tin coupon. |
| `DELETE` | `/api/v1/coupons/{id}` | Xóa một coupon. |

---

## 9. Customer API (`/api/v1/customers`)
Các API phục vụ quản lý thông tin khách hàng.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/customers` | Lấy danh sách tất cả khách hàng. |
| `GET` | `/api/v1/customers/{id}` | Lấy thông tin khách hàng theo ID. |
| `GET` | `/api/v1/customers/email?email={email}` | Lấy thông tin khách hàng bằng địa chỉ Email. |
| `PATCH` | `/api/v1/customers/{id}/deactivate` | Ngừng kích hoạt (deactivate) một khách hàng. |
| `PATCH` | `/api/v1/customers/{id}/activate` | Kích hoạt lại (activate) tài khoản khách hàng. |
| `DELETE` | `/api/v1/customers/{id}` | Xóa vĩnh viễn thông tin khách hàng. |

---

## 8. Order API (`/api/v1/orders`)
Các API quản lý các đơn đặt hàng vé, bắp nước.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/orders` | Lấy toàn bộ danh sách đơn đặt hàng. |
| `GET` | `/api/v1/orders/{id}` | Lấy thông tin chi tiết của một đơn hàng theo ID. |
| `GET` | `/api/v1/orders/email?email={email}` | Tìm đơn hàng thuộc về một Email. |
| `POST` | `/api/v1/orders` | Khởi tạo đơn đặt hàng (Yêu cầu có thông tin User). |

---

## 9. Payment API (`/api/v1/payments`)
Các API liên quan đến xử lý giao dịch và thanh toán (Ví dụ: VNPay, Momo)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/payments/subscribe?orderId={id}` | Mở luồng SSE nhận thông báo cho quá trình thanh toán. |
| `POST` | `/api/v1/payments` | Tạo yêu cầu thanh toán (Redirect URL cho VNPay, Momo,...). |
| `GET` | `/api/v1/payments/callback` | Redirect URL/Callback được gọi khi thanh toán hoàn thành. |

---

## 10. Employee API (`/api/v1/employees`)
Các API quản lý nhân sự (Employee/Staff).

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/employees` | Lấy danh sách toàn bộ nhân viên. |
| `GET` | `/api/v1/employees/{id}` | Lấy thông tin chi tiết của nhân viên theo ID. |
| `GET` | `/api/v1/employees/branch/{branchId}` | Lấy danh sách nhân viên đang làm việc ở một chi nhánh. |
| `POST` | `/api/v1/employees` | Tạo mới hồ sơ nhân viên. |
| `PUT` | `/api/v1/employees/{id}` | Sửa đổi, cập nhật thông tin nhân viên. |
| `PATCH` | `/api/v1/employees/{id}/deactivate` | Ngắt kích hoạt/vô hiệu hoá tài khoản nhân viên. |
| `DELETE` | `/api/v1/employees/{id}` | Xóa thông tin nhân viên khỏi hệ thống. |

---

## 11. File API (`/api/v1/files`)
Các API chịu trách nhiệm quản lý upload tài nguyên tĩnh như hình ảnh, video với Cloudinary v.v.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/files/upload?folderName={folder}` | Upload tệp (Hình ảnh, banner, poster) lên thư mục nhất định. |
| `POST` | `/api/v1/files/upload-video?folderName={f}` | Upload tệp chứa định dạng Video. |
