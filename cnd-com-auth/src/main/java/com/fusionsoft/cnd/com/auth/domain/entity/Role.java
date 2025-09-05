package com.fusionsoft.cnd.com.auth.domain.entity;

import com.fusionsoft.cnd.com.auth.domain.type.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "ROLES")
@Getter @Setter
@NoArgsConstructor
public class Role implements Serializable {

    @Id
    @Size(max = 50)
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE_NAME", nullable = false, length = 50)
    private RoleType roleName;

    public Role(RoleType roleName) {
        this.roleName = roleName;
    }

}