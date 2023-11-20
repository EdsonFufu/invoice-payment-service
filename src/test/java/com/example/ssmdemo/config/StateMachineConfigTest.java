package com.example.ssmdemo.config;

import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class StateMachineConfigTest {
    @Autowired StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine(){
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        System.out.println(sm.getState().toString());
        sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        System.out.println(sm.getState().getId().name());
        sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        System.out.println(sm.getState().getId().name());
        sm.sendEvent(PaymentEvent.PRE_AUTH_DECLINED);
        System.out.println(sm.getState().getId().name());
    }
}