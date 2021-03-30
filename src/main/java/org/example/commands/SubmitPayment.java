package org.example.commands;

public class SubmitPayment implements ICommand{
    final public int userId;
    final public int productId;

    public SubmitPayment(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
