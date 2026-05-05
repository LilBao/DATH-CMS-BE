package com.cms.service.booking;

import com.cms.common.exception.AppException;
import com.cms.dto.request.OrderRequest;
import com.cms.dto.response.AddonResponse;
import com.cms.dto.response.OrderResponse;
import com.cms.dto.response.TicketResponse;
import com.cms.entity.booking.Coupon;
import com.cms.entity.booking.Order;
import com.cms.entity.cinema.Seat;
import com.cms.entity.cinema.SeatId;
import com.cms.entity.customer.Customer;
import com.cms.entity.products.AddonItem;
import com.cms.entity.products.FoodDrink;
import com.cms.entity.screening.Showtime;
import com.cms.entity.screening.Ticket;
import com.cms.entity.staff.Employee;
import com.cms.enums.EOrderStatus;
import com.cms.enums.ETicketStatus;
import com.cms.repository.booking.CouponRepository;
import com.cms.repository.booking.OrderRepository;
import com.cms.repository.cinema.SeatRepository;
import com.cms.repository.customer.CustomerRepository;
import com.cms.repository.screening.ShowtimeRepository;
import com.cms.repository.staff.EmployeeRepository;
import com.cms.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cms.util.ValidationUtil.isNull;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final CouponRepository couponRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll(EOrderStatus status) {
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findByOrderStatus(status);
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(String id) {
        Order order = orderRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> AppException.notFound("Không tìm thấy Order: ", id));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getByEmail(String email) {
        Order order = orderRepository.findByCustomerEmail(email)
                .stream()
                .findFirst()
                .orElseThrow(() -> AppException.notFound("Không tìm thấy Order cho email: ", email));
        return mapToResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(UserDetails userDetails, OrderRequest request) {
        if (isNull(userDetails)) {
            throw AppException.unauthorized("Vui lòng đăng nhập.");         
        }

        String email = userDetails.getUsername();
        Customer customer = customerRepository.findByEmail(email).orElse(null);
        Employee employee = employeeRepository.findByEmail(email).orElse(null);

        if (customer == null && employee == null) {
            throw AppException.unauthorized("Người dùng không hợp lệ.");
        }

        Order order = new Order();
        if (customer != null) {
            order.setCustomer(customer);
        } else {
            order.setEmployee(employee);
        }

        order.setPaymentMethod(request.getPaymentMethod());
        order.setOrderStatus(EOrderStatus.PENDING);

        BigDecimal originalTotal = BigDecimal.ZERO;
        
        List<Ticket> tickets = new ArrayList<>();
        if (request.getTickets() != null) {
            for (OrderRequest.TicketRequest tr : request.getTickets()) {
                Showtime showtime = showtimeRepository.findById(tr.getShowtimeId())
                        .orElseThrow(() -> AppException.notFound("Không tìm thấy suất chiếu.", tr.getShowtimeId()));
                
                SeatId seatId = new SeatId(tr.getBranchId(), tr.getRoomId(), tr.getSRow(), tr.getSColumn());
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> AppException.notFound("Ghế không tồn tại.", seatId));
                
                BigDecimal p = tr.getTPrice();
                if (p == null) {
                    BigDecimal base = seat.getScreenRoom().getBasePrice() != null ? seat.getScreenRoom().getBasePrice() : BigDecimal.ZERO;
                    BigDecimal surcharge = seat.getSPrice() != null ? seat.getSPrice() : BigDecimal.ZERO;
                    p = base.add(surcharge);
                }
                originalTotal = originalTotal.add(p);
                
                Ticket t = Ticket.builder()
                        .daySold(LocalDate.now())
                        .tPrice(p)
                        .qrCode("QR-" + System.currentTimeMillis() + "-" + seatId.getSRow() + seatId.getSColumn())
                        .ticketStatus(ETicketStatus.SOLD)
                        .showtime(showtime)
                        .seat(seat)
                        .order(order)
                        .build();
                tickets.add(t);
            }
        }
        order.setTickets(tickets);
        
        List<AddonItem> addons = new ArrayList<>();
        if (request.getAddons() != null) {
            for (OrderRequest.AddonItemRequest ar : request.getAddons()) {
                BigDecimal p = ar.getPrice() != null ? ar.getPrice() : BigDecimal.ZERO;
                Integer q = ar.getQuantity() != null ? ar.getQuantity() : 1;
                originalTotal = originalTotal.add(p.multiply(new BigDecimal(q)));

                FoodDrink fd = new FoodDrink();
                fd.setPrice(p);
                fd.setItemType("FOOD_DRINK");
                fd.setPType(ar.getPType());
                fd.setPName(ar.getPName());
                fd.setQuantity(q);
                fd.setOrder(order);
                addons.add(fd);
            }
        }
        order.setAddonItems(addons);

        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getCouponId() != null) {
            Coupon coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(() -> AppException.badRequest("Mã giảm giá không tồn tại."));
            
            LocalDate now = LocalDate.now();
            if (!coupon.getIsActive() || coupon.getStartDate().isAfter(now) || coupon.getEndDate().isBefore(now)) {
                throw AppException.badRequest("Mã giảm giá đã hết hạn hoặc chưa khả dụng.");
            }
            if (coupon.getAvailNum() != null && coupon.getAvailNum() <= 0) {
                throw AppException.badRequest("Mã giảm giá đã hết lượt sử dụng.");
            }

            BigDecimal percent = new BigDecimal(coupon.getSaleOff()).divide(new BigDecimal(100));
            discountAmount = originalTotal.multiply(percent);

            order.setCoupon(coupon);

            if (coupon.getAvailNum() != null) {
                coupon.setAvailNum(coupon.getAvailNum() - 1);
                couponRepository.save(coupon);
            }
        }

        BigDecimal finalTotal = originalTotal.subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
            discountAmount = originalTotal;
        }

        order.setOriginalTotal(originalTotal);
        order.setDiscountAmount(discountAmount);
        order.setTotal(finalTotal);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = modelMapper.map(order, OrderResponse.class);
        if (order.getOrderStatus() != null) {
            response.setOrderStatus(order.getOrderStatus().name());
        }

        if (order.getTickets() != null) {
            response.setTicketDetails(order.getTickets().stream().map(t -> {
                TicketResponse tr = new TicketResponse();
                tr.setTicketId(t.getTicketId());
                if (t.getShowtime() != null) {
                    if (t.getShowtime().getMovie() != null) {
                        tr.setMovieName(t.getShowtime().getMovie().getMName());
                    }
                    if (t.getShowtime().getDay() != null && t.getShowtime().getStartTime() != null) {
                        tr.setShowtime(LocalDateTime.of(t.getShowtime().getDay(), t.getShowtime().getStartTime()));
                    }
                    if (t.getShowtime().getScreenRoom() != null) {
                        tr.setScreenRoomName("Room " + t.getShowtime().getScreenRoom().getId().getRoomId());
                        if (t.getShowtime().getScreenRoom().getBranch() != null) {
                            tr.setBranchName(t.getShowtime().getScreenRoom().getBranch().getBName());
                        }
                    }
                }
                if (t.getSeat() != null && t.getSeat().getId() != null) {
                    tr.setSeatName(t.getSeat().getId().getSRow() + "-" + t.getSeat().getId().getSColumn());
                }
                tr.setPrice(t.getTPrice());
                return tr;
            }).collect(Collectors.toList()));
        }

        if (order.getAddonItems() != null) {
            response.setAddonDetails(order.getAddonItems().stream().map(a -> {
                AddonResponse ar = new AddonResponse();
                ar.setProductId(a.getProductId());
                ar.setItemType(a.getItemType());
                ar.setPrice(a.getPrice());
                if (a instanceof FoodDrink) {
                    FoodDrink fd = (FoodDrink) a;
                    ar.setPName(fd.getPName());
                    ar.setQuantity(fd.getQuantity());
                }
                return ar;
            }).collect(Collectors.toList()));
        }

        return response;
    }
}
