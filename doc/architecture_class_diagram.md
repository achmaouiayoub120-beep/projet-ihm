# Architecture Logicielle (MVC / Singleton)

L'application suit une structure proche du **Modèle-Vue-Contrôleur (MVC)** adaptée pour Android natif, couplée à l'utilisation du patron de conception **Singleton** pour la persistance locale.

```mermaid
classDiagram
    %% Package: activities (Vues & Contrôleurs)
    class LoginActivity {
        <<Activity>>
        -DatabaseHelper dbHelper
        -SessionManager sessionManager
        +onCreate()
        +validateCredentials()
        +navigateToDashboard(String role)
    }

    class GenerateQRActivity {
        <<Activity>>
        -QRCodeHelper qrHelper
        +onCreate()
        +startCountdown()
        +onQRCodeGenerated(Bitmap, String)
        +onQRCodeError(String)
    }

    class ScanQRActivity {
        <<Activity>>
        -ActivityResultLauncher scanLauncher
        +startScan()
        +handleScanResult(String)
        +onAttendanceSuccess(String, String)
        +onAttendanceFailure(String)
    }

    %% Package: interfaces (Contrats / Callbacks)
    class UserRoleNavigator {
        <<Interface>>
        +navigateToDashboard(String role)
        +getAvailableActions(String role)
        +onQuickActionClicked(String actionId)
    }

    class OnQRCodeGenerated {
        <<Interface>>
        +onQRCodeGenerated(Bitmap, String)
        +onQRCodeError(String)
    }

    class OnAttendanceMarked {
        <<Interface>>
        +onAttendanceSuccess(String, String)
        +onAttendanceFailure(String)
    }

    %% Package: database (Modèle de données)
    class DatabaseHelper {
        <<Singleton>>
        -DatabaseHelper instance$
        +getInstance(Context) DatabaseHelper$
        +validateLogin() User
        +createSession() long
        +markAttendance() long
        +getActiveSessionByQR() Session
    }

    %% Package: models (POJOs)
    class Session {
        -int id
        -String moduleName
        -String qrData
        +getModuleName()
    }

    class Attendance {
        -int id
        -int studentId
        -String status
        +getStatus()
    }

    class User {
        -int id
        -String email
        -String role
        +getRole()
    }

    %% Relations
    LoginActivity ..|> UserRoleNavigator : "implements"
    GenerateQRActivity ..|> OnQRCodeGenerated : "implements"
    ScanQRActivity ..|> OnAttendanceMarked : "implements"

    LoginActivity --> DatabaseHelper : "utilise"
    ScanQRActivity --> DatabaseHelper : "utilise"
    GenerateQRActivity --> DatabaseHelper : "utilise"

    DatabaseHelper --> User : "retourne"
    DatabaseHelper --> Session : "retourne"
    DatabaseHelper --> Attendance : "retourne"
```

## Structure de Packages

- `com.estsb.smartattendance.activities` : La logique de présentation (UI + interactions).
- `com.estsb.smartattendance.models` : Les classes objets (User, Session, Attendance).
- `com.estsb.smartattendance.database` : La couche d'accès aux données (SQLiteOpenHelper).
- `com.estsb.smartattendance.interfaces` : Les contrats de callback (exigence académique).
- `com.estsb.smartattendance.adapters` : Les adaptateurs RecyclerView (`AttendanceAdapter`).
- `com.estsb.smartattendance.utils` : Les helpers et gestion de session.
