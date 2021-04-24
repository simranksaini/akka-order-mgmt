package org.example.actors;
import akka.actor.*;
import akka.persistence.AbstractPersistentActor;
import org.example.commands.ReserveProduct;
import org.example.commands.ShipProduct;
import org.example.events.Event;
import org.example.events.ProductReserved;
import org.example.events.ProductShipped;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Inventory extends AbstractPersistentActor{

    int nrOfProductsShipped = 0; // Mutable state, persisted in memory (AKA Memory Image)

    Event reserveProduct(int userId, int productId) {
        System.out.println("SIDE-EFFECT:\tReserving Product => " + getSelf().path().name());
        return new ProductReserved(userId, productId);
    }

    Event shipProduct(int userId, int txId) {
        nrOfProductsShipped += 1; // Update internal state
        System.out.println("SIDE-EFFECT:\tShipping Product => " + getSelf().path().name() +
                " - ProductsShipped: " + nrOfProductsShipped);
        return new ProductShipped(userId, txId);
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(ProductReserved.class, evt -> { // Replay ProductReserved
                    System.out.println("EVENT (REPLAY):\t" + evt + " => " + getSelf().path().name());
                })
                .match(ProductShipped.class, evt -> {  // Replay ProductShipped
                    nrOfProductsShipped += 1;          // Update the internal state
                    System.out.println("EVENT (REPLAY):\t" + evt + " => " + getSelf().path().name() +
                            " - ProductsShipped: " + nrOfProductsShipped);
                })
                .build();    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReserveProduct.class, cmd -> {                                // Receive ReserveProduct Command
                    System.out.println("COMMAND:\t\t" + cmd + " => " + getSelf().path().name());
                    Event productStatus = reserveProduct(cmd.userId, cmd.productId); // Try to reserve the product
                    persist(productStatus, evt -> {                                  // Try to persist the Event
                        getContext().system().eventStream().publish(evt);            // Publish Event to Event Stream
                    });

                })
                .match(ShipProduct.class, cmd -> {                                   // Receive ShipProduct Command
                    System.out.println("COMMAND:\t\t" + cmd + " => " + getSelf().path().name());
                    Event shippingStatus = shipProduct(cmd.userId, cmd.txId);        // Try to ship the product
                    persist(shippingStatus, evt -> {                                 // Try to persist the Event
                        getContext().system().eventStream().publish(evt);            // Publish Event to Event Stream
                    });
                })
                .build();    }

    @Override
    public String persistenceId() {
        return "inventory";
    }
}
