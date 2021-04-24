package org.example.events;

public class ProductShipped implements Event{
    public final int userId;
    public final int txId;

    public ProductShipped(int userId, int txId) {
        this.userId = userId;
        this.txId = txId;
    }
}
