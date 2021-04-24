package org.example.events;

public class PaymentDeclined implements Event{
    final int userId;
    final int txId;

    PaymentDeclined(int userId, int txId) {
        this.userId = userId;
        this.txId = txId;
    }
}
