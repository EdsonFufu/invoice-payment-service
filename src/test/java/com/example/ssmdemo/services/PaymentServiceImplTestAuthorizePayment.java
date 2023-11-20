package com.example.ssmdemo.services;

import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import com.example.ssmdemo.model.Payment;
import com.example.ssmdemo.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class PaymentServiceImplTestAuthorizePayment {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    @Rollback(value = false)
    @Test
    void authorizePayment() {
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.authorizePayment(20L);
        Payment preAuthPayment = paymentRepository.getReferenceById(20L);
        System.out.println(sm.getState().getId());
        System.out.println(preAuthPayment);
    }
}