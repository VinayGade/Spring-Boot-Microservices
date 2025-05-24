package com.ecommerce.notification.order;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderPlacedEvent {

    private String orderNumber;
    private String email;
}
