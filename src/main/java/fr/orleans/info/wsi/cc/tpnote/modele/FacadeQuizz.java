package fr.orleans.info.wsi.cc.tpnote.modele;

import fr.orleans.info.wsi.cc.tpnote.modele.exceptions.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FacadeQuizz {

    private Map<String,Utilisateur> mapUtilisateurs;
    private Map<String,Question> mapQuestions;


    public FacadeQuizz(){
        mapUtilisateurs=new HashMap<>();
        mapQuestions=new HashMap<>();
    }




    /**
     *
     * @param email : email valide
     * @param password : mot de passe utilisateur non vide et chiffré (lors de son intégration au web-service)
     * @return identifiant entier
     * @throws EmailDejaUtiliseException : email déjà utilisé
     * @throws EmailNonValideException : email n'est pas de la bonne forme
     * @throws MotDePasseObligatoireException : le mot de passe est Blank ou nul
     */

    public int creerUtilisateur(String email,String password) throws EmailDejaUtiliseException, EmailNonValideException, MotDePasseObligatoireException {
        if (mapUtilisateurs.containsKey(email)) {
            throw new EmailDejaUtiliseException();
        }
        if (password.isBlank()){
            throw new MotDePasseObligatoireException();
        }
        if(password.isEmpty()){
            throw new MotDePasseObligatoireException();
        }
        if(!OutilsPourValidationEmail.patternMatches(email)){
            throw new EmailNonValideException();
        }
        mapUtilisateurs.put(email,new Utilisateur(email,password));

        return mapUtilisateurs.get(email).getIdUtilisateur();
    }

    /**
     * Permet de récupérer l'identifiant int d'un utilisateur par son E-mail
     * @param email
     * @return identifiant int
     */

    public int getIdUserByEmail(String email) throws EmailInexistantException {
       if (!mapUtilisateurs.containsKey(email)){
           throw new EmailInexistantException();
       }
        int idUser=mapUtilisateurs.get(email).getIdUtilisateur();
       return idUser;
    }

    /**
     * Permet à un professeur de créer une question
     * @param idUser id du professeur (on suppose qu'uniquement les professeurs pourront accéder à cette fonctionnalité donc
     *               pas besoin de vérifier s'il s'agit d'un professeur ou s'il s'agit d'un utilisateur existant)
     * @param libelleQuestion : libellé de la question
     * @param libellesReponses : libellés des réponses possibles
     * @return identifiant aléatoire chaîne de caractère (UUID)
     * @throws AuMoinsDeuxReponsesException : au moins deux réponses sont attendues
     * @throws LibelleQuestionNonRenseigneException : le libellé doit être obligatoirement non vide (non blank)
     */

    public String creerQuestion(int idUser, String libelleQuestion, String... libellesReponses) throws AuMoinsDeuxReponsesException, LibelleQuestionNonRenseigneException {
        if(libellesReponses.length<=2) throw new AuMoinsDeuxReponsesException();
        if(libelleQuestion.isBlank()) throw new LibelleQuestionNonRenseigneException();
        Question question=new Question(idUser,libelleQuestion,libellesReponses);
        mapQuestions.put(question.getIdQuestion(),question);
        return question.getIdQuestion();
    }


    /**
     * Permet de récupérer une question par son identifiant
     * @param idQuestion : id de la question concernée
     * @return l'objet Question concerné
     * @throws QuestionInexistanteException : l'identifiant donné ne correspond à aucune question
     */

    public Question getQuestionById(String idQuestion) throws QuestionInexistanteException {
        Question question=mapQuestions.get(idQuestion);
        if (!mapQuestions.containsKey(idQuestion)) throw new QuestionInexistanteException();
        return question;
    }

    /**
     * Permet à un étudiant de voter pour une réponse
     * @param idUser : identifiant entier de l'étudiant en question (là encore on suppose que l'idUser est correct et que c'est bien un étudiant. Cette
     *               vérification est déléguée au contrôleur REST)
     * @param idQuestion : identifiant de la question concernée
     * @param numeroProposition : numéro de la proposition (les réponses possibles sont stockées dans un tableau donc le
     *                          numéro correspond à l'indice dans le tableau)
     * @throws ADejaVoteException : l'étudiant concerné a déjà voté à cette question (éventuellement pour une autre réponse)
     * @throws NumeroPropositionInexistantException : le numéro de la proposition n'est pas un indice correct du tableau des propositions
     * de la question
     * @throws QuestionInexistanteException : la question identifiée n'existe pas
     */

    public void voterReponse(int idUser,String idQuestion, int numeroProposition) throws ADejaVoteException,
            NumeroPropositionInexistantException, QuestionInexistanteException {
        Question question=mapQuestions.get(idQuestion);
        if (question==null) throw new QuestionInexistanteException();
        question.voterPourUneReponse(idUser,numeroProposition);
    }


    /**
     * Vous devez dans la fonction ci-dessous vider toutes vos structures de données.
     * Pensez à remettre à 0 vos éventuels compteurs statiques (probablement dans la classe utilisateur)
     */

    public void reinitFacade(){
    //TODO
        mapUtilisateurs=new HashMap<>();
        mapQuestions=new HashMap<>();
        Utilisateur.resetCompteur();
    }


    /**
     * Permet de récupérer un utilisateur par son email
     * @param username
     * @return
     */
    public Utilisateur getUtilisateurByEmail(String username) throws UtilisateurInexistantException {
        Utilisateur utilisateur=mapUtilisateurs.get(username);
        if (utilisateur==null) throw new UtilisateurInexistantException();
        return utilisateur;
    }


    /**
     * Permet de récupérer le résultat d'un vote à une question
     * @param idQuestion
     * @return
     * @throws QuestionInexistanteException
     */

    public ResultatVote[] getResultats(String idQuestion) throws QuestionInexistanteException {
       Question question=mapQuestions.get(idQuestion);
       if (question==null) throw new QuestionInexistanteException();
        return question.getResultats();
    }
}
