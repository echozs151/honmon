package com.example.honmon.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.honmon.Models.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
 
public class MyUserDetails implements UserDetails {
 
    private User user;
    private String userName;
    private List<GrantedAuthority> authorities;
     
    public MyUserDetails(String userName) {
        this.userName = userName;
    }

    public MyUserDetails(User user) {
        this.user = user;
        this.authorities = Arrays.stream(user.getRole().split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    }
 
    public MyUserDetails() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // return Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority("User"));
        return authorities;
    }
 
    @Override
    public String getPassword() {
        return user.getPassword();
    }
 
    @Override
    public String getUsername() {
        return user.getUsername(); //user.getUsername();
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    public User getUser() {
        return user;
    }

    // public String getUserName()
    // {
    //     return userName;
    // }

    // public void setUserName(String userName)
    // {
    //     this.userName = userName;
    // }
 
}