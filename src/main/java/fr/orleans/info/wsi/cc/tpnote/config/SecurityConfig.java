package fr.orleans.info.wsi.cc.tpnote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new CostumUsers();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/quizz/utilisateur").permitAll()
                .antMatchers(HttpMethod.GET,"/api/quizz/utilisateur/*").authenticated()
                .antMatchers(HttpMethod.POST,"/api/quizz/question").hasAnyRole("PROFESSEUR")
                .antMatchers(HttpMethod.GET,"/api/quizz/question/*").authenticated()
                .antMatchers(HttpMethod.PUT,"/api/quizz/question/*/vote ").hasAnyRole("ETUDIANT")
                .antMatchers(HttpMethod.GET,"/api/quizz/question/*/vote ").hasAnyRole("PROFESSEUR")
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
