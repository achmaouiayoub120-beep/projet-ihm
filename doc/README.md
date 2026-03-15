# EST SB Smart Attendance — Documentation du Projet

## 📋 Informations Générales

| Élément | Détails |
|---------|---------|
| **Nom du projet** | EST SB Smart Attendance |
| **Type** | Application mobile Android native |
| **Technologie** | Java / XML / Android Studio |
| **Base de données** | SQLite (locale) |
| **Session utilisateur** | SharedPreferences |
| **Scanner QR** | ZXing (zxing-android-embedded) |
| **SDK minimum** | API 24 (Android 7.0) |
| **SDK cible** | API 34 (Android 14) |
| **Langue UI** | Français |

## 🏫 Contexte Académique

Ce projet a été développé dans le cadre d'un mini-projet à l'**École Supérieure de Technologie de Sidi Bennour (EST SB)**. Il s'agit d'un système de gestion de présence intelligent utilisant des QR codes.

### Objectif
Permettre aux professeurs de générer des QR codes de session et aux étudiants de scanner ces codes pour enregistrer leur présence automatiquement.

### Contraintes techniques
- Application **100% locale** (pas de Firebase, pas d'API, pas de serveur)
- Utilisation d'au moins **3 interfaces Java** (exigence du professeur)
- Architecture **Android natif** (pas de framework cross-platform)

## 🔐 Comptes de Démonstration

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| `student@estsb.ma` | `password` | Étudiant |
| `prof@estsb.ma` | `password` | Professeur |
| `admin@estsb.ma` | `password` | Administrateur |

## 📱 Fonctionnalités Principales

### Professeur
1. Tableau de bord avec statistiques (sessions, présences, taux)
2. Génération de QR Code avec timer de 2 minutes
3. Régénération automatique du QR à l'expiration
4. Consultation de l'historique de toutes les présences
5. Détails de chaque session avec liste des étudiants

### Étudiant
1. Tableau de bord avec résumé de présence
2. Scanner de QR Code via caméra
3. Enregistrement automatique de la présence
4. Blocage des doublons (un scan par session)
5. Historique personnel des présences

### Commun
- Écran de connexion avec validation
- Option "Se souvenir de moi"
- Déconnexion sécurisée
- Design premium (glassmorphism, dégradés, animations)
