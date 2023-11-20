package com.example.ssmdemo.services;

import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import com.example.ssmdemo.model.Payment;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
    public Payment newPayment(Payment payment);
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

    public StateMachine<PaymentState, PaymentEvent> declinePreAuth(Long paymentId);
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
