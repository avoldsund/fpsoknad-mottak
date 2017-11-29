@Library('deploy')
import deploy

def deployLib = new deploy()

node {
    def commitHash, commitHashShort, commitUrl, currentVersion
    def repo = "navikt"
    def application = "p2-selvbetjening-mottak"
    def committer, committerEmail, changelog, pom, releaseVersion, isSnapshot, nextVersion // metadata
    def mvnHome = tool "maven-3.3.9"
    def mvn = "${mvnHome}/bin/mvn"
    def appConfig = "nais.yaml"
    def dockerRepo = "docker.adeo.no:5000"
    def branch = "master"
    def groupId = "nais"
    def environment = 't1'
    def zone = 'fss'
    def namespace = 'default'

    //try {

    stage("checkout") {
        cleanWs()
        withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
                sh(script: "git clone https://github.com/${repo}/${application}.git .")
            }
        commitHash = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
        commitHashShort = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        commitUrl = "https://github.com/${repo}/${application}/commit/${commitHash}"

            /* gets the person who committed last as "Surname, First name" */
        committer = sh(script: 'git log -1 --pretty=format:"%an"', returnStdout: true).trim()
    }

    stage("initialize") {
        pom = readMavenPom file: 'pom.xml'
        releaseVersion = pom.version.tokenize("-")[0]
        isSnapshot = pom.version.contains("-SNAPSHOT")
        committer = sh(script: 'git log -1 --pretty=format:"%an (%ae)"', returnStdout: true).trim()
        committerEmail = sh(script: 'git log -1 --pretty=format:"%ae"', returnStdout: true).trim()
        changelog = sh(script: 'git log `git describe --tags --abbrev=0`..HEAD --oneline', returnStdout: true)
    }

    stage("verify maven versions") {
        sh 'echo "Verifying that no snapshot dependencies is being used."'
        sh 'grep module pom.xml | cut -d">" -f2 | cut -d"<" -f1 > snapshots.txt'
        sh 'while read line;do if [ "$line" != "" ];then if [ `grep SNAPSHOT $line/pom.xml | wc -l` -gt 1 ];then echo "SNAPSHOT-dependencies found. See file $line/pom.xml.";exit 1;fi;fi;done < snapshots.txt'
    }

    stage("test backend") {
        if (isSnapshot) {
            sh "${mvn} clean install -Djava.io.tmpdir=/tmp/${application} -B -e"
        } else {
            println("POM version is not a SNAPSHOT, it is ${pom.version}. Skipping build and testing of backend")
        }

    }

    stage("release version") {
        if (isSnapshot) {
            sh "${mvn} versions:set -B -DnewVersion=${releaseVersion} -DgenerateBackupPoms=false"
            sh "${mvn} clean install -Djava.io.tmpdir=/tmp/${application} -B -e"
            sh "docker build --build-arg version=${releaseVersion} --build-arg app_name=${application} -t ${dockerRepo}/${application}:${releaseVersion} ."
            sh "git commit -am \"set version to ${releaseVersion} (from Jenkins pipeline)\""
            //sh "git push origin master"
            withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
              withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
                   sh ("git push https://${token}:x-oauth-basic@github.com/navikt/p2-selvbetjening-mottak.git master")
                   sh ("git tag -a ${application}-${releaseVersion} -m ${application}-${releaseVersion}")
                   sh ("git push https://${token}:x-oauth-basic@github.com/navikt/p2-selvbetjening-mottak.git --tags")
               }
            }
        }else{
            println("POM version is not a SNAPSHOT, it is ${pom.version}. Skipping releasing")
        }
    }
    stage("publish artifact") {
        if (isSnapshot) {
            sh "${mvn} clean deploy -DskipTests -B -e"
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'nexusUser', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                sh "curl -s -F r=m2internal -F hasPom=false -F e=yaml -F g=${groupId} -F a=${application} -F " + "v=${releaseVersion} -F p=yaml -F file=@${appConfig} -u ${env.USERNAME}:${env.PASSWORD} http://maven.adeo.no/nexus/service/local/artifact/maven/content"
            }
            sh "docker push ${dockerRepo}/${application}:${releaseVersion}"
        } else {
            println("POM version is not a SNAPSHOT, it is ${pom.version}. Skipping publishing!")
        }
    }


    stage("deploy to t") {
        callback = "${env.BUILD_URL}input/Deploy/"
        deployLib.testCmd(releaseVersion)
        deployLib.testCmd(committer)

        def deploy = deployLib.deployNaisApp(application, releaseVersion, environment, zone, namespace, callback, committer).key

        try {
            timeout(time: 15, unit: 'MINUTES') {
                input id: 'deploy', message: "Check status here:  https://jira.adeo.no/browse/${deploy}"
            }
        } catch (Exception e) {
            throw new Exception("Deploy feilet :( \n Se https://jira.adeo.no/browse/" + deploy + " for detaljer", e)

        }
    }

    // Add test of preprod instance here

    stage("new dev version") {
        nextVersion = releaseVersion + "-SNAPSHOT"
        sh "${mvn} versions:set -B -DnewVersion=${nextVersion} -DgenerateBackupPoms=false"
        withEnv(['HTTPS_PROXY=http://webproxy-utvikler.nav.no:8088']) {
             withCredentials([string(credentialsId: 'OAUTH_TOKEN', variable: 'token')]) {
                 sh "git commit -am \"updated to new dev-version ${nextVersion} after release by ${committer}\""
                 sh "git push origin master"
       }
    }

    //hipchatSend  color: 'GREEN', message: "Jeg deployet akkurat ${application} :${releaseVersion} til Nais", textFormat: true, room: 'PAM - CV Utvikling', v2enabled: true, token: 'ZzxxzGzuY7BgKHk6dy6TJ1XqCQpAa34Zi6Tm4M2R'

    //} catch (e) {
    //hipchatSend color: 'RED', message: "@all ${env.JOB_NAME} failed (nice) \n \n Committer: ${committer} \n Jenkins: http://a34apvl00015.devillo.no:8080/job/pam-arena-T1/ " , textFormat: true, notify: true, room: 'PAM - CV Utvikling', v2enabled: true, token: 'ZzxxzGzuY7BgKHk6dy6TJ1XqCQpAa34Zi6Tm4M2R'
    //}

}
