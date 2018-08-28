def call(def server, def port) {
        sh "scp target/*.jar root@${server}:/usr/local/tomcat/webapps/ROOT/"
        sh "/usr/local/tomcat/bin/startup.sh" 
}
