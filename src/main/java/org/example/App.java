package org.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import org.example.actors.Inventory;
import org.example.actors.Orders;
import org.example.actors.Payment;
import org.example.commands.CreateOrder;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {

        System.out.println("Hello World!");
        // Create the Order Management actor system
        final ActorSystem system = ActorSystem.create("OrderManagement");

        // Plumbing for "client"
        final Inbox clientInbox = Inbox.create(system);
        final ActorRef client = clientInbox.getRef();


        // Create the services
        final ActorRef inventory = system.actorOf(Props.create(Inventory.class), "Inventory");
        final ActorRef payment = system.actorOf(Props.create(Payment.class), "Payment");
        final ActorRef orders = system.actorOf(Props.create(Orders.class, client, inventory, payment), "Orders");

        // Send a CreateOrder Command to the Orders service
        clientInbox.send(orders, new CreateOrder(9, 1337));

        try {
            // Wait for the order confirmation
            Object confirmation = clientInbox.receive(Duration.create(5, TimeUnit.SECONDS));
            System.out.println("EVENT:\t\t\t" + confirmation + " => Client");
        } catch (TimeoutException e) {
            System.out.println("Waited 5 seconds for the OrderCompleted event, giving up...");
        }

        System.out.println("Order completed. Shutting down system.");

        system.terminate();
    }
}
