package org.example.commands;

public class ReserveProduct implements ICommand{
    public final int userId;
    public final int productId;

    public ReserveProduct(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
