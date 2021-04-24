package org.example.commands;

public class CreateOrder implements Command{
    public final int userId;
    public final int productId;

    public CreateOrder(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
