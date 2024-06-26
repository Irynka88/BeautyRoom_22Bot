package com.project22.BeautyRoom_22Bot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class User {
    @Id
    private Long chatId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String userName;
    @NotNull
    private LocalDate registeredAt;

    @OneToMany(mappedBy = "user")
    private Set<UserProcedure> userProcedures;
}
