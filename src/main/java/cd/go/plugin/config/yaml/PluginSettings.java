package cd.go.plugin.config.yaml;

import java.util.Map;

class PluginSettings {
    static final String PLUGIN_SETTINGS_FILE_PATTERN = "file_pattern";
    static final String DEFAULT_FILE_PATTERN = "**/*.gocd.yaml,**/*.gocd.yml";
    static final String DEFAULT_DEFAULT_AUTO_UPDATE = "";

    static final String DEFAULT_USE_APPROVAL_MANUAL_FOR_PRS = "false";

    static final String DEFAULT_PR_MATERIAL_ID_PATTERN = null;

    static final String PLUGIN_SETTINGS_DEFAULT_AUTO_UPDATE = "default_auto_update";

    static final String PLUGIN_SETTINGS_USE_APPROVAL_MANUAL_FOR_PRS = "use_approval_manual_for_prs";
    static final String PLUGIN_SETTINGS_PR_MATERIAL_ID_PATTERN = "pr_material_id_pattern";

    private String filePattern;

    private Boolean defaultGitAutoUpdate;
    private boolean useApprovalManualForPRs;
    private String prMaterialIdPattern;

    PluginSettings() {
    }

    PluginSettings(String filePattern) {
        this.filePattern = filePattern;
    }

    static PluginSettings fromJson(String json) {
        Map<String, String> raw = JSONUtils.fromJSON(json);
        PluginSettings settings = new PluginSettings();

        settings.filePattern = raw.get(PLUGIN_SETTINGS_FILE_PATTERN);

        String defaultAutoUpdateVal = raw.get(PLUGIN_SETTINGS_DEFAULT_AUTO_UPDATE);
        settings.defaultGitAutoUpdate = defaultAutoUpdateVal == null || defaultAutoUpdateVal.isBlank() ? null :
                Boolean.valueOf(defaultAutoUpdateVal);

        settings.useApprovalManualForPRs = Boolean.parseBoolean(raw.get(PLUGIN_SETTINGS_USE_APPROVAL_MANUAL_FOR_PRS));
        settings.prMaterialIdPattern = raw.get(PLUGIN_SETTINGS_PR_MATERIAL_ID_PATTERN);

        return settings;
    }

    String getFilePattern() {
        return filePattern;
    }

    Boolean getDefaultGitAutoUpdate() {
        return defaultGitAutoUpdate;
    }

    boolean useApprovalManualForPRs() {
        return useApprovalManualForPRs;
    }

    String getPrMaterialIdPattern() {
        return prMaterialIdPattern;
    }
}