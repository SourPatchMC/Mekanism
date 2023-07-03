import java.util.Objects;

import io.github.coolcrabs.brachyura.maven.MavenId;

public class MavenIdWithRepo {
	public String repo;
	public MavenId mavenId;

	public MavenIdWithRepo(String repo, MavenId mavenId) {
		this.repo = repo;
		this.mavenId = mavenId;
	}

	public int hashCode() {
		return Objects.hash(repo, mavenId);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof MavenIdWithRepo) {
			MavenIdWithRepo other = (MavenIdWithRepo) obj;
			return repo.equals(other.repo) && mavenId.equals(other.mavenId);
		} else {
			return false;
		}
	}
}
