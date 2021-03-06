package net.malevy.hyperdemo.config;

import net.malevy.hyperdemo.security.OPADecisionVoter;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Value("${app.security.enabled:true}")
    private boolean enableSecurity;

    @Value("${app.security.opa.apiDecisionUrl}")
    public String OPADecisionUrl;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inMemoryUserDetailsManager());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // authorizeRequests(ar -> authorizeRequests.antMatchers("/something").hasAnyRole(role))

        if (enableSecurity) {
            http.authorizeRequests().anyRequest().authenticated().accessDecisionManager(customAccessDecisionManager());
        }
        else {
            http.authorizeRequests().anyRequest().permitAll();
        }

        http.httpBasic();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
    }

    @Bean
    public AccessDecisionManager customAccessDecisionManager() {
        OPADecisionVoter decisionVoter = new OPADecisionVoter(new OkHttpClient(), this.OPADecisionUrl);
        List<AccessDecisionVoter<?>> voters = Collections.singletonList(decisionVoter);
        return new UnanimousBased(voters);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails cmUser = User.builder()
                .username("joe")
                .password(passwordEncoder().encode("password"))
                .roles("campaign-manager")
                .build();

        return new InMemoryUserDetailsManager(cmUser);
    }
}
