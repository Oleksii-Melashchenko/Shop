package com.clozex.shop.dto.order;

import com.clozex.shop.model.Status;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDto(
        @NotNull(message = "Status is required")
        Status status
) {

}
