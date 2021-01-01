package com.example.springbootsimpleexample.dto;

import com.sun.istack.NotNull;
import lombok.Data;

@Data
public class BookDTO {
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String author;
}
