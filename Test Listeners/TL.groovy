import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.carmina.Helpers
import com.kazurayam.carmina.TestResultsRepository
import com.kazurayam.carmina.TestResultsRepositoryFactory

import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.configuration.RunConfiguration

class TL {
	
	static Path resultsDir = Paths.get(RunConfiguration.getProjectDir()).resolve('Results')
	
	static {
		Helpers.ensureDirs(resultsDir)
	}
	
	/**
	 * Executes before every test suite starts.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {
		//
		GlobalVariable.RESULTS_DIR = resultsDir

		Path reportFolder = Paths.get(RunConfiguration.getReportFolder())
		// for example, reportFolder = C:/Users/username/katalon-workspace/VisualTestingWithKatalonStudio/Reports/TS1/20180618_165141

		//
		TestResultsRepository trr = TestResultsRepositoryFactory.createInstance(resultsDir)
		trr.setCurrentTestSuite(testSuiteContext.getTestSuiteId(), reportFolder.getFileName().toString())
		GlobalVariable.TEST_RESULTS_REPOSITORY = trr
	}
	
	/**
	 * Executes before every test case starts.
	 * @param testCaseContext relate information of the executed test case.
	 */
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		if (GlobalVariable.TEST_RESULTS_REPOSITORY == null) {
			GlobalVariable.TEST_RESULTS_REPOSITORY = TestResultsRepositoryFactory.createInstance(resultsDir)
		}
		GlobalVariable.CURRENT_TESTCASE_ID = testCaseContext.getTestCaseId()
	}

	/**
	 * Executes after every test case ends.
	 * @param testCaseContext related information of the executed test case.
	 */
	@AfterTestCase
	def afterTestCase(TestCaseContext testCaseContext) {
		TestResultsRepository trr = (TestResultsRepository)GlobalVariable.TEST_RESULTS_REPOSITORY
		assert trr != null
		def testCaseId = testCaseContext.getTestCaseId()
		def testCaseStatus = testCaseContext.getTestCaseStatus()
		trr.setTestCaseStatus(testCaseId, testCaseStatus)
	}

	/**
	 * Executes after every test suite ends.
	 * @param testSuiteContext: related information of the executed test suite.
	 */
	@AfterTestSuite
	def afterTestSuite(TestSuiteContext testSuiteContext) {
		
		def reportFolder = RunConfiguration.getReportFolder()
		WebUI.comment("#afterTestSuite reportFolder=${reportFolder}")
		
		TestResultsRepository trr = (TestResultsRepository)GlobalVariable.TEST_RESULTS_REPOSITORY
		assert trr != null
		trr.report()
	}

}