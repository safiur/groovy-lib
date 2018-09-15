package com.symantec.devops.deploy

/********************************************
** Function to Deploy S3 Bucket
*********************************************/
def s3Deploy(String BUCKET_NAME)
{
   try {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        if (BUCKET_NAME == 'null') {
           error "\u001B[41mERROR => S3 bucket name can't be empty,exiting..."
        }
	    println "\u001B[32mINFO => Deploying on S3 Bucket $BUCKET_NAME, please wait..."
		sh """
          zip -r dist.zip dist/* node_modules/*
          aws s3 cp dist.zip s3://$BUCKET_NAME --cache-control "no-cache" --metadata-directive REPLACE
          rm -rf dist.zip
		 """
	  }
   }
   catch (Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
        println "\u001B[41mERROR => failed to Deploy on S3 Bucket $BUCKET_NAME"
	    currentBuild.result = 'FAILED'
        throw caughtException
      }
   }
}

/****************************************************
***** Function to validate the lambda functions *****
*****************************************************/
def validateLambdaFunctions(String LAMBDA_VALIDATION_SCRIPT,String ENVIRONMENT)
{
   try {
       ansiColor('xterm') {
	      println "\u001B[32mINFO => validating AWS lambda functions, please wait..."
	      sh "$LAMBDA_VALIDATION_SCRIPT --environment=$ENVIRONMENT"
	   }
   }
   catch(Exception caughtException) {
      wrap([$class: 'AnsiColorBuildWrapper']) {
         println "\u001B[41mERROR => failed to validate lambda functions, exiting..."
	     currentBuild.result = 'FAILED'
         throw caughtException
      }
   }
}



