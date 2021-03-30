package org.example.events;

public class ProductOutOfStock implements IEvent {
    final int userId;
    final int productId;

    ProductOutOfStock(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
