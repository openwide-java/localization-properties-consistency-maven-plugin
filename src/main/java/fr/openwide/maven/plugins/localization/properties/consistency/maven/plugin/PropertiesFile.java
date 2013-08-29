package fr.openwide.maven.plugins.localization.properties.consistency.maven.plugin;

import java.io.File;
import java.util.Locale;

public class PropertiesFile {
	
	private File file;
	
	private String rootName;
	
	private Locale locale;
	
	private PropertiesEncoding encoding = PropertiesEncoding.ISO_8859_1;
	
	public PropertiesFile(File file) {
		this.file = file;
		
		splitFileName();
	}
	
	private void splitFileName() {
		String[] splitName = file.getName().split("[_\\.]");
		rootName = splitName[0];
		Boolean utf8Suffix = "utf8".equals(splitName[splitName.length - 2]);
		int suffixIndex = splitName.length - (1 + utf8Suffix.compareTo(false));
		
		if (utf8Suffix) {
			encoding = PropertiesEncoding.UTF_8;
		}
		
		if (suffixIndex == 1) {
			locale = Locale.getDefault();
		} else if (suffixIndex == 2) {
			locale = new Locale(splitName[1]);
		} else {
			locale = new Locale(splitName[1], splitName[2]);
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public String getRootName() {
		return rootName;
	}
	
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public PropertiesEncoding getEncoding() {
		return encoding;
	}
	
	public void setEncoding(PropertiesEncoding encoding) {
		this.encoding = encoding;
	}
}
