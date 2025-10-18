package pl.learnedge.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt

    @Column(nullable = false, length = 30)
    private String role;     // np. ROLE_USER, ROLE_ADMIN

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(unique = true)
    private String email;
}
