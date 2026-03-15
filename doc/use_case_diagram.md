# Diagramme de Cas d'Utilisation

Ce diagramme illustre les interactions possibles entre les différents acteurs (Étudiant, Professeur, Administrateur) et le système EST SB Smart Attendance.

```mermaid
usecaseDiagram
    actor "Étudiant" as student
    actor "Professeur" as prof
    actor "Administrateur" as admin

    rectangle "EST SB Smart Attendance" {
        usecase "Se connecter" as UC_Login
        usecase "Se déconnecter" as UC_Logout
        usecase "Consulter son tableau de bord" as UC_DashStudent
        usecase "Scanner un QR Code" as UC_ScanQR
        usecase "Consulter son historique" as UC_HistStudent
        
        usecase "Consulter tableau de bord Professeur" as UC_DashProf
        usecase "Générer une session QR" as UC_GenQR
        usecase "Terminer une session" as UC_EndSession
        usecase "Consulter l'historique global" as UC_HistProf
        usecase "Voir les détails d'une session" as UC_SessionDetails
    }

    student --> UC_Login
    student --> UC_DashStudent
    student --> UC_ScanQR
    student --> UC_HistStudent
    student --> UC_Logout

    prof --> UC_Login
    prof --> UC_DashProf
    prof --> UC_GenQR
    prof --> UC_EndSession
    prof --> UC_HistProf
    prof --> UC_SessionDetails
    prof --> UC_Logout

    admin --> UC_Login
    admin --> UC_DashProf
    admin --> UC_GenQR
    admin --> UC_HistProf
    admin --> UC_SessionDetails
    admin --> UC_Logout

    %% L'administrateur hérite des droits du professeur dans cette application
    admin -|> prof

    %% Inclusions (Includes)
    UC_GenQR ..> UC_EndSession : <<include>>
    UC_ScanQR ..> UC_Login : <<precedes>>
```
