package jpabook.jpashop.repository.order.simplequery;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderSimpleRepository {

    private final EntityManager em;
    
    public List<OrderSimpleQueryDto> findOrderDtos() {

        return em.createQuery(
                "select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                        + " from Order o" + " join o.member m" + " join o.delivery d",
                OrderSimpleQueryDto.class).getResultList();

    }
}