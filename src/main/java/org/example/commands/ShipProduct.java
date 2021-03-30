package org.example.commands;

public class ShipProduct implements ICommand{
    public final int userId;
    public final int txId;

    public ShipProduct(int userId, int txId) {
        this.userId = userId;
        this.txId = txId;
    }
}
