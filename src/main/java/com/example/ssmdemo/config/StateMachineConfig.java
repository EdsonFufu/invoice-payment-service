package com.example.ssmdemo.config;

import com.example.ssmdemo.config.actions.PreAuthAction;
import com.example.ssmdemo.config.guards.PaymentIdGuard;
import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Optional;

import static com.example.ssmdemo.services.PaymentServiceImpl.PAYMENT_ID_HEADER;


@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    private final PaymentIdGuard paymentIdGuard;
    private final PreAuthAction preAuthAction;

    public StateMachineConfig(PaymentIdGuard paymentIdGuard, PreAuthAction preAuthAction) {
        this.paymentIdGuard = paymentIdGuard;
        this.preAuthAction = preAuthAction;
    }

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState,PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState,PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTHORIZE).action(preAuthAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED).action(authorizePaymentAction()).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED).action(declineAuthAction()).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED).action(preAuthDeclineAction()).guard(paymentIdGuard);

    }


//    public Guard<PaymentState,PaymentEvent> paymentIdGuard(){
//        return context -> context.getMessageHeader(PAYMENT_ID_HEADER) != null;
//    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {

        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                super.stateChanged(from, to);
                Optional.ofNullable(from).ifPresent(fromState -> {
                    System.out.println("Change state from: %s to %s".formatted(fromState.getStates(),to.getStates()));
                });

            }
        };

        config.withConfiguration().listener(adapter);
    }

//    public Action<PaymentState,PaymentEvent> preAuthAction(){
//        return context -> {
//            System.out.println("PreAuth was Called");
//            System.out.println("Approved");
//            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED).setHeader(PAYMENT_ID_HEADER,context.getMessageHeader(PAYMENT_ID_HEADER)).build());
//        };
//    }

    public Action<PaymentState,PaymentEvent> authorizePaymentAction(){
        return context -> {
            System.out.println("authorizePayment was Called");
            System.out.println("Approved");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED).setHeader(PAYMENT_ID_HEADER,context.getMessageHeader(PAYMENT_ID_HEADER)).build());
        };
    }

    public Action<PaymentState,PaymentEvent> declineAuthAction(){
        return context -> {
            System.out.println("declineAuth was Called");
            System.out.println("declineAuth");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED).setHeader(PAYMENT_ID_HEADER,context.getMessageHeader(PAYMENT_ID_HEADER)).build());
        };
    }

    private Action<PaymentState, PaymentEvent> preAuthDeclineAction() {
        return context -> {
            System.out.println("declinePreAuth was Called");
            System.out.println("declinePreAuth");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED).setHeader(PAYMENT_ID_HEADER,context.getMessageHeader(PAYMENT_ID_HEADER)).build());
        };
    }
}
