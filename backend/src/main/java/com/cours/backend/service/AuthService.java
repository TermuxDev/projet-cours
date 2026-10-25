package com.cours.backend.service;

import com.cours.backend.exception.AccesRefuseException;
import com.cours.backend.exception.CodeInvalideException;
import com.cours.backend.exception.CompteNonVerifieException;
import com.cours.backend.exception.EmailDejaUtiliseException;
import com.cours.backend.exception.IdentifiantsInvalidesException;
import com.cours.backend.exception.RessourceNonTrouveeException;
import com.cours.backend.model.CodeVerification;
import com.cours.backend.model.RoleUtilisateur;
import com.cours.backend.model.TypeCode;
import com.cours.backend.model.Utilisateur;
import com.cours.backend.model.dto.*;
import com.cours.backend.repository.CodeVerificationRepository;
import com.cours.backend.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private static final SecureRandom ALEATOIRE = new SecureRandom();
    private static final int DUREE_VALIDITE_MINUTES = 15;

    private final UtilisateurRepository utilisateurRepository;
    private final CodeVerificationRepository codeVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(UtilisateurRepository utilisateurRepository, CodeVerificationRepository codeVerificationRepository,
                        PasswordEncoder passwordEncoder, EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.codeVerificationRepository = codeVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public UtilisateurDTO connexion(ConnexionRequest requete) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(requete.email())
                .orElseThrow(() -> new IdentifiantsInvalidesException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(requete.motDePasse(), utilisateur.getMotDePasse())) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }
        if (!utilisateur.isEmailVerifie()) {
            throw new CompteNonVerifieException("Ce compte n'est pas encore verifie. Verifie tes e-mails ou demande un nouveau code.");
        }

        return versDTO(utilisateur);
    }

    // Inscription ouverte uniquement aux etudiants et enseignants : un
    // compte administrateur ne se cree pas depuis un formulaire public.
    public void inscrire(InscriptionCompteRequest requete) {
        if (requete.role() == RoleUtilisateur.ADMINISTRATEUR) {
            throw new AccesRefuseException("Impossible de creer un compte administrateur par ce formulaire");
        }
        if (utilisateurRepository.existsByEmail(requete.email())) {
            throw new EmailDejaUtiliseException("Cet e-mail est deja utilise");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(requete.nom());
        utilisateur.setPrenom(requete.prenom());
        utilisateur.setEmail(requete.email());
        utilisateur.setMotDePasse(passwordEncoder.encode(requete.motDePasse()));
        utilisateur.setRole(requete.role());
        utilisateur.setEmailVerifie(false);
        utilisateurRepository.save(utilisateur);

        envoyerNouveauCode(utilisateur, TypeCode.INSCRIPTION);
    }

    public UtilisateurDTO verifierOtp(OtpRequest requete) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(requete.email())
                .orElseThrow(() -> new RessourceNonTrouveeException("Compte introuvable"));

        CodeVerification code = codeVerificationRepository
                .findTopByUtilisateurIdAndTypeAndUtiliseFalseOrderByDateCreationDesc(utilisateur.getId(), TypeCode.INSCRIPTION)
                .orElseThrow(() -> new CodeInvalideException("Aucun code en attente pour ce compte"));

        validerCode(code, requete.code());
        utilisateur.setEmailVerifie(true);

        return versDTO(utilisateur);
    }

    public void renvoyerOtp(EmailRequest requete) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(requete.email())
                .orElseThrow(() -> new RessourceNonTrouveeException("Compte introuvable"));
        if (utilisateur.isEmailVerifie()) {
            throw new CodeInvalideException("Ce compte est deja verifie, connecte-toi directement");
        }
        envoyerNouveauCode(utilisateur, TypeCode.INSCRIPTION);
    }

    // Ne revele jamais si l'e-mail existe ou non (evite l'enumeration de comptes) :
    // le controleur renvoie toujours le meme message, que l'envoi ait eu lieu ou pas.
    public void demanderReinitialisation(EmailRequest requete) {
        utilisateurRepository.findByEmail(requete.email())
                .ifPresent(utilisateur -> envoyerNouveauCode(utilisateur, TypeCode.REINITIALISATION));
    }

    public void reinitialiserMotDePasse(ReinitialisationMotDePasseRequest requete) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(requete.email())
                .orElseThrow(() -> new CodeInvalideException("Code invalide ou expire"));

        CodeVerification code = codeVerificationRepository
                .findTopByUtilisateurIdAndTypeAndUtiliseFalseOrderByDateCreationDesc(utilisateur.getId(), TypeCode.REINITIALISATION)
                .orElseThrow(() -> new CodeInvalideException("Code invalide ou expire"));

        validerCode(code, requete.code());
        utilisateur.setMotDePasse(passwordEncoder.encode(requete.nouveauMotDePasse()));
    }

    private void validerCode(CodeVerification stocke, String fourni) {
        if (!stocke.getCode().equals(fourni)) {
            throw new CodeInvalideException("Code incorrect");
        }
        if (stocke.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new CodeInvalideException("Ce code a expire, demande-en un nouveau");
        }
        stocke.setUtilise(true);
    }

    private void envoyerNouveauCode(Utilisateur utilisateur, TypeCode type) {
        CodeVerification code = new CodeVerification();
        code.setUtilisateur(utilisateur);
        code.setType(type);
        code.setCode(genererCode());
        code.setDateExpiration(LocalDateTime.now().plusMinutes(DUREE_VALIDITE_MINUTES));
        codeVerificationRepository.save(code);

        emailService.envoyerCode(utilisateur.getEmail(), utilisateur.getPrenom(), code.getCode(), type);
    }

    private String genererCode() {
        return String.format("%06d", ALEATOIRE.nextInt(1_000_000));
    }

    private UtilisateurDTO versDTO(Utilisateur utilisateur) {
        return new UtilisateurDTO(
                utilisateur.getIdentifiantPublic(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole(),
                utilisateur.getDateCreation()
        );
    }
}
