# Come Together Planner

Eine einfache Webanwendung, um schnell und unkompliziert Termine für Gruppen zu finden.

## Wofür ist das?

Mit dem Come Together Planner kannst du ein Ereignis erstellen und mehrere Terminvorschläge machen. Anschließend teilst du einen Link mit den Teilnehmern, die dann abstimmen können, welche Termine für sie passen. So siehst du auf einen Blick, welcher Termin für die meisten Teilnehmer am besten geeignet ist.

## Wie funktioniert es?

1.  **Anwendung starten:** Die Anwendung ist ein Standard-Spring-Boot-Projekt.
2.  **Ereignis erstellen:** Öffne die Webanwendung im Browser, gib einen Namen für dein Ereignis ein und füge einen oder mehrere Terminvorschläge hinzu.
3.  **Link teilen:** Nach dem Erstellen erhältst du einen einzigartigen Link zu deiner Terminfindungsseite.
4.  **Abstimmen lassen:** Teile diesen Link mit allen gewünschten Teilnehmern. Diese können auf der Seite ihren Namen eingeben und für die Termine abstimmen, die ihnen passen.
5.  **Besten Termin finden:** Die Übersicht zeigt dir an, wie viele Teilnehmer für jeden Termin zugesagt haben, sodass du den optimalen Termin für alle finden kannst.

## Zugang & Sicherheit

Um die Erstellung und Verwaltung von Terminen zu schützen, ohne die Dinge zu verkomplizieren, gibt es **keine** klassische Benutzerverwaltung mit individuellen Konten. Stattdessen wird die Anwendung durch ein einziges, globales Passwort geschützt, das in der Konfigurationsdatei (`application.yml`) festgelegt wird.

Jeder, der dieses Passwort kennt, kann neue Terminumfragen erstellen und bestehende verwalten. Das Abstimmen selbst erfordert kein Passwort, um es für die Teilnehmer so einfach wie möglich zu machen.

## Entwicklung & Ausführung

### Voraussetzungen

*   Java 21
*   Maven

### Ausführen

Du kannst die Anwendung mit dem folgenden Maven-Befehl starten:

```sh
mvn spring-boot:run
```

Die Anwendung läuft standardmäßig auf Port 80.

### Datenbank

*   **Standard-Profil:** Verwendet eine dateibasierte H2-Datenbank. Die Daten werden in der Datei `./data/cometogether` gespeichert.
*   **`dev`-Profil:** Um die Anwendung mit einer In-Memory-Datenbank für Entwicklungszwecke zu starten, verwende das `dev`-Profil:
    ```sh
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
    ```

## Releases & Deployment

Für dieses Projekt ist eine GitHub Action eingerichtet, die bei jedem Push eines Tags im Format `v*` (z.B. `v1.0.0`) automatisch ein neues Release auf GitHub erstellt. Das gebaute Anwendungs-JAR wird diesem Release als Artefakt beigefügt und kann von dort heruntergeladen werden.
