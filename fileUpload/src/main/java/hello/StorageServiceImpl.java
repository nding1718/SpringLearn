package hello;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.SringUtils;
import org.springframework.web.multipart.MultpartFile;

@Service
public class StorageServiceImpl implements StroageService {

	private final Path rootLocation;

	@Autowired
	public StroageServiceImpl(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFileName());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Faild to store empty file" + filename);
			}
			if (filename.contains("..")) {
				throw new StroageException("Cannot store file with relative path outside current directory" + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream = this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOExcetion e) {
			throw new StroageException("Failed to stroe file " + filename, e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation)).map(this.rootLocation::relativize);
		}
		catch (IOExcetion e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	@Ovrride
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resouce loadAsResource(String filename){
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Ovrride
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}