package za.ac.cput.domain;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {
    @Id
    protected String userId;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String phoneNumber;
    protected RoleEnum role;


}
