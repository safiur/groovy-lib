Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1' )
import groovyx.net.http.HTTPBuilder;
def call(def server, def port) {

        httpRequest httpMode: 'POST', url: "http://${server}:${port}/shutdown", validResponseCodes: '200,408'
        sshagent(['RemoteCredentials']) {
        sh "scp target/*.jar root@${server}:/usr/local/tomcat/webapps/ROOT/"
        sh "/usr/local/tomcat/bin/startup.sh" 
}
retry (3) {
        sleep 5
        httpRequest url:"http://${server}:${port}/health", validResponseCodes: '200', validResponseContent: '"status":"UP"'
    }
}
