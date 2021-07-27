package cd.go.plugin.config.yaml;

import java.util.Map;

class PluginSettings {
    static final String PLUGIN_SETTINGS_FILE_PATTERN = "file_pattern";
    static final String DEFAULT_FILE_PATTERN = "**/*.gocd.yaml,**/*.gocd.yml";
    static final String DEFAULT_DEFAULT_AUTO_UPDATE = "";

    static final String PLUGIN_SETTINGS_DEFAULT_AUTO_UPDATE = "default_auto_update";

    private String filePattern;

    private Boolean defaultAutoUpdate;

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
        settings.defaultAutoUpdate = defaultAutoUpdateVal == null || defaultAutoUpdateVal.isBlank() ? null :
                Boolean.valueOf(defaultAutoUpdateVal);

        return settings;
    }

    String getFilePattern() {
        return filePattern;
    }

    Boolean getDefaultAutoUpdate() {
        return defaultAutoUpdate;
    }
}