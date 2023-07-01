import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import io.github.coolcrabs.brachyura.util.Lazy;

public class Versions {
	private final Path file;

	Versions(Path file) {
		this.file = file;
	}

	private final Lazy<Properties> properties = new Lazy<>(this::loadProperties);

	private Properties loadProperties() {
		try {
			Properties properties = new Properties();

			if (Files.exists(file)) {
				try (BufferedReader reader = Files.newBufferedReader(file)) {
					properties.load(reader);
				}
			} else {
				throw new RuntimeException("Couldn't find versions.properties");
			}

			return properties;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public final Version JAVA = new Version("java", properties);
	public final Version MINECRAFT = new Version("minecraft", properties);
	public final Version QUILT_LOADER = new Version("quilt_loader", properties);
	public final Version QUILTFLOWER = new Version("quiltflower", properties);

	public final Version QUILTED_FABRIC_API = new Version("quilted_fabric_api", properties);
}
