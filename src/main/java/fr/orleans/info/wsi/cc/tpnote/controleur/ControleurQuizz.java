package fr.orleans.info.wsi.cc.tpnote.controleur;

import fr.orleans.info.wsi.cc.tpnote.modele.FacadeQuizz;
import fr.orleans.info.wsi.cc.tpnote.modele.ResultatVote;
import fr.orleans.info.wsi.cc.tpnote.modele.Utilisateur;
import fr.orleans.info.wsi.cc.tpnote.modele.exceptions.*;
import fr.orleans.info.wsi.cc.tpnote.modele.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.parser.Entity;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/quizz", produces = MediaType.APPLICATION_JSON_VALUE)

public class ControleurQuizz {

    @Autowired
    private FacadeQuizz facadeQuizz;

    @PostMapping("/utilisateur")
    public ResponseEntity<Utilisateur> enregistreUtilisateur(@RequestParam String pseudo,@RequestParam String password){
        try{
            Utilisateur utilisateur=new Utilisateur(pseudo,password);
           int id = facadeQuizz.creerUtilisateur(utilisateur.getEmailUtilisateur(), utilisateur.getMotDePasseUtilisateur());
            URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
            return ResponseEntity.created(location).body(utilisateur);
        } catch (MotDePasseObligatoireException e) {
            return ResponseEntity.status(406).build();
        } catch (EmailNonValideException e) {
            return ResponseEntity.status(406).build();
        } catch (EmailDejaUtiliseException e) {
            return ResponseEntity.status(409).build();
        }catch (Exception e){
            return ResponseEntity.status(406).build();
        }
    }
    @GetMapping("/utilisateur/{idUtilisateur}")
    public ResponseEntity<Utilisateur> recupereUtilisateur(Principal principal,@PathVariable int idUtilisateur) throws UtilisateurInexistantException {

            String email=principal.getName();
            Utilisateur utilisateur=facadeQuizz.getUtilisateurByEmail(email);
            if(utilisateur.getIdUtilisateur()!=idUtilisateur) {
                return ResponseEntity.status(403).build();
            }
        return ResponseEntity.ok().body(utilisateur);
    }


    @PostMapping("/question")
    public ResponseEntity<Question> enregistreQuestion(Principal principal,@RequestBody Question question){
        try{
            String email=principal.getName();
            Utilisateur utilisateur=facadeQuizz.getUtilisateurByEmail(email);
            Question questionRec=new Question(utilisateur.getIdUtilisateur(), question.getLibelleQuestion(), question.getReponsesPossibles());
            facadeQuizz.creerQuestion(utilisateur.getIdUtilisateur(), questionRec.getLibelleQuestion(), questionRec.getReponsesPossibles());
            URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(questionRec.getIdQuestion()).toUri();
            return ResponseEntity.created(location).body(questionRec);
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(403).build();
        } catch (LibelleQuestionNonRenseigneException e) {
            return ResponseEntity.status(406).build();
        } catch (AuMoinsDeuxReponsesException e) {
            return ResponseEntity.status(406).build();
        }
    }

    @GetMapping("/question/{idQuestion}")
    public ResponseEntity<Question> recupereQuestion(Principal principal,@PathVariable("idQuestion") String idQuestion){
        try {
            String email= principal.getName();
            Question question=facadeQuizz.getQuestionById(idQuestion);
            return ResponseEntity.ok().body(question);
        } catch (QuestionInexistanteException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/question/{idQuestion}/vote")
    public ResponseEntity enregistreVote(Authentication authentication,
                                         @PathVariable String idQuestion,
                                         @RequestParam int idReponse){
        List<String> roles =authentication.getAuthorities().stream()
                .map(r->r.getAuthority()).collect(Collectors.toList());
        if (!roles.contains("ROLE_ETUDIANT")) return ResponseEntity.status(403).build();
        try{
            Utilisateur utilisateur=facadeQuizz.getUtilisateurByEmail(authentication.getName());
            facadeQuizz.voterReponse(utilisateur.getIdUtilisateur(), idQuestion,idReponse);
            return ResponseEntity.accepted().build();
        } catch (QuestionInexistanteException e) {
            return ResponseEntity.status(404).build();
        } catch (UtilisateurInexistantException e) {
            return ResponseEntity.status(403).build();
        } catch (NumeroPropositionInexistantException e) {
            return ResponseEntity.status(406).build();
        } catch (ADejaVoteException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @GetMapping("/question/{idQuestion}/vote")
    public ResponseEntity<ResultatVote[]> recupereVote(Principal principal, @PathVariable("idQuestion") String idQuestion){
        try{
            ResultatVote[] resultatVotes=facadeQuizz.getResultats(idQuestion);
            return ResponseEntity.ok().body(resultatVotes);
        } catch (QuestionInexistanteException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
