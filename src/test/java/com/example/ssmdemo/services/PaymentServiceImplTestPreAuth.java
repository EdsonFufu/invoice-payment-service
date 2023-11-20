package com.example.ssmdemo.services;

import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import com.example.ssmdemo.model.Payment;
import com.example.ssmdemo.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTestPreAuth {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    @Rollback(value = false)
    @Test
    void preAuth() {
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(22L);
        Payment preAuthPayment = paymentRepository.getReferenceById(22L);
        System.out.println(sm.getState().getId());
        System.out.println(preAuthPayment);
    }
}