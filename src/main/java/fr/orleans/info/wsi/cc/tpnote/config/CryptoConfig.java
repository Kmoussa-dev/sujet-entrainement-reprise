package fr.orleans.info.wsi.cc.tpnote.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class CryptoConfig extends WebSecurityConfigurerAdapter {

    /**
     * Configuration des utilisateurs et les roles
     * */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    /**
     * Configurer les endoints et les chaques roles
     * */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET," /api/quizz/utilisateur/{idUtilisateur}").authenticated()
                .antMatchers(HttpMethod.POST,"/api/quizz/question").hasRole("PROFESSEUR")
                .antMatchers(HttpMethod.GET,"/api/quizz/question/{idQuestion}").authenticated()
                .antMatchers(HttpMethod.PUT,"/api/quizz/question/{idQuestion}/vote").hasRole("ETUDIANT")
                .antMatchers(HttpMethod.GET,"/api/quizz/question/{idQuestion}/vote").hasRole("PROFESSEUR")
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
