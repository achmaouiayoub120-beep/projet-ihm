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

## 🚀 Comment Lancer / Installer le Projet

### Prérequis
- **Android Studio** installé sur votre machine.
- Un émulateur Android (AVD) ou un appareil physique sous **Android 7.0 (API 24) minimum**.

### Étapes d'installation
1. **Cloner ou télécharger** le projet sur votre machine locale.
2. Lancer **Android Studio**.
3. Cliquer sur **"Open"** (Ouvrir) et sélectionner le dossier racine du projet.
4. Patienter pendant la **synchronisation de Gradle** (cela peut prendre quelques minutes lors de la première ouverture).
5. Sélectionner un appareil cible (Émulateur ou Appareil physique) dans le menu déroulant en haut de l'interface.
6. Cliquer sur le bouton vert **"Run 'app'"** (▶️) ou utiliser le raccourci `Shift + F10` pour compiler et installer l'application sur l'appareil.

> **Note :** Le projet utilise une base de données locale (SQLite). Aucune configuration serveur ou API n'est requise. Vous pouvez vous connecter directement avec les comptes de démonstration fournis plus haut !
