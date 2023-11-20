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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTestPreAuthDeclined {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @Transactional
    @Rollback(value = false)
    @Test
    void declinePreAuth() {
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.declinePreAuth(19L);
        Payment declinePreAuth = paymentRepository.getReferenceById(19L);
        System.out.println(sm.getState().getId());
        System.out.println(declinePreAuth);
    }
}