/*
 * Author : AdNovum Informatik AG
 */

package cd.go.plugin.config.yaml.transforms;

import static cd.go.plugin.config.yaml.JSONUtils.getKeyAsString;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.JSON_MATERIAL_AUTO_UPDATE_FIELD;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.JSON_MATERIAL_TYPE_FIELD;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.YAML_MATERIAL_AUTO_UPDATE_FIELD;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.YAML_SHORT_KEYWORD_GIT;
import static cd.go.plugin.config.yaml.transforms.MaterialTransform.YAML_SHORT_KEYWORD_SCM_ID;
import static cd.go.plugin.config.yaml.transforms.PipelineTransform.JSON_PIPELINE_MATERIALS_FIELD;
import static cd.go.plugin.config.yaml.transforms.PipelineTransform.JSON_PIPELINE_STAGES_FIELD;
import static cd.go.plugin.config.yaml.transforms.PipelineTransform.YAML_PIPELINE_MATERIALS_FIELD;
import static cd.go.plugin.config.yaml.transforms.PipelineTransform.YAML_PIPELINE_STAGES_FIELD;
import static cd.go.plugin.config.yaml.transforms.StageTransform.JSON_STAGE_APPROVAL_FIELD;
import static cd.go.plugin.config.yaml.transforms.StageTransform.JSON_STAGE_APPROVAL_TYPE_FIELD;
import static cd.go.plugin.config.yaml.transforms.StageTransform.YAML_STAGE_APPROVAL_TYPE_FIELD;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import cd.go.plugin.config.yaml.JSONUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DefaultOverrides {

	private static final ThreadLocal<DefaultOverrides> SINGLETON = new ThreadLocal<>();

	// null = not set, don't use a default
	// true = set auto_update to true by default
	// false = set auto_update to false by default
	private Boolean defaultGitAutoUpdate = null;

	private boolean useApprovalManualForPRs = false;

	private String prMaterialIdPattern = null;

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

	public static boolean useApprovalManualForPRs() {
		return getInstance().useApprovalManualForPRs;
	}

	public static void setUseApprovalManualForPRs(boolean useApprovalManualForPRs) {
		getInstance().useApprovalManualForPRs = useApprovalManualForPRs;
	}

	public static void setPrMaterialIdPattern(String prMaterialIdPattern) {
		getInstance().prMaterialIdPattern = prMaterialIdPattern;
	}

	public static String getPrMaterialIdPattern() {
		return getInstance().prMaterialIdPattern;
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

	public static void addApprovalManualForPullRequests(JsonObject pipeline) {
		if (!isApprovalManualForPRsFullyConfigured()) {
			return;
		}

		JsonArray materials = pipeline.getAsJsonArray(JSON_PIPELINE_MATERIALS_FIELD);
		if (materials == null) {
			return;
		}

		boolean hasPRMaterials = JSONUtils.stream(materials).anyMatch(DefaultOverrides::isPRMaterial);
		if (!hasPRMaterials) {
			return;
		}

		JsonArray stages = pipeline.getAsJsonArray(JSON_PIPELINE_STAGES_FIELD);
		if (stages == null || stages.size() == 0) {
			return;
		}
		JsonObject firstStage = stages.get(0).getAsJsonObject();
		if (firstStage.has("approval")) {
			return;
		}
		JsonObject approvalJson = new JsonObject();
		approvalJson.addProperty(JSON_STAGE_APPROVAL_TYPE_FIELD, "manual");
		firstStage.add(JSON_STAGE_APPROVAL_FIELD, approvalJson);
	}

	public static void addApprovalManualForPullRequestsInverse(Map<String, Object> pipeline) {
		if (!isApprovalManualForPRsFullyConfigured()) {
			return;
		}

		Map<String, Map<String, Object>> materials =
				(Map<String, Map<String, Object>>) pipeline.get(YAML_PIPELINE_MATERIALS_FIELD);
		if (materials == null) {
			return;
		}

		boolean hasPRMaterials = materials.values().stream().anyMatch(DefaultOverrides::isPRMaterial);
		if (!hasPRMaterials) {
			return;
		}

		List<Map<String, Object>> stages = (List<Map<String, Object>>) pipeline.get(YAML_PIPELINE_STAGES_FIELD);
		if (stages == null || stages.isEmpty()) {
			return;
		}
		Map<String, Object> firstStage = (Map<String, Object>) stages.get(0).values().iterator().next();

		Map<String, Object> approval = (Map<String, Object>) firstStage.get("approval");
		if (approval != null && Objects.equals(approval.get(YAML_STAGE_APPROVAL_TYPE_FIELD), "manual")) {
			firstStage.remove("approval");
		}
	}

	private static boolean isApprovalManualForPRsFullyConfigured() {
		return useApprovalManualForPRs() && getPrMaterialIdPattern() != null && !getPrMaterialIdPattern().isBlank();
	}

	private static boolean isPRMaterial(JsonElement material) {
		if (!material.isJsonObject() || !Objects.equals(getKeyAsString(material, JSON_MATERIAL_TYPE_FIELD), "plugin")) {
			return false;
		}

		String scmId = getKeyAsString(material, "scm_id");
		return scmId != null && Pattern.matches(getPrMaterialIdPattern(), scmId);
	}

	private static boolean isPRMaterial(Map<String, Object> material) {
		String scmId = (String) material.get(YAML_SHORT_KEYWORD_SCM_ID);
		return scmId != null && Pattern.matches(getPrMaterialIdPattern(), scmId);
	}
}
