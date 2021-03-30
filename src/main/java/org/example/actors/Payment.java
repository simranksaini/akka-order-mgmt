package org.example.actors;

import akka.persistence.AbstractPersistentActor;
import org.example.commands.SubmitPayment;
import org.example.events.IEvent;
import org.example.events.PaymentAuthorized;

public class Payment extends AbstractPersistentActor {

    @Override
    public String persistenceId() {
        return "payment";
    }

    int uniqueTransactionNr = 0; // Mutable state, persisted in memory (AKA Memory Image)

    IEvent processPayment(int userId, int txId) {
        uniqueTransactionNr += 1;  // Update the internal state
        System.out.println("SIDE-EFFECT:\tProcessing payment => " + getSelf().path().name() +
                " - TxNumber: " + uniqueTransactionNr);
        return new PaymentAuthorized(userId, uniqueTransactionNr);
    }

    @Override
    public Receive createReceive() {
        System.out.println("in create receive of payment");

        return receiveBuilder()
                .match(SubmitPayment.class, cmd -> {                                 // Receive SubmitPayment Command
                    System.out.println("COMMAND:\t\t" + cmd + " => " + getSelf().path().name());
                    IEvent paymentStatus = processPayment(cmd.userId, cmd.productId); // Try to pay product
                    persist(paymentStatus, evt -> {                                  // Try to persist the Event
                        getContext().system().eventStream().publish(evt);            // Publish Event to Event Stream
                    });

                })
                .build();
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(PaymentAuthorized.class, evt -> { // Replay PaymentAuthorized
                    uniqueTransactionNr += 1;            // Update the internal state
                    System.out.println("EVENT (REPLAY):\t" + evt + " => " + getSelf().path().name() +
                            " - TxNumber: " + uniqueTransactionNr);
                })
                .build();
    }
}
