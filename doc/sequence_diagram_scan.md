# Diagramme de Séquence : Flux de Scan QR

Ce diagramme de séquence illustre une des fonctionnalités clés de l'application : l'enregistrement de la présence par un étudiant via le scan d'un QR code. L'utilisation de l'interface obligatoire `OnAttendanceMarked` y est mise en évidence.

```mermaid
sequenceDiagram
    actor E as Étudiant
    participant UI as ScanQRActivity
    participant ZX as ZXing Scanner
    participant DB as DatabaseHelper
    participant CB as OnAttendanceMarked (Interface)

    E->>UI: Clique "Scanner QR Code"
    UI->>ZX: startScan() (Lance la caméra)
    ZX-->>E: Affiche le viseur caméra
    E->>ZX: Scanne le QR Code du professeur
    ZX-->>UI: Retourne scanResult (qrData)

    UI->>DB: getActiveSessionByQR(qrData)
    
    alt Session trouvée et active
        DB-->>UI: Session Object (id=12, module="Java")
        
        UI->>DB: hasAttendance(studentId, sessionId)
        
        alt Présence non existante
            DB-->>UI: false (Pas de doublon)
            UI->>DB: markAttendance(student, session, "present")
            DB-->>UI: insertId (success)
            
            UI->>CB: onAttendanceSuccess(studentName, moduleName)
            CB-->>UI: Affiche message "Présence enregistrée!"
            UI-->>E: Interface de succès (Checkmark vert)
        else Présence déjà enregistrée
            DB-->>UI: true (Doublon détecté)
            UI->>CB: onAttendanceFailure("Présence déjà enregistrée")
            CB-->>UI: Toast d'erreur
            UI-->>E: Affiche erreur
        end
        
    else Session expirée ou invalide
        DB-->>UI: null
        UI->>CB: onAttendanceFailure("QR Code invalide")
        CB-->>UI: Toast d'erreur
        UI-->>E: Affiche erreur
    end
```
