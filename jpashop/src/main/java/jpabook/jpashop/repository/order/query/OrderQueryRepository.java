package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;


    // ver.4
	public List<OrderQueryDto> findOrderQueryDtos() {
       List<OrderQueryDto> result = findOrders(); // query 1번 -> N번

       result.forEach(o -> {
           List<OrderitemQueryDto> orderItems = findOrderItems(o.getOrderId()); // query N번
           o.setItems(orderItems);
       });
       return result;
    }
    
    private List<OrderitemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderitemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi"+
                                " join oi.item i"+
                                " where oi.order.id = :orderId", OrderitemQueryDto.class)
                                .setParameter("orderId", orderId)
                                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" + 
                " from Order o" + 
                " join o.member m" + 
                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    // ver.5
	public List<OrderQueryDto> findAllByDto_optimaization() {
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = result.stream()
                            .map(o -> o.getOrderId())
                            .collect(Collectors.toList());
    
        List<OrderitemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderitemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi"+
                " join oi.item i"+
                " where oi.order.id in :orderIds", OrderitemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        Map<Long, List<OrderitemQueryDto>> orderItemMap = orderItems
                                        .stream()
                                        .collect(Collectors
                                                .groupingBy(OrderitemQueryDto -> OrderitemQueryDto.getOrderId()));
        
        result.forEach(o -> o.setItems(orderItemMap.get(o.getOrderId())));

        return result;

    }


    // ver.6
	public List<OrderFlatDto> findAllByDto_flat() {
        // 페이징 안됨
        return em.createQuery(
            "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
            " from Order o" +
            " join o.member m" +
            " join o.delivery d" +
            " join o.orderItems oi" +
            " join oi.item i", OrderFlatDto.class)
            .getResultList();

	}


    
    
}
