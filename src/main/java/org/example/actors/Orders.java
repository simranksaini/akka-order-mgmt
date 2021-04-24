package org.example.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.example.commands.CreateOrder;
import org.example.commands.ReserveProduct;
import org.example.commands.ShipProduct;
import org.example.commands.SubmitPayment;
import org.example.events.*;

public class Orders extends AbstractActor {

    final ActorRef client;
    final ActorRef inventory;
    final ActorRef payment;

    public Orders(ActorRef client, ActorRef inventory, ActorRef payment) {
        this.client = client;
        this.inventory = inventory;
        this.payment = payment;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateOrder.class, cmd -> {
                    System.out.println("COMMAND:\t\t" + cmd + " => " + getSelf().path().name());
                    inventory.tell(new ReserveProduct(cmd.userId, cmd.productId), getSelf());
                })
                .match(ProductReserved.class, evt -> {
                    System.out.println("EVENT:\t\t\t" + evt + " => " + getSelf().path().name());
                    payment.tell(new SubmitPayment(evt.userId, evt.txId), getSelf());
                })
                .match(PaymentAuthorized.class, evt -> {
                    System.out.println("EVENT:\t\t\t" + evt + " => " + getSelf().path().name());
                    inventory.tell(new ShipProduct(evt.userId, evt.txId), getSelf());
                })
                .match(ProductShipped.class, evt -> {
                    System.out.println("EVENT:\t\t\t" + evt + " => " + getSelf().path().name());
                    client.tell(new OrderCompleted(evt.userId, evt.txId), getSelf());
                })
                .build();
    }

    @Override
    public void preStart() {
        // Subscribe to Events from the Event Stream
        getContext().system().eventStream().subscribe(getSelf(), ProductReserved.class);
        getContext().system().eventStream().subscribe(getSelf(), ProductOutOfStock.class);
        getContext().system().eventStream().subscribe(getSelf(), ProductShipped.class);
        getContext().system().eventStream().subscribe(getSelf(), PaymentAuthorized.class);
        getContext().system().eventStream().subscribe(getSelf(), PaymentDeclined.class);
    }
}
