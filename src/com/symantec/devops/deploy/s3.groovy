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
		   cd build
		   aws s3 sync . s3://$BUCKET_NAME --cache-control "no-cache, no-store" --metadata-directive REPLACE --delete
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


