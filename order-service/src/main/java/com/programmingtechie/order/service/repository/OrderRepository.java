package com.programmingtechie.order.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmingtechie.order.service.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
