/********************************************************************************
***** Description :: This Package is used to perform RubyOnRails deployment *****
***** Author      :: DevOps Team                                            *****
***** Date        :: 12/18/2017                                             *****
***** Revision    :: 1.0                                                    *****
*********************************************************************************/
package com.symantec.devops.deploy

/*******************************************
***** Function to deploy the ruby code *****
********************************************/
def deployRubyCode(String LINUX_CREDENTIALS, String LINUX_USER, String DEPLOYMENT_SERVERS, String ENVIRONMENT, String BRAND_NAME, String DEPLOYMENT_SCRIPT)
{
    try {
	  wrap([$class: 'AnsiColorBuildWrapper']) {
	    println "\u001B[32mINFO => Deploying ruby on rails code for brand $BRAND_NAME and environment ${ENVIRONMENT}, please wait..."
        for (LINUX_SERVER in DEPLOYMENT_SERVERS.split(',')) {
          sshagent(["${LINUX_CREDENTIALS}"]) {
             sh "ssh -o StrictHostKeyChecking=no -l $LINUX_USER $LINUX_SERVER $DEPLOYMENT_SCRIPT --brand=${BRAND_NAME} --build=${BUILD_NUMBER} --environment=${ENVIRONMENT}"
		  }
        }
      }
	}
	catch(Exception caughtException) {
	  wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to deploy ruby on rails code for brand $BRAND_NAME and environment ${ENVIRONMENT}, exiting..."
	    currentBuild.result = 'FAILED'
        throw caughtException
      }
	}
}
`
