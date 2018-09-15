#!groovy
@Library('PKJEE_lib@master') _
node {
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '2', numToKeepStr: '2'))])
    rubyPipeline_prod {
        GIT_URL                 = ''
        BITBUCKET_NOTIFY_URL    = ''
        GIT_CREDENTIALS         = 'Git-Credentials'
        REPORT_DIRECTORY        = 'reports'
        RUBY_REPORT_FILE        = 'index.html'
        RUBY_REPORT_TITLE       = 'SANDI METER REPORT'
        RUBOCOP_REPORT_FILE     = 'rubocop.html'
        RUBOCOP_REPORT_TITLE    = 'RUBOCOP ANALYSIS REPORT'
        BRAKEMAN_REPORT_FILE    = 'brakeman.html'
        BRAKEMAN_REPORT_TITLE   = 'BRAKEMAN SECURITY REPORT'
        RSPEC_REPORT_FILE       = 'rspec.html'
        RSPEC_REPORT_TITLE      = 'RSPEC TEST REPORT'
        RUBYCRITIC_REPORT_TITLE = 'RUBYCRITIC ANALYSIS REPORT'
        RUBYCRITIC_REPORT_FILE  = 'overview.html'
        RECIPIENT               = 'pramod.s.02@gmail.com'
        EMAIL_TEMPLATE          = 'email_template'
        SKIP_UNIT_TEST          = 'yes'
        DEPLOYMENT_PACKAGE_DIRECTORY = '/app/artifacts'
        DEPLOYMENT_SCRIPT            = '/app/scripts/deploy.sh'
        BRAND_NAME                   = 'DEV'
        BUILD_PACKAGE_DIRECTORY      = '/app/DEV/ci/jenkins/build_packages'
        LINUX_USER                   = 'admin'
    }
}

