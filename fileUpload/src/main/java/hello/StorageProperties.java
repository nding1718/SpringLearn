package hello;

import org.springframework.boot.context.properties.ConfigurationProperties;

// the reason we add the storage
@ConfigurationProperties("storage")
public class StorageProperties {

	private String location = "upload-dir";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}