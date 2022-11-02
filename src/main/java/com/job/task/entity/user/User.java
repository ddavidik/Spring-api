package com.job.task.entity.user;

import com.job.task.entity.endpoint.MonitoredEndpoint;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(name = "username", unique = true)
    private String username;

    @Email
    @NotBlank
    @Column(name = "email", unique = true)
    private String email;

    // instead of UUID type I use String as stated in task description
    @Column(name = "access_token", unique = true)
    private String accessToken;

    @OneToMany(mappedBy = "owner")
    private Set<MonitoredEndpoint> endpoints;

    public User() {
    }

    public User(@NotBlank String username, @Email @NotBlank String email, String accessToken) {
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && username.equals(user.username) && email.equals(user.email) && accessToken.equals(user.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, accessToken);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", accessToken=" + accessToken +
                '}';
    }
}
