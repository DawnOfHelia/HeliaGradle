package fr.welsy.dawnofhelia;

import fr.welsy.dawnofhelia.tasks.ZipAssets;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.*;

public class HeliaGradle implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        final TaskContainer tasks = project.getTasks();
        final TaskProvider<ZipAssets> zipAssets = tasks.register("zipAssets", ZipAssets.class);
        zipAssets.get().setGroup("helia-gradle");
        zipAssets.configure(task -> {
            SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
            task.setRessourcePath(mainSourceSet.getResources().getSrcDirs().iterator().next().getAbsolutePath());
        });
    }
}
