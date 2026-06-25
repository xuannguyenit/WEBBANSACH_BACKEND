package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.dto.response.orderresponse.DailyRevenueResponse;
import com.xuannguyen.identity.entity.Order;
import com.xuannguyen.identity.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
    List<Order> findByUserId(Long userId);

    Optional<Order> findByUserIdAndStatus(Long userId, String status);

    @Query("""
        select o
        from Order o
        where o.userId = :userId
          and upper(o.status) in ('COMPLETE', 'COMPLETED')
        order by o.orderTime desc
    """)
    List<Order> getAllByUserIdComplete(@Param("userId") Long userId);

    @Query("""
        select o
        from Order o
        where o.userId = :userId
        order by o.orderTime desc
    """)
    List<Order> getAllByUserId(@Param("userId") Long userId);

    @Query("""
        select o
        from Order o
        where upper(o.status) in ('COMPLETE', 'COMPLETED')
    """)
    List<Order> getAllOrderCompleted();

    @Query("""
        select o
        from Order o
        order by o.orderTime desc
    """)
    List<Order> getAllOrderDesc();

    /**
     * PostgreSQL native query.
     * Không map trực tiếp vào DailyRevenueResponse ở Repository.
     * Service sẽ map Object[] sang DTO.
     */
    @Query(value = """
        select
            cast(o.order_time as date) as revenue_date,
            coalesce(sum(o.total_price), 0) as revenue
        from orders o
        where upper(o.status) in ('COMPLETE', 'COMPLETED')
          and extract(month from o.order_time) = :month
          and extract(year from o.order_time) = :year
        group by cast(o.order_time as date)
        order by cast(o.order_time as date)
    """, nativeQuery = true)
    List<Object[]> getDailyRevenueForMonthRaw(
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
        select count(o)
        from Order o
    """)
    Long getCountOrder();

    @Query("""
        select od
        from OrderDetail od
        join od.order o
        where upper(o.status) = 'PENDING'
    """)
    List<OrderDetail> getPendingOrderDetails();

    @Query("""
        select od
        from OrderDetail od
        join od.order o
        where o.id = :id
    """)
    List<OrderDetail> getOrderDetailById(@Param("id") String id);
}
