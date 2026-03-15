# Modèle Conceptuel de Données (MCD) / ERD

Ce diagramme illustre la structure de la base de données SQLite `smart_attendance.db` utilisée dans l'application. L'application utilise une architecture 100% locale pour ce modèle.

```mermaid
erDiagram
    USERS ||--o{ ATTENDANCE : "marque"
    SESSIONS ||--o{ ATTENDANCE : "contient"
    USERS ||--o{ SESSIONS : "crée"

    USERS {
        int id PK "AUTOINCREMENT"
        string first_name "NOT NULL"
        string last_name "NOT NULL"
        string email "UNIQUE NOT NULL"
        string password "NOT NULL"
        string role "student, professor, admin"
    }

    SESSIONS {
        int id PK "AUTOINCREMENT"
        string module_name "NOT NULL"
        string group_name "NOT NULL"
        string qr_data "UNIQUE QR String"
        string created_at "Date Time"
        int professor_id FK
        string professor_name "Denormalized for perf"
        int is_active "1 = Active, 0 = Ended"
    }

    ATTENDANCE {
        int id PK "AUTOINCREMENT"
        int student_id FK
        string student_name "Denormalized for perf"
        int session_id FK
        string module_name "Denormalized for perf"
        string status "present, absent, late"
        string scanned_at "Date Time"
    }
```

## Description des Entités

1. **USERS (Utilisateurs)** : Stocke tous les comptes utilisateurs (étudiants, professeurs, administrateurs). Authentification via `email` et `password`.
2. **SESSIONS (Séances d'absence)** : Représente une séance créée par un professeur. Le champ `qr_data` stocke la chaîne unique encodée dans le code QR que les étudiants vont scanner. `is_active` permet de bloquer les scans une fois la séance terminée.
3. **ATTENDANCE (Présences)** : Table associative qui relie un étudiant à une session. Interdit les doublons (un étudiant ne peut avoir qu'une seule présence par session active).
