/*************************************************************************
**** Description :: this groovy code is used to clone the git code    ****
**** Created By  :: DevOps Team                                       ****
**** Created On  :: 108/12/2018                                        ****
**** version     :: 1.0                                               ****
**************************************************************************/
package com.symantec.devops.scm

/*****************************************************
***** function to checkout code from Git repository
******************************************************/
def Checkout(String GIT_URL, String BRANCH, String GIT_CREDENTIALS)
{
   try {
        wrap([$class: 'AnsiColorBuildWrapper']) {
          def BRANCH_NAME = "${env.BRANCH_NAME}"
          if ( BRANCH_NAME != "null" && !BRANCH_NAME.isEmpty() && !BRANCH_NAME.trim().isEmpty())
	      { 
     	     BRANCH=BRANCH_NAME
          }
          println "\u001B[32mINFO => Checking out ${GIT_URL} from branch ${BRANCH}, please wait..."
          checkout([$class: 'GitSCM', branches: [[name: "${BRANCH}"]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CloneOption', noTags: false, reference: '', shallow: true, timeout: 30]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: "${GIT_CREDENTIALS}", url: "${GIT_URL}"]]])
          env.GIT_BRANCH = "${BRANCH}"
          env.GIT_URL = "$GIT_URL"
          env.GIT_COMMIT = getGitCommitHash()
          env.GIT_AUTHOR_EMAIL = getCommitAuthorEmail()
        }
   }
   catch (Exception caughtError) {
       wrap([$class: 'AnsiColorBuildWrapper']) {
          print "\u001B[41m[ERROR]: clone for repository ${env.GIT_URL} failed, please check the logs..."
          currentBuild.result = "FAILURE"
          throw caughtError
       }
   }
}

/**********************************************
***** function to get the Git commit hash *****
***********************************************/
def getGitCommitHash()
{
   try {
     gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
     return gitCommit
   }
   catch (Exception error)
   {
     wrap([$class: 'AnsiColorBuildWrapper']) {
          print "\u001B[41m[ERROR] failed to get last Git commit ID....."
          throw error
     }
   }
}

/*************************************************
***** Function to get the committer email id *****
**************************************************/
def getCommitAuthorEmail()
{
   try {
     def COMMIT = getGitCommitHash()
     sh "git log --format='%ae' $COMMIT > author"
     def author = readFile('author').trim()
     return author
   }
   catch (Exception error)
   {
     wrap([$class: 'AnsiColorBuildWrapper']) {
          print "\u001B[41m[ERROR] failed to get the last Git commit author email ID....."
          throw error
     }
   }
}

/***************************************************
***** Function to notify bitbuckt build status *****
****************************************************/
def notifyBitbucket(String BITBUCKET_NOTIFY_URL,String BUILD_STATUS)
{
   try {
     wrap([$class: 'AnsiColorBuildWrapper']) {
        print "\u001B[32mINFO => Notifying BitBucket about Jenkins build status, please wait..."
        withCredentials([usernameColonPassword(credentialsId: 'Git-Credentials', variable: 'GIT_NOTIFY')]) {
           sh """
              echo '{' > build.json
              if [ "$BUILD_STATUS" = "SUCCESS" ]; then
                 echo '   "state": "SUCCESSFUL",' >> build.json
              fi
              if [ "$BUILD_STATUS" = "FAILURE" ]; then
                 echo '   "state": "FAILED",' >> build.json
              fi
              echo '   "key": "fcg",' >> build.json
              echo '   "url": "$BUILD_URL"' >> build.json
              echo '}' >> build.json
              curl -u $GIT_NOTIFY -H 'Content-Type: application/json' -X POST $BITBUCKET_NOTIFY_URL/$GIT_COMMIT -d @build.json
           """
        }
     }
   }
   catch (Exception error)
   {
      wrap([$class: 'AnsiColorBuildWrapper']) {
          print "\u001B[41m[ERROR] failed to notify bitbucket about jenkins build status, exiting..."
          throw error
      }
   }
}

/*********************************************
***** Function to raise the pull request *****
**********************************************/
def raisePullRequest(String BITBUCKET_PULL_REQUEST_URL, String PROJECT,String REPOSITORY,String REVIEWER,String REVIEWER_EMAIL, String MERGE_BRANCH)
{
  try {
     wrap([$class: 'AnsiColorBuildWrapper']) {
       println "\u001B[32m[INFO] raising pull request from branch: ${env.GIT_BRANCH} to branch: ${MERGE_BRANCH} in GIT repository $REPOSITORY"
     }
     withCredentials([usernamePassword(credentialsId: 'Git-Credentials', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh """
        curl -u '$USERNAME':'$PASSWORD' -H "Content-Type: application/json" "$BITBUCKET_PULL_REQUEST_URL/$PROJECT/repos/$REPOSITORY/pull-requests" -X POST --data '{"title":"Raised pull request from branch: ${env.GIT_BRANCH}","description":"Raised pull request from branch: ${env.GIT_BRANCH} to review the chef changes","fromRef":{"id":"refs/heads/${env.GIT_BRANCH}","repository":{"slug":"$REPOSITORY","name":null,"project":{"key":"$PROJECT"}}},"toRef":{"id":"refs/heads/${MERGE_BRANCH}","repository":{"slug":"$REPOSITORY","name":null,"project":{"key":"$PROJECT"}}},"reviewers": [{"user": {"name": "$REVIEWER","emailAddress": "$REVIEWER_EMAIL","active": true,"slug": "$REPOSITORY","type": "NORMAL"},"role": "REVIEWER","approved": true,"status": "OPENED"}]}'
        """
     }
  }
  catch(Exception caughtError)
  {
     wrap([$class: 'AnsiColorBuildWrapper']) {
       print "\u001B[41m[ERROR]: failed to raise pull request from branch: ${env.GIT_BRANCH} to branch: ${MERGE_BRANCH} in GIT repository $REPOSITORY, please check the logs..." 
       currentBuild.result='FAILURE'
       throw caughtError
     }
  }
}

