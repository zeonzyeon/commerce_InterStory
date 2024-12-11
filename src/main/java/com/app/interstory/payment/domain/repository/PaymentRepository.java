package com.app.interstory.payment.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.interstory.payment.domain.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
