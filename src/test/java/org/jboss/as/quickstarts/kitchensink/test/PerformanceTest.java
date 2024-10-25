package org.jboss.as.quickstarts.kitchensink.test;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceTest {

    private static final String TEST_PLAN_PATH = "/kitchensink-test-plan.jmx";
    private static final String JMETER_PROPERTIES_PATH = "/jmeter.properties";
    private static final String RESULTS_PATH = "target/jmeter-results/";

    @BeforeAll
    public static void setupJMeter() throws IOException {
        // Set JMeter home directory (assuming JMeter is installed in the project)
        File jmeterHome = new File("jmeter");
        String jmeterProperties = jmeterHome.getAbsolutePath() + JMETER_PROPERTIES_PATH;

        JMeterUtils.loadJMeterProperties(jmeterProperties);
        JMeterUtils.setJMeterHome(jmeterHome.getAbsolutePath());
        JMeterUtils.initLocale();

        // Create results directory if it doesn't exist
        new File(RESULTS_PATH).mkdirs();
    }

    @Test
    public void testConcurrentUserRegistrations() throws Exception {
        runJMeterTest("Concurrent User Registrations");
    }

    @Test
    public void testBulkDataRetrieval() throws Exception {
        runJMeterTest("Bulk Data Retrieval");
    }

    @Test
    public void testSustainedLoad() throws Exception {
        runJMeterTest("Sustained Load");
    }

    private void runJMeterTest(String testName) throws Exception {
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // Load test plan
        HashTree testPlanTree = SaveService.loadTree(getClass().getResource(TEST_PLAN_PATH));

        // Configure the results file
        String resultFile = RESULTS_PATH + testName.replaceAll("\\s+", "_") + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".jtl";
        Summariser summer = new Summariser(testName);
        ResultCollector resultCollector = new ResultCollector(summer);
        resultCollector.setFilename(resultFile);
        testPlanTree.add(testPlanTree.getArray()[0], resultCollector);

        // Run the test
        jmeter.configure(testPlanTree);
        jmeter.run();

        // Analyze results
        analyzeResults(summer);
    }

    private void analyzeResults(Summariser summer) {
        // Example assertions - adjust these based on your specific requirements
        assertTrue(summer.getErrorCount() == 0, "There should be no errors");
        assertTrue(summer.getAverage() < 1000, "Average response time should be less than 1 second");
        assertTrue(summer.getErrorPercentage() < 1.0, "Error percentage should be less than 1%");
    }
}