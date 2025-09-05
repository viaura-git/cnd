package com.fusionsoft.cnd.com.auth.security.model;

import com.fusionsoft.cnd.com.auth.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "REFRESH_TOKENS")
@Getter @Setter
@NoArgsConstructor
//@RequiredArgsConstructor //final, lombok @NonNull인 것들만 생성한다. @RequiredArgsConstructor가 lombok것이기 때문
//하지만, Builder패턴이 더 유연하기에 Builder를 쓴다
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RID")
    private Long rId;

    @NonNull
    @Column(nullable = false, unique = true)
    private String token;

    @NonNull
    @Column(nullable = false)
    private Instant expiryDate;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}

