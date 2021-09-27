/*
 * Author : AdNovum Informatik AG
 */

package cd.go.plugin.config.yaml.transforms;

import static cd.go.plugin.config.yaml.transforms.MaterialTransform.JSON_MATERIAL_AUTO_UPDATE_FIELD;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.JSON_MATERIAL_TYPE_FIELD;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.YAML_MATERIAL_AUTO_UPDATE_FIELD;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.YAML_SHORT_KEYWORD_GIT;

import java.util.Map;

import com.google.gson.JsonObject;

public class DefaultOverrides {

	private static final ThreadLocal<DefaultOverrides> SINGLETON = new ThreadLocal<>();

	private Boolean defaultGitAutoUpdate = null;

	public static DefaultOverrides getInstance() {
		if (SINGLETON.get() == null) {
			SINGLETON.set(new DefaultOverrides());
		}
		return SINGLETON.get();
	}

	public static void setDefaultGitAutoUpdate(Boolean autoUpdate) {
		getInstance().defaultGitAutoUpdate = autoUpdate;
	}

	public static Boolean getDefaultGitAutoUpdate() {
		return getInstance().defaultGitAutoUpdate;
	}

	public static void overrideGitMaterialAutoUpdate(JsonObject jsonOutput) {
		if (getDefaultGitAutoUpdate() != null &&
				jsonOutput.has(JSON_MATERIAL_TYPE_FIELD) &&
				jsonOutput.get(JSON_MATERIAL_TYPE_FIELD).getAsString().equals("git") &&
				!jsonOutput.has(JSON_MATERIAL_AUTO_UPDATE_FIELD)) {
			jsonOutput.addProperty(JSON_MATERIAL_AUTO_UPDATE_FIELD, DefaultOverrides.getDefaultGitAutoUpdate());
		}
	}

	public static void overrideGitMaterialAutoUpdateInverse(Map<String, Object> yamlOutput) {
		if (getDefaultGitAutoUpdate() != null && yamlOutput.containsKey(YAML_SHORT_KEYWORD_GIT)) {
			Boolean gitAutoUpdate = (Boolean) yamlOutput.get(YAML_MATERIAL_AUTO_UPDATE_FIELD);
			if (getDefaultGitAutoUpdate().equals(gitAutoUpdate)) {
				yamlOutput.remove(YAML_MATERIAL_AUTO_UPDATE_FIELD);
			}
		}
	}
}
