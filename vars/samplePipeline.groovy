/*************************************************************************
**** Description :: This groovy code is used to run the ROR pipeline  ****
**** Created By  :: Pramod Vishwakarma                                ****
**** Created On  :: 08/02/2018                                        ****
**** version     :: 1.0                                               ****
**************************************************************************/
import com.symantec.devops.scm.*
//import com.symantec.devops.build.java.*
//import com.symantec.devops.reports.*
//import com.symantec.devops.notification.*
//import com.symantec.devops.deploy.*
//import com.symantec.devops.approval.*

def call(body) 
{
   def config = [:]
   body.resolveStrategy = Closure.DELEGATE_FIRST
   body.delegate = config
   body()
   timestamps {
           def git = new git()
           git.Checkout("${config.GIT_URL}","${BRANCH}","${config.GIT_CREDENTIALS}")
    }
}	

