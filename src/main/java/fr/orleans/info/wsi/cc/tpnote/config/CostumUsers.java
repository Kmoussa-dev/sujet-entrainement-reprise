package fr.orleans.info.wsi.cc.tpnote.config;

import fr.orleans.info.wsi.cc.tpnote.modele.FacadeQuizz;
import fr.orleans.info.wsi.cc.tpnote.modele.Utilisateur;
import fr.orleans.info.wsi.cc.tpnote.modele.exceptions.UtilisateurInexistantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CostumUsers implements UserDetailsService {
    @Autowired
    FacadeQuizz facadeQuizz;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Utilisateur u = facadeQuizz.getUtilisateurByEmail(username);
            UserDetails userDetails = User.builder()
                    .username(u.getEmailUtilisateur())
                    .password(passwordEncoder.encode(u.getMotDePasseUtilisateur()))
                    .roles(u.getRoles())
                    .build();
            return userDetails;
        } catch (UtilisateurInexistantException e) {
            throw new UsernameNotFoundException("username  " +username );
        }
    }

}
