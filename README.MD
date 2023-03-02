
# Helia Gradle

Plugin permettant d'ajouter des tasks custom pour MCP-Reborn (tout est testé avec la version 1.15.2 de Minecraft via mcp-reborn)

# Comment ajouter le plugin à un projet Gradle ?
Ajoutez ceci tout en haut de votre build.gradle:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'fr.welsy.dawnofhelia:HeliaGradle:1.0'
    }
}

apply plugin: 'fr.welsy.dawnofhelia.HeliaGradle'
``` 

# Qu'est-ce que ce plugin ajoute ?

• Une tâche zipAssets qui permet de mettre tout le contenu de votre src/main/ressources dans le client-extra.jar (la tache récupère l'emplacement de la lib selon le pc).

# On peut contribuer ?
Ouais mon gars, fait des pull requests on est là.