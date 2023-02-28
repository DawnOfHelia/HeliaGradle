package fr.welsy.dawnofhelia.tasks;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.TA_GridThemes;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import de.vandermeer.translation.characters.Html2AsciiDoc;
import de.vandermeer.translation.characters.Text2Html;
import de.vandermeer.translation.targets.Text2AsciiDoc;
import fr.welsy.dawnofhelia.console.UnicodeColor;
import fr.welsy.dawnofhelia.file.ZipHelper;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.gradle.internal.impldep.org.apache.commons.io.filefilter.RegexFileFilter;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class ZipAssets extends DefaultTask {
    private String resourcePath;

    @TaskAction
    public void run() {
        System.out.println("# Current Ressources Path: " + resourcePath);
        List<File> files = new ArrayList<>();

        try {
            Files.find(Paths.get(resourcePath), 999, (p, bfa) -> bfa.isRegularFile()).forEach(f -> {
                files.add(f.toFile());
            });

            Configuration configuration = getProject().getConfigurations().getByName("compileClasspath");

            ResolvedArtifact resolvedArtifact = configuration.getResolvedConfiguration().getResolvedArtifacts().stream().filter(d -> d.getFile().getAbsolutePath().endsWith("client-extra.jar")).findAny().orElse(null);
            if(resolvedArtifact == null){
                throw new RuntimeException("client-extra.jar not found in libraries, verify you are in the correct project (mcp-reborn & dawn-of-helia)");
            }

            File target = resolvedArtifact.getFile();
            File backupClone = new File(target.getParentFile(), target.getName() + "-old-backup");
            if(backupClone.exists())
                backupClone.delete();

            Files.copy(target.toPath(), backupClone.toPath());
            System.out.println("Moved client-extra.jar to client-extra.jar-old-backup");

            Map<File, Long> timings = new HashMap<>();

            for(File localResource : files){
                long time = System.currentTimeMillis();
                String absPath = localResource.getParentFile().getAbsolutePath();
                String f = absPath.replace(resourcePath, "");
                f = f.substring(1, f.length());
                f += File.separator;

                ZipHelper.addFilesToZip(target, localResource, f);
                timings.put(localResource, System.currentTimeMillis() - time);
            }

            AsciiTable header = new AsciiTable();
            header.addRule();
            header.addRow("HeliaGradle");
            header.addRow("Summary of Task ZipAssets");
            header.addRow(" ");

            AsciiTable at = new AsciiTable();
            at.addRow("Asset Path", "Copy Duration (ms)");
            at.addRule();

            long totalTime = 0;

            for(File localResource : files){
                String absPath = localResource.getParentFile().getAbsolutePath();
                String f = absPath.replace(resourcePath, "");
                f = f.substring(1, f.length());
                f += File.separator;

                at.addRow(" " + f + localResource.getName(), " " + timings.get(localResource).intValue() + "ms");
                totalTime += timings.get(localResource).intValue();
            }

            at.addRule();
            at.setTextAlignment(TextAlignment.CENTER);
            header.setTextAlignment(TextAlignment.CENTER);

            AsciiTable footer = new AsciiTable();
            footer.addRow("Total copy time: " + totalTime + "ms");
            footer.addRow("Output file: " + target.getAbsolutePath());
            footer.setTextAlignment(TextAlignment.CENTER);
            footer.addRule();

            this.printSummary(header, at, footer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void printSummary(AsciiTable header, AsciiTable at, AsciiTable footer) {
        header.getContext().setGridTheme(TA_GridThemes.NONE);
        at.getContext().setGridTheme(TA_GridThemes.NONE);
        footer.getContext().setGridTheme(TA_GridThemes.NONE);
        String a = header.render(at.getContext().getWidth());
        String b = at.render();
        String c = footer.render(at.getContext().getWidth());
        System.out.printf("%s %s %s %n", UnicodeColor.RED_BOLD_BRIGHT, a, UnicodeColor.RESET);
        System.out.printf("%s %s %s %n", UnicodeColor.PURPLE_BRIGHT, b, UnicodeColor.RESET);
        System.out.printf("%s %s %s %n", UnicodeColor.GREEN_BOLD_BRIGHT, c, UnicodeColor.RESET);

    }

    public void setRessourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
