package org.wpstarters.repositoriesprocessor;

import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultJpaRepositoriesProcessor extends AbstractProcessor {

    private Yaml yamlLoader;
	private Elements elementUtils;
	private Messager messager;

	@Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.yamlLoader = new Yaml();
		this.processingEnv = processingEnv;
		this.elementUtils = processingEnv.getElementUtils();
		this.messager = processingEnv.getMessager();
    }

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.of(StartersJpaRepositories.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

	    // retrieving active application properties

        Map<String, Object> applicationProperties = loadApplicationProperties();

        //

        Set<? extends Element> elements =  roundEnv.getElementsAnnotatedWith(StartersJpaRepositories.class);
		if (elements.isEmpty()) {
        	return false;
		} else if (elements.size() != 1) {
		    messager.printMessage(
		            Diagnostic.Kind.ERROR, "@DefaultJpaRepositories annotation must be used only once"
            );
        }

		Element annotatedElement = elements.stream().findAny().get();
		PackageElement packageElement = elementUtils.getPackageOf(annotatedElement);
		



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
        for (String profile : activeProfiles) {
            Map<String, Object> profileProperties = loadProfileProperties(profile);

            // TODO: can be issues with ordering
            // if two profiles contain same properties, how overriding of properties should be
            for (Map.Entry<String, Object> entry : profileProperties.entrySet()) {
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
