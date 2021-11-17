package org.wpstarters.repositoriesprocessor;

import jdk.jfr.Enabled;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("org.wpstarters.repositoriesprocessor.Homie")
@SupportedSourceVersion(SourceVersion.RELEASE_15)
public class MultitenancyJpaRepositoriesProcessor extends AbstractProcessor {

	private Yaml yamlLoader;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.yamlLoader = new Yaml();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			Map<String, Object> applicationProperties = loadApplicationProperties();
			roundEnv.getElementsAnnotatedWith(EnableJpaRepositories.class)
					.stream()
					.map(

							element -> {

								element.getAnnotation(Homie.class)

							}

					)


		} catch (Exception e) {
			error(e);
			throw new RuntimeException(e);
		}

	}

	private Map<String, Object> loadApplicationProperties() {
		try {
			Map<String, Object> applicationProperties = loadApplicationProperties("application.yaml");
			String activeProfilesStr = loadActifeProfiles(applicationProperties);
			applicationProperties = loadProfileApplicationProperties(applicationProperties, activeProfilesStr);
			return applicationProperties;
		} catch (IOException e) {
			error("Exception occurred: {}", e);
			throw new RuntimeException(e);
		}
	}

	private Map<String, Object> loadProfileApplicationProperties(Map<String, Object> parentProperties,
															   String activeProfilesStr) {
		Map<String, Object> applicationProperties = new HashMap<>(parentProperties);

		List<String> activeProfiles = List.of(activeProfilesStr);

		if (activeProfilesStr.contains(",")) {
			String[] activeProfilesSplit = activeProfilesStr.split(",");
			activeProfiles = Arrays.stream(activeProfilesSplit).filter(profile -> !profile.isBlank())
					.collect(Collectors.toList());
		}
		for (String profile: activeProfiles) {
			Map<String, Object> profileProperties = loadProfileProperties(profile);

			// TODO: can be issues with ordering
			// if two profiles contain same properties, how overriding of properties should be
			for (Map.Entry<String, Object> entry: profileProperties.entrySet()) {
				applicationProperties.put(entry.getKey(), entry.getValue());
			}
		}
		return applicationProperties;
	}

	private Map<String, Object> loadProfileProperties(String profile) {
		String filePropertiesPath = "application-" + profile + ".yaml";
		return loadApplicationPropertiesIfExist(filePropertiesPath);
	}

	private Map<String, Object> loadApplicationPropertiesIfExist(String filePropertiesPath) {
		try {
			return loadApplicationProperties(filePropertiesPath);
		} catch (IOException e) {
			error(e);
		}
		return Collections.emptyMap();
	}

	private String loadActifeProfiles(Map<String, Object> applicationProperties) {
		try {
			if (applicationProperties.containsKey("spring")) {
				Map<String, Object> springProperties = (Map<String, Object>) applicationProperties.get("spring");
				if (springProperties.containsKey("profiles")) {
					Map<String, Object> profilesProperties = (Map<String, Object>) springProperties.get("profiles");
					if (profilesProperties.containsKey("active")) {
						return (String) profilesProperties.get("active");
					}
				}
			}
		} catch (ClassCastException e) {
			error(e);
		}
		return "";
	}

	private Map<String, Object> loadApplicationProperties(String pathToPropertiesFile) throws IOException {
		Filer filer = this.processingEnv.getFiler();
		FileObject applicationYaml = filer.getResource(StandardLocation.CLASS_OUTPUT, "", pathToPropertiesFile);
		return load(applicationYaml);
	}

	public Map<String, Object> load(FileObject fileObject) {
		try (InputStream stream = fileObject.openInputStream()) {
			return yamlLoader.load(stream);
		} catch (IOException e) {
			error("While loading {} exception occurred: {}", fileObject.getName(), e);
			throw new RuntimeException(e);
		}
	}

	private void error(String msg, Object... args) {
		this.processingEnv
				.getMessager()
				.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
	}


	private void error(Exception e) {
		this.processingEnv
				.getMessager()
				.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
	}

}
