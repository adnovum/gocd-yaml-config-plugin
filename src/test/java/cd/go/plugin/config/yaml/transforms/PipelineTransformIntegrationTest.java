package cd.go.plugin.config.yaml.transforms;

import static cd.go.plugin.config.yaml.TestUtils.assertYamlEquivalent;
import static cd.go.plugin.config.yaml.TestUtils.loadString;
import static cd.go.plugin.config.yaml.TestUtils.readJsonGson;
import static cd.go.plugin.config.yaml.TestUtils.readJsonObject;
import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import cd.go.plugin.config.yaml.JsonObjectMatcher;
import cd.go.plugin.config.yaml.YamlUtils;
import com.google.gson.JsonObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PipelineTransformIntegrationTest {

    private PipelineTransform parser;

    @Before
    public void setUp() {
        EnvironmentVariablesTransform environmentTransform = new EnvironmentVariablesTransform();

        parser = new PipelineTransform(
                new MaterialTransform(),
                new StageTransform(environmentTransform, new JobTransform(environmentTransform, new TaskTransform())),
                environmentTransform,
                new ParameterTransform());
    }

    @After
    public void cleanUp() {
        DefaultOverrides.setUseApprovalManualForPRs(false);
        DefaultOverrides.setPrMaterialIdPattern(null);
    }

    @Test
    public void shouldAddApprovalManualToPRPipelines() throws IOException {
        DefaultOverrides.setUseApprovalManualForPRs(true);
        DefaultOverrides.setPrMaterialIdPattern("^.*\\.pr$");

        testTransform("pullrequest.pipe");
    }

    @Test
    public void shouldAddApprovalManualToPRPipelines_Inverse() throws IOException {
        DefaultOverrides.setUseApprovalManualForPRs(true);
        DefaultOverrides.setPrMaterialIdPattern("^.*\\.pr$");

        testInverseTransform("pullrequest.pipe");
    }

    @Test
    public void shouldNotAddApprovalManualIfOtherSCM() throws IOException {
        DefaultOverrides.setUseApprovalManualForPRs(true);
        DefaultOverrides.setPrMaterialIdPattern("not-matching-pattern");

        testTransform("pullrequest.pipe", "pullrequest-no-approval.pipe");
    }

    @Test
    public void shouldNotAddApprovalManualIfOtherSCM_Inverse() throws IOException {
        DefaultOverrides.setUseApprovalManualForPRs(true);
        DefaultOverrides.setPrMaterialIdPattern("not-matching-pattern");

        testInverseTransform("pullrequest-no-approval.pipe", "pullrequest.pipe");
    }

    @Test
    public void shouldNotAddApprovalManualIfExplicitApproval() throws IOException {
        DefaultOverrides.setUseApprovalManualForPRs(true);
        DefaultOverrides.setPrMaterialIdPattern("^.*\\.pr$");

        testTransform("explicit_approval.pipe");
    }

    @Test
    public void shouldNotAddApprovalManualIfExplicitApproval_Inverse() throws IOException {
        DefaultOverrides.setUseApprovalManualForPRs(true);
        DefaultOverrides.setPrMaterialIdPattern("^.*\\.pr$");

        testInverseTransform("explicit_approval.pipe");
    }

    private void testTransform(String caseFile) throws IOException {
        testTransform(caseFile, caseFile);
    }

    private void testInverseTransform(String caseFile) throws IOException {
        testInverseTransform(caseFile, caseFile);
    }

    private void testTransform(String caseFile, String expectedFile) throws IOException {
        JsonObject expectedObject = (JsonObject) readJsonObject("parts/" + expectedFile + ".json");
        JsonObject jsonObject = parser.transform(readYamlObject("parts/" + caseFile + ".yaml"), 9);
        assertThat(jsonObject, is(new JsonObjectMatcher(expectedObject)));
    }

    private void testInverseTransform(String caseFile, String expectedFile) throws IOException {
        String expectedObject = loadString("parts/" + expectedFile + ".yaml");
        Map<String, Object> actual = parser.inverseTransform(readJsonGson("parts/" + caseFile + ".json"));
        assertYamlEquivalent(expectedObject, YamlUtils.dump(actual));
    }
}
