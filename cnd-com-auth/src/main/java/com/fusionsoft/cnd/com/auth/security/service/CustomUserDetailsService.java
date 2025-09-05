package com.fusionsoft.cnd.com.auth.security.service;

import com.fusionsoft.cnd.com.auth.domain.entity.User;
import com.fusionsoft.cnd.com.auth.repository.UserRepository;
import com.fusionsoft.cnd.com.auth.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    //spring security에서 말하는 username은 한국에서 말하는 userId이다
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserId(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 없습니다"));
    }
}

