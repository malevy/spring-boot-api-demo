package net.malevy.hyperdemo;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

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

}
