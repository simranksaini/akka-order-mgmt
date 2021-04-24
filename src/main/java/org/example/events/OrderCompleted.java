package org.example.events;

public class OrderCompleted implements Event{
    final int userId;
    final int txId;

    public OrderCompleted(int userId, int txId) {
        this.userId = userId;
        this.txId = txId;
    }
}
