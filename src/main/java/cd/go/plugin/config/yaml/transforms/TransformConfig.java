/*
 * Author : AdNovum Informatik AG
 */

package cd.go.plugin.config.yaml.transforms;

public class TransformConfig {

	private static final ThreadLocal<TransformConfig> SINGLETON = new ThreadLocal<>();

	private Boolean defaultAutoUpdate = null;

	public static TransformConfig getInstance() {
		if (SINGLETON.get() == null) {
			SINGLETON.set(new TransformConfig());
		}
		return SINGLETON.get();
	}

	public static void setDefaultAutoUpdate(Boolean defaultAutoUpdate) {
		getInstance().defaultAutoUpdate = defaultAutoUpdate;
	}

	public static Boolean getDefaultAutoUpdate() {
		return getInstance().defaultAutoUpdate;
	}
}
