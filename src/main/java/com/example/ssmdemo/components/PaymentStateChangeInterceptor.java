package com.example.ssmdemo.components;

import com.example.ssmdemo.domain.PaymentEvent;
import com.example.ssmdemo.domain.PaymentState;
import com.example.ssmdemo.model.Payment;
import com.example.ssmdemo.repository.PaymentRepository;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static com.example.ssmdemo.services.PaymentServiceImpl.PAYMENT_ID_HEADER;


@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
    private final PaymentRepository paymentRepository;

    public PaymentStateChangeInterceptor(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine, StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
//        super.preStateChange(state, message, transition, stateMachine, rootStateMachine);
        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable(Objects.toString(msg.getHeaders().get(PAYMENT_ID_HEADER),"-1L"))).ifPresent(paymentId -> {
            Payment payment = paymentRepository.getReferenceById(Long.valueOf(paymentId));
            System.out.println("PreStateChange:[" +payment+"] => new State[" + state.getId() +"]");
            payment.setState(state.getId());
            System.out.println("Payment After Change State:["+payment+"] => new State[" + state.getId() +"]");
            Payment paymentSaved = paymentRepository.save(payment);
            System.out.println("Payment After Save:["+paymentSaved+"]");
        });
    }
}
