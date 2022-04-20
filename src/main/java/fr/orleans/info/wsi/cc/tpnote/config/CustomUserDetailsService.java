package fr.orleans.info.wsi.cc.tpnote.config;

import fr.orleans.info.wsi.cc.tpnote.modele.FacadeQuizz;
import fr.orleans.info.wsi.cc.tpnote.modele.Utilisateur;
import fr.orleans.info.wsi.cc.tpnote.modele.exceptions.UtilisateurInexistantException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private FacadeQuizz facadeQuizz;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Utilisateur utilisateur= null;
        try {
            utilisateur = facadeQuizz.getUtilisateurByEmail(username);
            if(utilisateur==null){
                throw new UsernameNotFoundException("User "+username+" not found");
            }
            UserDetails userDetails= User.builder()
                    .username(utilisateur.getEmailUtilisateur())
                    .password(passwordEncoder.encode(utilisateur.getMotDePasseUtilisateur()))
                    .roles(utilisateur.getRoles())
                    .build();

            return userDetails;

        } catch (UtilisateurInexistantException e) {
            throw new RuntimeException(e);
        }


    }}
