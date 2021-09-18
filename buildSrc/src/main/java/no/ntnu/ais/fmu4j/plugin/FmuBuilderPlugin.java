package no.ntnu.ais.fmu4j.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.DependencyResolutionListener;
import org.gradle.api.artifacts.ResolvableDependencies;

public class FmuBuilderPlugin implements Plugin<Project>, DependencyResolutionListener {

    private Project project;
    private FmuBuilder buildFmu;

    @Override
    public void apply(Project target) {
        this.project = target;
        this.buildFmu = target.getTasks().create("fmu4j", FmuBuilder.class);
        this.buildFmu.dependsOn(target.getTasks().getByName("build"));
        this.buildFmu.setGroup("fmu-export");
        target.getGradle().addListener(this);
    }

    @Override
    public void beforeResolve(ResolvableDependencies dependencies) {
//        String version = buildFmu.version;
//        DependencySet compile = project.getConfigurations().getByName("compile").getDependencies();
//        compile.add(project.getDependencies().create("no.ntnu.ihb.fmi4j:fmi-export:" + version));
        project.getGradle().removeListener(this);
    }

    @Override
    public void afterResolve(ResolvableDependencies dependencies) {
    }

}
