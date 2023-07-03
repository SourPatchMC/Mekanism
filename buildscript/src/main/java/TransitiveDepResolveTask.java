import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.util.Lazy;
import io.github.coolcrabs.brachyura.util.NetUtil;
import io.github.coolcrabs.brachyura.util.PathUtil;
import io.github.coolcrabs.brachyura.util.StreamUtil;
import io.github.coolcrabs.brachyura.util.Util;

public class TransitiveDepResolveTask {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	{
		try {
			DOCUMENT_BUILDER_FACTORY.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (ParserConfigurationException e) {
			throw Util.sneak(e);
		}
	}

	private final Lazy<Set<MavenIdWithRepo>> value = new Lazy<>(this::resolve);
	private final String[] repositories;
	private final MavenIdWithRepo mavenId;
	private HashSet<MavenIdWithRepo> resolved = new HashSet<>();

	public TransitiveDepResolveTask(String[] repositories, MavenId mavenId) {
		this.repositories = repositories;
		this.mavenId = new MavenIdWithRepo(repositories[0], mavenId);
	}

	public TransitiveDepResolveTask(String[] repositories, MavenIdWithRepo mavenId) {
		this.repositories = repositories;
		this.mavenId = mavenId;
	}

	private static Node nodeByName(NodeList nodes, String name) {
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().equals(name)) return node;
		}
		return null;
	}

	private Set<MavenIdWithRepo> resolve() {
		String fileName = mavenId.mavenId.artifactId + "-" + mavenId.mavenId.version;
		if (resolved.contains(mavenId)) return Collections.emptySet();
		resolved.add(mavenId);

		HashSet<MavenIdWithRepo> output = new HashSet<>();
		output.add(mavenId);

		ArrayList<Lazy<Set<MavenIdWithRepo>>> lazies = new ArrayList<>();
		try {
			Path cachedFilePath = PathUtil.cachePath().resolve(fileName + ".deps.txt");
			if (!Files.exists(cachedFilePath)) {
				URL url = new URL(
					(mavenId.repo.endsWith("/") ? mavenId.repo : mavenId.repo + "/")
					+ mavenId.mavenId.groupId.replace('.', '/')
					+ "/"
					+ mavenId.mavenId.artifactId
					+ "/"
					+ mavenId.mavenId.version
					+ "/"
					+ fileName + ".pom"
				);

				try (InputStream inputStream = NetUtil.inputStream(url)) {
					StringBuilder builder = new StringBuilder();
					Document pomDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
					Node dependenciesNode = nodeByName(pomDocument.getLastChild().getChildNodes(), "dependencies");
					if (dependenciesNode != null) {
						NodeList dependencyNodes = dependenciesNode.getChildNodes();
						for (int i = 0; i < dependencyNodes.getLength(); i++) {
							Node dependencyNode = dependencyNodes.item(i);
							if (dependencyNode.getNodeName().contains("#")) continue;
							NodeList childNodes = dependencyNode.getChildNodes();
							String scope = nodeByName(childNodes, "scope").getTextContent();
							if (!scope.equals("compile")) continue;
							MavenIdWithRepo depMavenId = new MavenIdWithRepo(repositories[0], new MavenId(
								nodeByName(childNodes, "groupId").getTextContent(),
								nodeByName(childNodes, "artifactId").getTextContent(),
								nodeByName(childNodes, "version").getTextContent()
							));
							for (String depRepo : repositories) {
								URL depUrl = new URL(
									(depRepo.endsWith("/") ? depRepo : depRepo + "/")
									+ depMavenId.mavenId.groupId.replace('.', '/')
									+ "/"
									+ depMavenId.mavenId.artifactId
									+ "/"
									+ depMavenId.mavenId.version
									+ "/"
									+ depMavenId.mavenId.artifactId + "-" + depMavenId.mavenId.version + ".pom"
								);
								
								try {
									if (((HttpURLConnection) depUrl.openConnection()).getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
										depMavenId.repo = depRepo;
										break;
									}
								} catch (Throwable e) { }
							}
							TransitiveDepResolveTask task = new TransitiveDepResolveTask(repositories, depMavenId);
							task.resolved = resolved;
							lazies.add(task.value);
							if (i > 1) builder.append("\n");
							builder.append(depMavenId.repo + ";" + depMavenId.mavenId);
						}
					}
					Files.write(cachedFilePath, builder.toString().getBytes(), StandardOpenOption.CREATE_NEW);
				}
			} else {
				try (InputStream inputStream = Files.newInputStream(cachedFilePath)) {
					String[] deps = StreamUtil.readFullyAsString(inputStream).split("\n");
					for (String dep : deps) {
						if (dep.isEmpty()) continue;
						String[] depParts = dep.split(";");
						MavenIdWithRepo depMavenId = new MavenIdWithRepo(depParts[0], new MavenId(depParts[1]));
						TransitiveDepResolveTask task = new TransitiveDepResolveTask(repositories, depMavenId);
						task.resolved = resolved;
						lazies.add(task.value);
					}
				}
			}
		} catch (Exception e) {
			throw Util.sneak(e);
		}

		Lazy.getParallel(lazies).forEach(output::addAll);

		return output;
	}

	public Set<MavenIdWithRepo> get() {
		return value.get();
	}
}
