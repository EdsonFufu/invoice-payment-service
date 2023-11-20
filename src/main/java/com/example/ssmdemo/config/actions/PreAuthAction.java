package com.example.ssmdemo.config.actions;

import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import static com.example.ssmdemo.services.PaymentServiceImpl.PAYMENT_ID_HEADER;

@Component
public class PreAuthAction implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
            System.out.println("PreAuth was Called");
            System.out.println("Approved");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED).setHeader(PAYMENT_ID_HEADER, context.getMessageHeader(PAYMENT_ID_HEADER)).build());

    }
}
