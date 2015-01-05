OC-Flow
=======
Das Organic Computing Projek simuliert Menschen, welche sich entlang eines Tunnels in zwei unterschiedliche Richtungen bewegen. Die Menschen befolgen verschiedene Regeln. Sie werden an zufällig Positionen initialisiert und können unterschiedlich Geschwindigkeiten besitzen. 

Es wird das Mason-Framwork verwendet.

Regeln
-------
* Regel 0 (Randomwalk): Person bewegt sich in zufällige Richtung
* Regel 1: Person verhält sich wie in [Flowchart dargestellt](doc/Regel1.png)
* Regel 2: Person verhält sich nach Regel 1. Wenn sie sich dabei staySteps-mal nicht bewegen kann, führt sie für einen Schritt Regel 0 aus.

Anleitung
--------
Alle Einstellungen werden durch verändern der Datei [Config.java](src/Config.java) eingestellt.
Die Variablen bedeuten:
* numRightRule1: Anzahl der Menschen, die sich nach rechts nach Regel 1 bewegen
* numLeftRule1: Anzahl der Menschen, die sich nach links nach Regel 1 bewegen
* numRightRule2: Anzahl der Menschen, die sich nach rechts nach Regel 2 bewegen
* numLeftRule2: Anzahl der Menschen, die sich nach links nach Regel 2 bewegen
* randomWalk: Anzahl der Menschen, die sich nach Regel 0 bewegen
* pausemax: Maximale Anzahl an Schritten, die pausiert wird bis ein weitere Schritt ausgeführt wird (Inverse Geschwindigkeit)
* pausemin: Minimale Anzahl an Schritten, die pausiert wird bis ein weiterer Schritt ausgeführt wird (Inverse Geschwindigkeit)
* staySteps: Anzal der Schritte, die eine Person (Regel 2) auf der Stelle stehen muss bevor sie Regel 0 ausführt.
* width: Feldbreite
* height: Feldhöhe

