package org.example.events;

public class ProductReserved implements Event{
    public final int userId;
    public final int txId;

    public ProductReserved(int userId, int txId) {
        this.userId = userId;
        this.txId = txId;
    }
}
