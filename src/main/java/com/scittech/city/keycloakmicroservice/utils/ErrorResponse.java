package com.scittech.city.keycloakmicroservice.utils;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    OffsetDateTime timestamp;
    String status;
    String error;
    String path;
}
