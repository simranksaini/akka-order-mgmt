package org.example.commands;

public class SubmitPayment implements Command{
    public final int userId;
    public final int productId;

    public SubmitPayment(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
