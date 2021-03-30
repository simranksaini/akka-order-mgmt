package org.example.commands;

import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CreateOrder implements ICommand{

    public final int userId;
    public final int productId;

    public CreateOrder(int userId, int productId) {
        System.out.println("CREATE ORDER!!");
        this.userId = userId;
        this.productId = productId;
    }
}
