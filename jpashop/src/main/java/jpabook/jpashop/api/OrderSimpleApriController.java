package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApriController {
    
    private final OrderRepository orderRepository;
    private final OrderSimpleRepository orderSimpleRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        return all;
        // 문제1. 무한 루프 --> @JsonIgnore
        // 문제2. 지연로딩 -> Proxy객체 생성 = ByteBuddyInterceptor

        // >>> Hibernate5Module 라이브러리 설치해서 사용해보기 --> JpashopApplication.java 에 Bean으로 추가됨
    }

    @GetMapping(value="/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        return orderRepository.findAllByCriteria(new OrderSearch())
                                .stream().map(SimpleOrderDto::new)
                                .collect(Collectors.toList());
        
        // 문제: 쿼리가 너무 많이 나가서 시간이 걸림
    }
    
    @GetMapping(value="/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        // 패치 조인
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        //--> 쿼리가 1번만 딱 나감!유리!!유리!!
    }

    @GetMapping(value = "/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return orderSimpleRepository.findOrderDtos();
    }
    
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();     // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화
        }
    }
    
}