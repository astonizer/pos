package com.increff.pos.pojo;

import com.increff.pos.model.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserPojo extends AbstractPojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    /**
     "supervisor": Indicates an admin role
     "operator": Indicates an operator role
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

}
