package fr.openwide.maven.plugins.localization.properties.consistency.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Mojo(name = "scan", defaultPhase = LifecyclePhase.VALIDATE)
public class LocalizationPropertiesConsistencyMojo extends AbstractMojo {

	private static final String PROPERTIES_FILE_PATTERN = ".*(_[a-z]{2})+(.utf8)?.properties";

	@Component
	private MavenProject project;

	@Parameter(defaultValue = "src/main/java", required = true)
	private File sourceDirectory;
	
	@Parameter(defaultValue = "src/main/resources", required = true)
	private File resourceDirectory;
	
	@Parameter
	private List<File> additionalSourceDirectories;

	public void execute() throws MojoExecutionException {
		if ("pom".equalsIgnoreCase(project.getPackaging())) {
			return;
		}
		
		List<File> propertiesFileList = getPropertiesFileList();
		while (!propertiesFileList.isEmpty()) {
			List<PropertiesFile> propertiesFileGroup = groupPropertiesFiles(propertiesFileList);
			Properties properties = getFileGroupProperties(propertiesFileGroup);
			
			for (PropertiesFile propertiesFile : propertiesFileGroup) {
				logMissingProperties(scanFileForMissingProperties(properties, propertiesFile));
			}
		}
	}
	
	private void logMissingProperties(final Map<String, String> missingProperties) throws MojoExecutionException {
		for (Map.Entry<String, String> entry : missingProperties.entrySet()) {
			StringBuilder sb = new StringBuilder(entry.getValue());
			sb.append(": missing property '")
				.append(entry.getKey())
				.append("'");
			getLog().error(sb.toString());
		}
		
		if (!missingProperties.isEmpty()) {
			throw new MojoExecutionException("One of the properties file is not complete");
		}
	}
	
	private Map<String, String> scanFileForMissingProperties(final Properties properties, final PropertiesFile fileToScan) {
		Map<String, String> missingProperties = Maps.newTreeMap();
		Properties fileToScanProperties = new Properties();
		
		loadPropertiesFromFile(fileToScanProperties, fileToScan);
		for (String property : properties.stringPropertyNames()) {
			if (fileToScanProperties.getProperty(property) == null) {
				missingProperties.put(property, fileToScan.getFile().getName());
			}
		}
		return missingProperties;
	}
	
	private Properties getFileGroupProperties(final List<PropertiesFile> propertiesFileGroup) {
		Properties properties = new Properties();
		for (PropertiesFile propertiesFile : propertiesFileGroup) {
			loadPropertiesFromFile(properties, propertiesFile);
		}
		
		return properties;
	}
	
	private void loadPropertiesFromFile(final Properties properties, final PropertiesFile propertiesFile) {
		InputStreamReader reader = null;
		try {
			final FileInputStream inputStream = new FileInputStream(propertiesFile.getFile());
			reader = new InputStreamReader(inputStream, propertiesFile.getEncoding().getCharset());
			properties.load(reader);
		} catch (Exception e) {
			getLog().error("An error occurred while reading the properties file: " + propertiesFile.getFile().getName(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// nothing
				}
			}
		}
	}
	
	private List<PropertiesFile> groupPropertiesFiles(final List<File> propertiesFileList) {
		List<PropertiesFile> propertiesFileGroup = Lists.newArrayList();
		List<File> filesToRemove = Lists.newArrayList();
		String rootName = null;
		
		for (File file : propertiesFileList) {
			PropertiesFile propertiesFile = new PropertiesFile(file);
			if (rootName == null) {
				rootName = propertiesFile.getRootName();
				
				propertiesFileGroup.add(getRootPropertiesFile(propertiesFile));
			}
			if (rootName.equals(propertiesFile.getRootName())) {
				propertiesFileGroup.add(propertiesFile);
				filesToRemove.add(file);
			}
		}
		
		propertiesFileList.removeAll(filesToRemove);
		return propertiesFileGroup;
	}
	
	private PropertiesFile getRootPropertiesFile(PropertiesFile propertiesFile) {
		String encoding = (PropertiesEncoding.UTF_8.equals(propertiesFile.getEncoding()) ? ".utf8" : "");
		String rootPropertiesFileName = propertiesFile.getRootName() + encoding + ".properties";
		
		return new PropertiesFile(new File(propertiesFile.getFile().getParent() + "/" + rootPropertiesFileName));
	}

	private List<File> getPropertiesFileList() {
		List<File> propertiesFileList = Lists.newArrayList();
		
		propertiesFileList.addAll(scanDirectory(sourceDirectory));
		propertiesFileList.addAll(scanDirectory(resourceDirectory));
		
		if (additionalSourceDirectories != null) {
			for (File directory : additionalSourceDirectories) {
				propertiesFileList.addAll(scanDirectory(directory));
			}
		}
		return propertiesFileList;
	}

	private Collection<File> scanDirectory(final File directory) {
		if (!directory.isDirectory()) {
			return Collections.emptyList();
		}
		
		RegexFileFilter regexFileFilter = new RegexFileFilter(PROPERTIES_FILE_PATTERN, IOCase.INSENSITIVE);
		return FileUtils.listFiles(directory, regexFileFilter, FileFilterUtils.trueFileFilter());
	}
}
