package net.malevy.hyperdemo;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.UUID;

public class AuthMother {

    public static User user() {
        return new User("jack", "password", AuthorityUtils.NO_AUTHORITIES);
    }

    public static User adminUser() {
        return new User("jack",
                "password",
                AuthorityUtils.createAuthorityList("admin"));
    }

    public static Authentication authentication() {
        return authentication(user());
    }

    public static Authentication authentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    public static Authentication anonymous() {
        User user = new User("anonymous", "password", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        return new AnonymousAuthenticationToken(UUID.randomUUID().toString(), user, user.getAuthorities());
    }

}
