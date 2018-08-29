@Grapes([
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' ),
@GrabExclude('xml-apis:xml-apis'),
@GrabExclude('org.codehaus.groovy:groovy')
])
import groovyx.net.http.HTTPBuilder;
def call(def server, def port) {

        httpRequest httpMode: 'POST', url: "http://${server}:${port}/shutdown", validResponseCodes: '200,408,404'
        sshagent(['sshkey_id']) {
        sh "target/*.jar root@${server}:/usr/local/tomcat/webapps/ROOT/"
        sh "/usr/local/tomcat/bin/startup.sh" 
}
retry (3) {
        sleep 5
        httpRequest url:"http://${server}:${port}/health", validResponseCodes: '200', validResponseContent: '"status":"UP"'
    }
}
