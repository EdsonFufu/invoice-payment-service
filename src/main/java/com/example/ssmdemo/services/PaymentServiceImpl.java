package com.example.ssmdemo.services;

import com.example.ssmdemo.components.PaymentStateChangeInterceptor;
import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import com.example.ssmdemo.model.Payment;
import com.example.ssmdemo.repository.PaymentRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;

    private PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    public static final String PAYMENT_ID_HEADER = "paymentId";
    private StateMachineFactory<PaymentState,PaymentEvent> stateMachineFactory;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentStateChangeInterceptor paymentStateChangeInterceptor, StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory) {
        this.paymentRepository = paymentRepository;
        this.paymentStateChangeInterceptor = paymentStateChangeInterceptor;
        this.stateMachineFactory = stateMachineFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTH_APPROVED);
        return sm;
    }
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public StateMachine<PaymentState, PaymentEvent> declinePreAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTH_DECLINED);
        return sm;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_APPROVED);
        return sm;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_DECLINED);
        return sm;
    }

    public void sendEvent(Long paymentId,StateMachine<PaymentState,PaymentEvent> sm,PaymentEvent event){
        Message message = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER,paymentId)
                .build();
        sm.sendEvent(message);
    }

    StateMachine<PaymentState,PaymentEvent> build(Long paymentId){
        Payment payment = paymentRepository.getReferenceById(paymentId);
        StateMachine<PaymentState,PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null,null,null));
        });
        sm.start();
        return sm;
    }
}
