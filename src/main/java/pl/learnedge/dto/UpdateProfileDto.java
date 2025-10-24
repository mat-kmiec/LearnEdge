package pl.learnedge.dto;

import lombok.Data;

@Data
public class UpdateProfileDto {
    private String firstName;
    private String lastName;
    private String email;
}