package com.hp.itsm.reporting;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestReport implements IReporter, ISuiteListener {

    private static final Logger LOGGER = LogManager.getLogger(TestReport.class);
    private static final String COLOR_BLACK = "black";
    private static final String ERROR_BGCOLOR = "#FFB5B5";
    private static final String ERROR_FGCOLOR = COLOR_BLACK;
    private static final String PASS_BGCOLOR = "#AAFD8E";
    private static final String PASS_FGCOLOR = COLOR_BLACK;
    private static final String SKIP_BGCOLOR = "#FFF284";
    private static final String SKIP_FGCOLOR = COLOR_BLACK;
    private static final String HEADER_BGCOLOR = "#E2E2E2";
    private static final String HEADER_FGCOLOR = COLOR_BLACK;
    private static final String TABLE_BORDER_COLOR = COLOR_BLACK;

    private static final int MAX_ERROR_LENGTH = 150;

    private List<ISuite> testSuites;
    private StringBuilder htmlBuilder;
    private File testOutputDirectory;
    private int stackTraceCount = 0;

    private static long suiteStartTimeInMilliseconds;

    @Override
    public void onStart(ISuite suite) {
        suiteStartTimeInMilliseconds = System.currentTimeMillis();
    }

    @Override
    public void onFinish(ISuite suite) {
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

        String reportName = new File(Paths.get("").toAbsolutePath().toString()).getName();

        this.testSuites = suites;
        testOutputDirectory = new File(System.getProperty("user.dir"));
        File report = new File(testOutputDirectory, reportName + (!reportName.equals("") ? "-" : "") + "testReport.html");
        if (report.exists()) {
            LOGGER.info("Deleting the old existing report...");
            report.delete();
        }

        try {
            report.createNewFile();
            createReport(report);
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | ParseException e) {
            LOGGER.error("Encountered error while saving triage report.", e);
        }
    }

    private void createReport(File file) throws InvalidKeyException, NoSuchAlgorithmException, ParseException, IOException {
        LOGGER.info("Saving report to \"" + file.getAbsolutePath() + "\"...");

        htmlBuilder = new StringBuilder();

        generateDocumentHead();
        generateSeleniumTable(getTestContext());
        generateGraph();

        htmlBuilder.append(getTestLogLink());

        for (ISuite suite : testSuites) {
            Map<String, ISuiteResult> suiteResult = suite.getResults();
            for (ISuiteResult result : suiteResult.values()) {
                generateFailureTable(result.getTestContext().getFailedConfigurations(), "Failed configurations", "error");
                generateFailureTable(result.getTestContext().getFailedTests(), "Failed tests", "error");
                generateFailureTable(result.getTestContext().getSkippedTests(), "Skipped tests", "skip");
                generateWarnings(result.getTestContext().getFailedTests(), result.getTestContext().getPassedTests(), result.getTestContext().getSkippedTests());
                generateFilteredTests(result.getTestContext());
                generatePassingTable(result.getTestContext().getPassedTests(), "Passing tests");
            }

        }
        generateDocumentFooter();

        PrintWriter writer = new PrintWriter(file);
        writer.write(htmlBuilder.toString());
        writer.close();
    }

    private void generateGraph() {
        double passed = 0;
        double failed = 0;
        double skipped = 0;

        for (ISuite suite : this.testSuites) {
            Map<String, ISuiteResult> suiteResults = suite.getResults();
            for (ISuiteResult sResult : suiteResults.values()) {
                ITestContext context = sResult.getTestContext();
                passed += context.getPassedTests().size();
                failed += context.getFailedTests().size();
                skipped += context.getSkippedTests().size();
            }
        }

        double total = passed + failed + skipped;
        double failPercent = (failed * 100.0) / total;
        double passPercent = (passed * 100.0) / total;
        double skipPercent = (skipped * 100.0) / total;

        Slice s1 = Slice.newSlice(
                (int) passPercent,
                Color.GREEN,
                String.format("%s (%.2f%%)", (int) passed, passPercent),
                "Passed");
        Slice s2 = Slice.newSlice(
                (int) failPercent,
                Color.RED,
                String.format("%s (%.2f%%)", (int) failed, failPercent),
                "Failed");
        Slice s3 = Slice.newSlice(
                (int) skipPercent,
                Color.GOLD,
                String.format("%s (%.2f%%)", (int) skipped, skipPercent),
                "Skipped");

        PieChart chart = GCharts.newPieChart(s1, s2, s3);
        chart.setTitle("Test results", Color.BLACK, 16);
        chart.setSize(400, 150);
        chart.setThreeD(true);
        String url = chart.toURLString();

        htmlBuilder.append("<div><img src='" + url + "'></div><br>");
    }

    /**
     * Writes the html header and defines the css styling.
     *
     * @throws ParseException
     */
    private void generateDocumentHead() throws ParseException {
        htmlBuilder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        htmlBuilder.append("<html>");
        htmlBuilder.append("<body>");
        htmlBuilder.append("<head>");

        htmlBuilder.append("<style type=\"text/css\">");
        htmlBuilder.append("table { border-collapse:collapse; table-layout:fixed;}");
        htmlBuilder.append(String.format("table { border: 2px solid %s; }", TABLE_BORDER_COLOR));
        htmlBuilder.append(String.format("th, td { border: 1px solid %s; text-align: left; padding-right: 30px; }", TABLE_BORDER_COLOR));

        htmlBuilder.append(".summaryTable { padding-left:10px; padding-right:10px; }");
        htmlBuilder.append(String.format("th { color:%s; background-color:%s;}", HEADER_FGCOLOR, HEADER_BGCOLOR));
        htmlBuilder.append("th.stack { text-align: center; }");
        htmlBuilder.append(String.format("tr.error { color:%s; background:%s; }", ERROR_FGCOLOR, ERROR_BGCOLOR));
        htmlBuilder.append(String.format("tr.skip { color:%s; background:%s; }", SKIP_FGCOLOR, SKIP_BGCOLOR));
        htmlBuilder.append(String.format("tr.pass { color:%s; background:%s; }", PASS_FGCOLOR, PASS_BGCOLOR));
        htmlBuilder.append(String.format("tr.healthError { color:%s; background:%s; }", ERROR_FGCOLOR, ERROR_BGCOLOR));
        htmlBuilder.append(String.format("tr.healthPass { color:%s; background:%s; }", PASS_FGCOLOR, PASS_BGCOLOR));
        htmlBuilder.append("table td { word-wrap:break-word; font-size:14px; }");
        htmlBuilder.append("pre { white-space: pre-wrap; }");
        htmlBuilder.append(".stack-trace { display: none; } ");
        htmlBuilder.append(".errorData { font-size:12px; } ");
        htmlBuilder.append("hr { width:1600px; background-color:black; }");
        htmlBuilder.append("</style>");

        String javascript = "<script type=\"text/javascript\">function toggle_visibility(id) { var e = document.getElementById(id); if(e.style.display == 'block') e.style.display = 'none'; else e.style.display = 'block'; }</script>";
        htmlBuilder.append(javascript);

        htmlBuilder.append("</head>");

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        htmlBuilder.append(String.format("<h1>Test Report [%s]</h1>", formatter.format(today)));
    }

    private void generateDocumentFooter() {
        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");
    }

    private void generatePassingTable(IResultMap map, String tableTitle) {
        if (map.size() == 0) {
            return;
        }
        htmlBuilder.append("<details>");
        htmlBuilder.append("<summary>" + tableTitle + "</summary>");

        htmlBuilder.append("<table>");
        htmlBuilder.append(String.format("<tr><th>%s</th><th>%s</th><th>%s</th></tr>",
                "test name",
                "groups",
                "duration"));

        for (ITestResult result : getSortedMethods(map, new PassedTestSorter<ITestResult>())) {
            ITestNGMethod testMethod = result.getMethod();
            String groupsString = Arrays.toString(testMethod.getGroups()).replace("[", "").replace("]", "");
            htmlBuilder.append("<tr class=pass>");
            htmlBuilder.append(String.format("<td>%s<br><b>%s</b></td>",
                    testMethod.getTestClass().getName(),
                    getLogLink(testMethod, result)));
            htmlBuilder.append(String.format("<td>%s</td>", groupsString));
            htmlBuilder.append(String.format("<td>%s</td>", getFormattedTimeFromMilliseconds(result.getEndMillis() - result.getStartMillis())));
            htmlBuilder.append("</tr>");
        }

        htmlBuilder.append("</table>");
        htmlBuilder.append("</details><br>");
    }

    private void generateWarnings(IResultMap... map) {
        BiMap<String, Integer> test = HashBiMap.create();
        List<Pair<String, String>> warnings = new ArrayList<Pair<String, String>>();

        for (IResultMap resultType : map) {
            for (ITestResult result : resultType.getAllResults()) {
                addRallyReportingWarning(result, warnings);
                addTrackingWarning(result, warnings);
                addDuplicateTrackingWarning(result, test, warnings);
            }
        }

        if (warnings.size() > 0) {
            htmlBuilder.append("<details>");
            htmlBuilder.append("<summary>Warnings</summary>");

            htmlBuilder.append("<table>");
            htmlBuilder.append(String.format("<tr><th>%s</th><th>%s</th></tr>",
                    "test name",
                    "warning"));

            for (Pair<String, String> p : warnings) {
                htmlBuilder.append(String.format("<tr><td>%s</td><td>%s</td></tr>", p.getLeft(), p.getRight()));
            }

            htmlBuilder.append("</table>");
            htmlBuilder.append("</details><br>");
        }
    }

    private void generateFilteredTests(ITestContext context) {
        StringBuilder tempHtml = new StringBuilder();

        tempHtml.append("<details>");
        tempHtml.append("<summary>Filtered tests</summary>");

        tempHtml.append("<table>");
        tempHtml.append(String.format("<tr><th>%s</th><th>%s</th></tr>",
                "test name",
                "filter"));

        boolean foundFilteredTest = false;
        for (ITestNGMethod method : context.getAllTestMethods()) {
            String key = String.format("testFilter.%s.%s",
                    method.getTestClass().getName(),
                    method.getMethodName());

            String filterReason = (String) context.getAttribute(key);

            if (filterReason != null) {
                String style = filterReason.contains("Test Filter Arg.") ? " class='filtered'" : "";
                tempHtml.append(String.format("<tr%s><td>%s.%s</td><td>%s</td></tr>",
                        style,
                        method.getTestClass().getName(),
                        method.getMethodName(),
                        filterReason));
                foundFilteredTest = true;
            }
        }

        tempHtml.append("</table>");
        tempHtml.append("</details><br>");

        if (foundFilteredTest) {
            htmlBuilder.append(tempHtml.toString());
        }
    }

    private void addDuplicateTrackingWarning(ITestResult result, final BiMap<String, Integer> test, final List<Pair<String, String>> warnings) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        Tracking annotation = method.getAnnotation(Tracking.class);
        if (annotation != null) {
            String fqTestName = String.format("%s.%s", result.getMethod().getTestClass().getName(), result.getMethod().getMethodName());
            if (!test.containsValue(annotation.TC())) {
                test.put(result.getMethod().getTestClass().getName() + "." + method.getName(), annotation.TC());
            } else if (!fqTestName.equals(test.inverse().get(annotation.TC()))) {
                warnings.add(Pair.of(fqTestName, String.format("Annotates a previously used TC [%s] reported by [%s].",
                        annotation.TC(),
                        test.inverse().get(annotation.TC()))));
            }
        }
    }

    private void addTrackingWarning(ITestResult result, final List<Pair<String, String>> warnings) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        Tracking annotation = method.getAnnotation(Tracking.class);
        boolean duplicate = false;
        if (annotation == null) {
            String fqTestName = String.format("%s.%s", result.getMethod().getTestClass().getName(), result.getMethod().getMethodName());
            for (Pair<String, String> p : warnings) {
                if (p.getKey().equals(fqTestName)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate && !isIntegrationTest(result.getMethod())) {
                warnings.add(Pair.of(fqTestName, "No TC associated with the test method."));
            }
        }
    }

    private void addRallyReportingWarning(ITestResult result, final List<Pair<String, String>> warnings) {
        String key = String.format("testReporting.%s.%s",
                result.getMethod().getTestClass().getName(),
                result.getMethod().getMethodName());

        Object warn = result.getTestContext().getAttribute(key);
        if (warn != null) {
            warnings.add(Pair.of(String.format("%s.%s",
                    result.getMethod().getTestClass().getName(),
                    result.getMethod().getMethodName()),
                    (String) warn));
        }
    }

    private boolean isIntegrationTest(ITestNGMethod test) {
        boolean result = false;
        for (String group : test.getGroups()) {
            if (group.toLowerCase().endsWith("integration")) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void generateFailureTable(IResultMap map, String tableTitle, String cssClass) throws InvalidKeyException, NoSuchAlgorithmException {

        if (map.size() == 0) {
            return;
        }
        htmlBuilder.append("<details open>");
        htmlBuilder.append("<summary>" + tableTitle + "</summary>");
        htmlBuilder.append("<table>");
        htmlBuilder.append(String.format("<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr>",
                "Test Name",
                "Error",
                "Groups",
                "Duration",
                "Test Case ID"));

        for (ITestResult result : getSortedMethods(map, new FailedTestSorter<ITestResult>())) {
            ITestNGMethod testMethod = result.getMethod();
            if (result.getMethod().getMethodName().equals(testMethod.getMethodName())) {
                Throwable cause = result.getThrowable();
                String errorMessage = "";
                String groupsString = Arrays.toString(testMethod.getGroups()).replace("[", "").replace("]", "");
                String dependsOnString = Arrays.toString(testMethod.getGroupsDependedUpon()).replace("[", "").replace("]", "");
                if (!dependsOnString.isEmpty()) {
                    groupsString += "<b><br>dependsOn:</b> " + dependsOnString;
                }

                if (cause != null) {
                    errorMessage = cause.getClass().toString() + ":" + cause.getMessage();

                    if (errorMessage.length() > MAX_ERROR_LENGTH) {
                        errorMessage = errorMessage.substring(0, MAX_ERROR_LENGTH) + "...";
                    }
                }

                htmlBuilder.append(String.format("<tr class=%s>", cssClass));
                htmlBuilder.append(String.format("<td>%s%s<br><b>%s</b></td>",
                        getConfigAnnotation(testMethod),
                        testMethod.getTestClass().getName(),
                        getLogLink(testMethod, result)));
                htmlBuilder.append(String.format("<td><div class='errorData'><pre><xmp>%s</xmp>%s</pre></div></td>",
                        errorMessage,
                        getStackTraceHtml(cause)));
                htmlBuilder.append(String.format("<td>%s</td>", groupsString));
                htmlBuilder.append(String.format("<td>%s</td>", getFormattedTimeFromMilliseconds(result.getEndMillis() - result.getStartMillis())));
                //Got to provide the TestRails search link later
                htmlBuilder.append(String.format("<td>%s</td>", getTestCase(testMethod)));
                htmlBuilder.append("</tr>");
            }
        }
        htmlBuilder.append("</table>");
        htmlBuilder.append("</details><br>");
    }


    private String getTestCase(ITestNGMethod testMethod) {
        String result = "";
        Tracking annotation = testMethod.getConstructorOrMethod().getMethod().getAnnotation(Tracking.class);
        if (annotation != null) {
            result = "TC" + annotation.TC();
        }
        return result;
    }

    private String getConfigAnnotation(ITestNGMethod method) {
        String annotation = "";
        if (method.isBeforeTestConfiguration()) {
            annotation = "@BeforeTest";
        } else if (method.isBeforeMethodConfiguration()) {
            annotation = "@BeforeMethod";
        } else if (method.isBeforeClassConfiguration()) {
            annotation = "@BeforeClass";
        } else if (method.isBeforeGroupsConfiguration()) {
            annotation = "@BeforeGroups";
        } else if (method.isBeforeSuiteConfiguration()) {
            annotation = "@BeforeSuite";
        } else if (method.isAfterTestConfiguration()) {
            annotation = "@AfterTest";
        } else if (method.isAfterMethodConfiguration()) {
            annotation = "@AfterMethod";
        } else if (method.isAfterClassConfiguration()) {
            annotation = "@AfterClass";
        } else if (method.isAfterGroupsConfiguration()) {
            annotation = "@AfterGroups";
        } else if (method.isAfterSuiteConfiguration()) {
            annotation = "@AfterSuite";
        }

        if (!annotation.isEmpty()) {
            annotation += "<br>";
        }

        return annotation;
    }

    private String getStackTraceHtml(Throwable cause) {
        String html = "";
        if (cause != null) {
            StringWriter trace = new StringWriter();
            cause.printStackTrace(new PrintWriter(trace));

            stackTraceCount++;
            html = String.format("<br><a href=\"#\" onClick=\"toggle_visibility('%s'); return false;\">Stack trace</a>",
                    "stacktrace" + Integer.toString(stackTraceCount));
            html += String.format("<div class='stack-trace' id='%s'><xmp>%s</xmp></div>", "stacktrace" + Integer.toString(stackTraceCount),
                    ExceptionUtils.getStackTrace(cause));
        }
        return html;
    }

    private void generateSeleniumTable(ITestContext testContext) throws IOException {
        String reportName = new File(Paths.get("").toAbsolutePath().toString()).getName();
        String buildLink = "None";
        String platformOS = System.getProperty("os.name");
        String deviceName = System.getProperty("selenium.browserName");
        htmlBuilder.append("<div><table>");

        htmlBuilder.append("<tr>");
        htmlBuilder.append(String.format("<th class=summaryTable>%s</th><th class=summaryTable>%s</th><th class=summaryTable>%s</th>",
                "OS",
                "Browser",
                "Duration"));
        htmlBuilder.append("</tr>");

        htmlBuilder.append(String.format("<tr><td align=center>%s</td><td align=center>%s</td><td align=center>%s</td></tr>",
                platformOS,
                deviceName,
                getFormattedTimeFromMilliseconds(getElapsedMilliseconds())));

        htmlBuilder.append("</table></div>");
        htmlBuilder.append("</br>");
    }

    private static long getElapsedMilliseconds() {
        return System.currentTimeMillis() - suiteStartTimeInMilliseconds;
    }

    private String getFormattedTimeFromMilliseconds(long milliseconds) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    /**
     * Creates html link to the matching log file if found.
     */
    private String getLogLink(ITestNGMethod testMethod, ITestResult result) {
        String methodName = testMethod.getMethodName();

        String fullTestName = String.format("%s.%s_%s",
                testMethod.getTestClass().getName(),
                methodName,
                result.getAttribute("Invocation"));

        File logFile = new File(testOutputDirectory, "logs/" + fullTestName + ".log");

        Method method = testMethod.getConstructorOrMethod().getMethod();
        String description = null;
        if (method.isAnnotationPresent(org.testng.annotations.Test.class)) {
            description = method.getAnnotation(org.testng.annotations.Test.class).description();
        }
        if (description == null || description.isEmpty()) {
            description = "No description found";
        }

        String paramSuffix = "";
        if (testMethod.getParameterInvocationCount() > 1) {
            Object[] params = result.getParameters();
            paramSuffix = "<details><summary>params</summary>" + Arrays.toString(params) + "</details>";
        }

        String href = logFile.exists() ?
                String.format("<a title='%s' href=\"./logs/%s\">%s %s</a>", description, fullTestName + ".log", methodName, paramSuffix) :
                String.format("<div title='%s'>%s %s</div>", description, methodName, paramSuffix);

        return href;
    }

    private String getTestLogLink() {
        File log = new File(testOutputDirectory, "test.log");
        String link = "";
        if (log.exists()) {
            link = "<a href=\"./test.log\"><font size=2>Full log</font></a><br><br>";
        }
        return link;
    }

    private Collection<ITestResult> getSortedMethods(IResultMap tests, Comparator<ITestResult> comparator) {
        List<ITestResult> sortedList = new ArrayList<ITestResult>();
        sortedList.addAll(tests.getAllResults());
        Collections.sort(sortedList, comparator);
        return sortedList;
    }

    private ITestContext getTestContext() {
        ITestContext context = null;
        for (ISuite suite : testSuites) {
            Map<String, ISuiteResult> suiteResult = suite.getResults();
            for (ISuiteResult result : suiteResult.values()) {
                context = result.getTestContext();
                break;
            }
        }
        return context;
    }

    private class PassedTestSorter<T extends ITestResult> implements Comparator<ITestResult> {

        @Override
        public int compare(ITestResult result1, ITestResult result2) {

            int retVal = result1.getTestClass().getName().compareTo(result2.getTestClass().getName());

            if (retVal == 0) {
                retVal = result1.getMethod().getMethodName().compareTo(result2.getMethod().getMethodName());
            }

            return retVal;
        }
    }

    private class FailedTestSorter<T extends ITestResult> implements Comparator<ITestResult> {

        @Override
        public int compare(ITestResult result1, ITestResult result2) {

            Throwable throwable1 = result1.getThrowable();
            Throwable throwable2 = result2.getThrowable();
            int retVal = 0;
            if (throwable1 != null && throwable2 != null)
            {
                retVal = throwable1.getClass().getName().compareTo(throwable2.getClass().getName());

                if (retVal == 0) {
                    String message1 = result1.getThrowable().getMessage();
                    String message2 = result2.getThrowable().getMessage();
                    if (message1 != null && message2 != null) {
                        retVal = message1.compareTo(message2);
                    }
                    else if (message1 != null) {
                        retVal = -1;
                    }
                    else if (message2 != null) {
                        retVal = 1;
                    }
                }

                if (retVal == 0) {
                    retVal = result1.getTestClass().getName().compareTo(result2.getTestClass().getName());
                }

                if (retVal == 0) {
                    retVal = result1.getMethod().getMethodName().compareTo(result2.getMethod().getMethodName());
                }
            }
            else if (throwable1 != null) {
                retVal = -1;
            }
            else if (throwable2 != null) {
                retVal = 1;
            }

            return retVal;
        }
    }
}
