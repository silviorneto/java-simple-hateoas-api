package com.silvio.spring3demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductDto(@NotBlank String name, @NotNull @PositiveOrZero BigDecimal value) {

}
