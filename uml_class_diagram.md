# DATH-CMS-BE UML Class Diagram

Mô hình lớp (UML Class Diagram) của các core entities trong database của hệ thống DATH-CMS-BE. Đã cập nhật mới nhất kèm các Enum tách rời và tích hợp Coupon vào Order.

```mermaid
classDiagram
    %% ===== BOOKING MODULE =====
    class Coupon {
        +Integer couponId
        +LocalDate startDate
        +LocalDate endDate
        +Integer saleOff
        +Integer releaseNum
        +Integer availNum
        +Boolean isActive
    }

    class Order {
        +Integer orderId
        +LocalDateTime orderTime
        +String paymentMethod
        +BigDecimal originalTotal
        +BigDecimal discountAmount
        +BigDecimal total
        +EOrderStatus orderStatus
    }

    class Payment {
        +Integer paymentId
        +BigDecimal amount
        +EPaymentMethod paymentMethod
        +EPaymentStatus paymentStatus
        +String transactionId
        +LocalDateTime paymentTime
    }

    class PaymentHistory {
        +Integer historyId
        +EPaymentStatus paymentStatus
        +BigDecimal amount
        +String transactionId
        +String responseCode
        +String responseMessage
        +String rawResponse
        +LocalDateTime createdAt
    }
    
    %% ===== CINEMA MODULE =====
    class Branch {
        +Integer branchId
        +String bName
        +String bAddress
        +List~String~ phoneNumbers
    }

    class ScreenRoom {
        +ScreenRoomId id
        +ERType rType
        +Integer rCapacity
    }
    
    class Seat {
        +SeatId id
        +Integer sType
        +Boolean sStatus
    }
    
    %% ===== CUSTOMER MODULE =====
    class Customer {
        +String cUserId
        +String cName
        +ESex sex
        +String phoneNumber
        +String email
        +String ePassword
        +UserType userType
        +AuthProviderType authProvider
        +String providerId
        +String avatarUrl
        +boolean isActive
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    class Membership {
        +Integer memberId
        +Integer point
        +ERank memberRank
    }
    
    %% ===== MOVIE MODULE =====
    class Actor {
        +String fullName
    }
    class Format {
        +String fName
    }
    class Genre {
        +String genre
    }
    class Movie {
        +Integer movieId
        +String mName
        +String descript
        +Integer runTime
        +Boolean isDub
        +Boolean isSub
        +LocalDate releaseDate
        +LocalDate closingDate
        +String ageRating
        +String posterUrl
        +String trailerUrl
    }
    class Review {
        +ReviewId id
        +Integer rating
        +LocalDate rDate
        +String comment
    }
    
    %% ===== PRODUCTS MODULE =====
    class AddonItem {
        +Integer productId
        +BigDecimal price
        +String itemType
    }
    class FoodDrink {
        +String pType
        +String pName
        +Integer quantity
    }
    class Merchandise {
        +Integer availNum
        +String merchName
        +LocalDate startDate
        +LocalDate endDate
    }
    
    %% ===== SCREENING MODULE =====
    class Showtime {
        +Integer timeId
        +LocalDate day
        +LocalTime startTime
        +LocalTime endTime
        +EShowtimeStatus status
    }
    class Ticket {
        +Integer ticketId
        +LocalDate daySold
        +BigDecimal tPrice
        +String qrCode
        +ETicketStatus ticketStatus
    }
    
    %% ===== STAFF MODULE =====
    class Employee {
        +String eUserId
        +String eName
        +ESex sex
        +String phoneNumber
        +String email
        +String ePassword
        +BigDecimal salary
        +UserType userType
        +boolean isActive
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }
    class WorkShift {
        +WorkShiftId id
        +String work
    }

    %% ===== RELATIONSHIPS =====
    
    %% Booking & Payment Relations
    Order "*" --> "1" Customer : customer
    Order "*" --> "1" Employee : employee
    Order "*" --> "1" Coupon : coupon
    Order "1" --> "*" Ticket : tickets
    Order "1" --> "*" AddonItem : addonItems
    Payment "1" --> "1" Order : order
    Payment "1" --> "*" PaymentHistory : histories
    
    %% Cinema Relations
    Branch "*" --> "1" Employee : manager
    Branch "1" --> "*" ScreenRoom : screenRooms
    ScreenRoom "*" --> "1" Branch : branch
    ScreenRoom "1" --> "*" Seat : seats
    Seat "*" --> "1" ScreenRoom : screenRoom
    
    %% Customer Relations
    Customer "1" --> "1" Membership : membership
    
    %% Movie Relations
    Movie "*" --> "*" Genre : genres
    Movie "*" --> "*" Format : formats
    Movie "*" --> "*" Actor : actors
    Movie "1" --> "*" Review : reviews
    Review "*" --> "1" Movie : movie
    Review "*" --> "1" Customer : customer
    
    %% Product Relations (Inheritance and Orders)
    AddonItem <|-- FoodDrink
    AddonItem <|-- Merchandise
    AddonItem "*" --> "1" Order : order
    
    %% Screening Relations
    Showtime "*" --> "1" Format : format
    Showtime "*" --> "1" Movie : movie
    Showtime "*" --> "1" ScreenRoom : screenRoom
    Showtime "1" --> "*" Ticket : tickets
    Ticket "*" --> "1" Showtime : showtime
    Ticket "*" --> "1" Order : order
    Ticket "*" --> "1" Seat : seat
    
    %% Staff Relations
    Employee "*" --> "1" Employee : manager
    Employee "1" --> "*" Employee : subordinates
    Employee "*" --> "1" Branch : branch
    Employee "*" --> "*" WorkShift : workShifts

```
