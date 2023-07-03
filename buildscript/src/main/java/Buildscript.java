import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

import io.github.coolcrabs.brachyura.decompiler.BrachyuraDecompiler;
import io.github.coolcrabs.brachyura.decompiler.fernflower.FernflowerDecompiler;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyCollector;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyFlag;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import io.github.coolcrabs.brachyura.quilt.QuiltMaven;
import io.github.coolcrabs.brachyura.quilt.SimpleQuiltProject;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends SimpleQuiltProject {
	private final Versions versions = new Versions(getProjectDir().resolve("buildscript").resolve("versions.properties"));

	@Override
	public int getJavaVersion() {
		return Integer.parseInt(versions.JAVA.get());
	}

	@Override
	public @Nullable BrachyuraDecompiler decompiler() {
		return new FernflowerDecompiler(Maven.getMavenJarDep(QuiltMaven.URL, new MavenId("org.quiltmc", "quiltflower", versions.QUILTFLOWER.get())));
	}

	@Override
	public FabricLoader getLoader() {
		return new FabricLoader(QuiltMaven.URL, QuiltMaven.loader(versions.QUILT_LOADER.get()));
	}

	@Override
	public VersionMeta createMcVersion() {
		return Minecraft.getVersion(versions.MINECRAFT.get());
	}

	@Override
	public MappingTree createMappings() {
		return createMojmap();
	}

	@Override
	public void getModDependencies(ModDependencyCollector d) {
		for (MavenIdWithRepo id : new TransitiveDepResolveTask(new String[] { QuiltMaven.URL }, new MavenId(QuiltMaven.GROUP_ID + ".quilted-fabric-api", "quilted-fabric-api", versions.QUILTED_FABRIC_API.get())).get()) {
			d.addMaven(id.repo, id.mavenId, ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME);
		}

		for (MavenIdWithRepo id : new TransitiveDepResolveTask(new String[] { "https://mvn.devos.one/snapshots/", "https://jitpack.io/", Maven.MAVEN_CENTRAL }, new MavenId("io.github.fabricators_of_create.Porting-Lib:Porting-Lib:2.1.999+1.20")).get()) {
			if (id.mavenId.artifactId.equals("model_generators")) continue; // Breaks remapping
			jij(d.addMaven(id.repo, id.mavenId, ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
		}

		for (String module : new String[] {
			"base",
			"chunk",
			"entity"
		}) {
			jij(d.addMaven("https://maven.ladysnake.org/releases/", new MavenId("dev.onyxstudios.cardinal-components-api", "cardinal-components-" + module, "5.2.1"), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
		}

		jij(d.addMaven(Maven.MAVEN_CENTRAL, new MavenId("com.electronwill.night-config:core:3.6.5"), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
		jij(d.addMaven(Maven.MAVEN_CENTRAL, new MavenId("com.electronwill.night-config:toml:3.6.5"), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
		jij(d.addMaven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/", new MavenId("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:8.0.0"), ModDependencyFlag.COMPILE, ModDependencyFlag.RUNTIME));
	}

	@Override
	public Path[] getSrcDirs() {
		return new Path[]{getProjectDir().resolve("src").resolve("main").resolve("java"), getProjectDir().resolve("src").resolve("api").resolve("java")};
	}
}
