package com.fusionsoft.cnd.com.auth.domain.entity;

import com.fusionsoft.cnd.com.auth.domain.dto.RegisterRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Getter @Setter @ToString(exclude = {"password", "phone", "regDate", "updDate"})
@Builder
@AllArgsConstructor @NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 8644532104784328711L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UID")
    @Comment("사용자 고유번호")
    private Long uId;

    @NotNull //validation not null 의미
    @Column(name = "USER_ID", nullable = false, length = 50) //테이블 컬럼 not null
    @Comment("사용자 ID")
    private String userId;

    @NotNull
    @Column(name = "USER_NAME", nullable = false)
    @Comment("사용자 이름")
    private String userName;

    @Size(max = 255)
    @NotNull
    @Comment("사용자 비밀번호")
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Size(max = 50) //validation 용 사이즈 제한
    @NotNull
    @Comment("사용자 이메일")
    @Column(name = "EMAIL", nullable = false, length = 50) //DB 컬럼 들어갈 사이즈 제한
    private String email;

    @Size(max = 20)
    @NotNull
    @Comment("사용자 전화번호")
    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    @Builder.Default
    @ColumnDefault("1")
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = true;

    @CreatedDate
    @Comment("사용자 가입일")
    @Column(name = "REG_DATE", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @ColumnDefault("current_timestamp()")
    @Column(name = "UPD_DATE", nullable = false)
    @Comment("마지막 변경일")
    private LocalDateTime updDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "UID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_NAME")
    )
    @Comment("사용자 권한")
    private Set<Role> roles = new HashSet<>();


    @PrePersist
    protected void onCreate() {
        regDate = LocalDateTime.now();
        updDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updDate = LocalDateTime.now();
    }

    public static User from(RegisterRequest req, PasswordEncoder passwordEncoder) {
        return User.builder()
                .userId(req.userId())
                .password(passwordEncoder.encode(req.password()))
                .userName(req.userName())
                .email(req.email())
                .phone(req.phone())
                .build();
    }

}
