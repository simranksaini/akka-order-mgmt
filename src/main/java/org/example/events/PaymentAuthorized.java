package org.example.events;

public class PaymentAuthorized implements Event{
    public final int userId;
    public final int txId;

    public PaymentAuthorized(int userId, int txId) {
        this.userId = userId;
        this.txId = txId;
    }
}
