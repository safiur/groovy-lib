/***************************************************************************
***** Description :: This Package is used to perform RubyOnRails tasks *****
***** Author      :: Pramod Vishwakarma                                *****
***** Date        :: 08/02/2018                                        *****
***** Revision    :: 1.0                                               *****
****************************************************************************/
package com.symantec.devops.build.java

/**************************************************
***** Function to create the report directory *****
***************************************************/
def createReportDirectory(String REPORT_DIRECTORY)
{
   try {
     wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Creating directory $REPORT_DIRECTORY if not already exist..."
        sh "mkdir -p $REPORT_DIRECTORY"
     }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to create the directory $REPORT_DIRECTORY, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/********************************************
***** Function to get the unused routes *****
*********************************************/
def checkUnusedRoutes()
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Checking present directory files, please wait..."
        sh "ls -ltrh"
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to check present directory files, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/*****************************************************
***** Function to check security vulnerabilities *****
******************************************************/
def scanSecurityVulnerabilities(String BRAKEMAN_REPORT_FILE, String REPORT_DIRECTORY)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Scanning code for security vulnerabilities, please wait..."
        sh "cd $REPORT_DIRECTORY "
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to scan the code for security vulnerabilities, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/********************************************************
***** Function to perform ruby static code analyzer *****
*********************************************************/
def rubyCodeAnalyzer(String RUBOCOP_REPORT_FILE, String REPORT_DIRECTORY)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Performing ruby static code analysis with rubocop, please wait..."
        sh "rubocop -R --fail-level E  -f html -o $REPORT_DIRECTORY/$RUBOCOP_REPORT_FILE . || true"
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to perform rubocop static code analysis, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/**********************************************
***** Function to perform ruby code smell *****
***********************************************/
def rubyCodeSmell(String REPORT_DIRECTORY)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Performing ruby code smell with RubyCritic stack: Reek, Flay and Flog, please wait..."
        sh "rubycritic --mode-ci --no-browser -p $REPORT_DIRECTORY"
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to perform ruby code smell with RubyCritic, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/****************************************************
***** Function to check the ruby best practices *****
*****************************************************/
def rubyBestPractices(String REPORT_DIRECTORY)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Scanning code for ruby best practices, please wait..."
        sh "sandi_meter -d -g -q -o ${REPORT_DIRECTORY} || true"
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to scan ruby code for best practices, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/**********************************************
***** Function to run the ruby unit tests *****
***********************************************/
def rubyUnitTests(String RSPEC_REPORT_FILE,String REPORT_DIRECTORY)
{
  try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Running ruby unit tests with RSpec, please wait..."
        sh "rspec --color --format html --out $REPORT_DIRECTORY/$RSPEC_REPORT_FILE spec"
      }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to run the rspec tests for ruby code, exiting..."
        currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/**********************************************
***** Function to run the ruby unit tests *****
***********************************************/
def createRubyPackage(String BRAND_NAME, String BUILD_PACKAGE_DIRECTORY)
{
  try {
    wrap([$class: 'AnsiColorBuildWrapper']) {
      println "\u001B[32mINFO => Creating compressed package FCA-${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz, please wait..."
      sh "echo $BUILD_NUMBER > BUILD_NUMBER && tar -cvzf $BUILD_PACKAGE_DIRECTORY/FCA-${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz --exclude=.git --exclude=reports --exclude=.gitignore --exclude=author --exclude=build.json ."
    }
  }
  catch (Exception caughtException) {
    wrap([$class: 'AnsiColorBuildWrapper']) {
       println "\u001B[41mERROR => failed to create the compressed package FCA-${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz, exiting..."
       currentBuild.result = 'FAILED'
       throw caughtException
    }
  }
}

/**************************************************
***** Function to copy the ruby build package *****
***************************************************/
def copyBuildPackage(String BRAND_NAME, String BUILD_PACKAGE_DIRECTORY, String LINUX_CREDENTIALS, String DEPLOYMENT_PACKAGE_DIRECTORY, String DEPLOYMENT_SERVERS, String LINUX_USER)
{
    try {
	  wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Deploying ruby on rails build package FCA-${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz, please wait..."
        for (LINUX_SERVER in DEPLOYMENT_SERVERS.split(',')) {
           sshagent(["${LINUX_CREDENTIALS}"]) {
             sh "scp ${BUILD_PACKAGE_DIRECTORY}/FCA-${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz ${LINUX_USER}@${LINUX_SERVER}:$DEPLOYMENT_PACKAGE_DIRECTORY"
           }
		}
      }
	}
	catch(Exception caughtException) {
	  wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to copy the build package package FCA-${BRAND_NAME}-BUILD-${BUILD_NUMBER}.tar.gz, exiting..."
	    currentBuild.result = 'FAILED'
        throw caughtException
      }
	}
}

/*********************************************************
***** Function to cleanup the old ruby build package *****
**********************************************************/
def cleanBuildPackage(String BRAND_NAME, String BUILD_PACKAGE_DIRECTORY)
{
    try {
	  wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[32mINFO => Cleaning up old ruby on rails build packages for brand ${BRAND_NAME}, please wait..."
        def PACKAGES = sh (script: "ls -t ${BUILD_PACKAGE_DIRECTORY}/FCA-${BRAND_NAME}*.tar.gz | tail -n +3",returnStdout: true).trim()
        if (PACKAGES) {
           sh "ls -t ${BUILD_PACKAGE_DIRECTORY}/FCA-${BRAND_NAME}*.tar.gz | tail -n +3 | xargs rm --"
        }
      }
	} 
	catch(Exception caughtException) {
	  wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to clean the old ruby on rails build packages for brand ${BRAND_NAME}, exiting..."
	    currentBuild.result = 'FAILED'
        throw caughtException
      }
	}
}


