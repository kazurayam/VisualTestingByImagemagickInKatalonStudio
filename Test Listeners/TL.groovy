import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.kstestresults.Helpers
import com.kazurayam.kstestresults.TestResults
import com.kazurayam.kstestresults.TestResultsFactory

import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext

import internal.GlobalVariable as GlobalVariable

class TL {
	
	/**
	 * Executes before every test suite starts.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {
		Path resultsDir = Paths.get(RunConfiguration.getProjectDir()).resolve('Results')
		Helpers.ensureDirs(resultsDir)
		TestResults testResults = 
			TestResultsFactory.createInstance(resultsDir, testSuiteContext.getTestSuiteId())
		GlobalVariable.TEST_RESULTS = testResults
		assert GlobalVariable.TEST_RESULTS != null
	}

	/**
	 * Executes after every test suite ends.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		TestResults testResults = (TestResults)GlobalVariable.TEST_RESULTS
		testResults.report()
	}
	
	/**
	 * Executes before every test case starts.
	 * @param testCaseContext related information of the executed test case.
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		GlobalVariable.CURRENT_TESTCASE_ID = testCaseContext.getTestCaseId()
	}

	/**
	 * Executes after every test case ends.
	 * @param testCaseContext related information of the executed test case.
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		TestResults testResults = (TestResults)GlobalVariable.TEST_RESULTS
		if (testResults != null) {
			def testCaseId = testCaseContext.getTestCaseId()
			def testCaseStatus = testCaseContext.getTestCaseStatus()
			testResults.setTcStatus(testCaseId, testCaseStatus)
		}
	}

}