package org.example.actors;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.AbstractPersistentActor;
import org.example.commands.ReserveProduct;
import org.example.commands.ShipProduct;
import org.example.events.IEvent;
import org.example.events.ProductReserved;
import org.example.events.ProductShipped;

public class Inventory extends AbstractPersistentActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public String persistenceId() {
        return "inventory";
    }

    int nrOfProductsShipped = 0; // Mutable state, persisted in memory (AKA Memory Image)

    IEvent reserveProduct(int userId, int productId) {
        log.info("SIDE-EFFECT:\tReserving Product => " + self().path().name());
        return new ProductReserved(userId, productId);
    }

    IEvent shipProduct(int userId, int txId) {
        nrOfProductsShipped += 1; // Update internal state
        log.info("SIDE-EFFECT:\tShipping Product => " + self().path().name() +
                " - ProductsShipped: " + nrOfProductsShipped);
        return new ProductShipped(userId, txId);
    }


    @Override
    public Receive createReceive() {
        System.out.println("in create receive of inventory");
        return receiveBuilder()
                .match(ReserveProduct.class, cmd -> {                                // Receive ReserveProduct Command
                    System.out.println("COMMAND:\t\t" + cmd + " => " + getSelf().path().name());
                    IEvent productStatus = reserveProduct(cmd.userId, cmd.productId); // Try to reserve the product
                    persist(productStatus, evt -> {                                  // Try to persist the Event
                        getContext().system().eventStream().publish(evt);            // Publish Event to Event Stream
                    });
                })
                .match(ShipProduct.class, cmd -> {                                   // Receive ShipProduct Command
                    System.out.println("COMMAND:\t\t" + cmd + " => " + getSelf().path().name());
                    IEvent shippingStatus = shipProduct(cmd.userId, cmd.txId);        // Try to ship the product
                    persist(shippingStatus, evt -> {                                 // Try to persist the Event
                        getContext().system().eventStream().publish(evt);            // Publish Event to Event Stream
                    });
                })
                .build();
    }

    @Override
    public Receive createReceiveRecover() {
        // Replay ProductReserved // Replay ProductShipped
        return receiveBuilder()
                .match(ProductReserved.class, evt -> {
                    System.out.println("EVENT (REPLAY):\t" + evt + " => " + getSelf().path().name());
                }).match(ProductShipped.class, evt -> {
                    nrOfProductsShipped += 1;
                    System.out.println("EVENT (REPLAY):\t" + evt + " => " + getSelf().path().name() +
                            " - ProductsShipped: " + nrOfProductsShipped);
                })
                .build();
    }
}
