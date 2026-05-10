package com.jackalcode.ecommerce_store.order;

import java.util.Map;

public record WebhookRequest(
        Map<String, String> headers,
        String payload
) {
}
