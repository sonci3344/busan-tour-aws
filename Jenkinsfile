pipeline {
    agent any
    
    tools {
        jdk 'jdk17'
        gradle 'G3'
        dockerTool 'D3'
    }
    environment { 
        // jenkins에 등록해 놓은 docker hub credentials 이름
        DOCKERHUB_CREDENTIALS = credentials('dockerCredentials') 
    }

    stages {
        stage('Git Clone') {
            steps {
                echo 'Git Clone'
                git url: 'https://github.com/Hyunkyoungkang/busan-tour-on.git',
                branch: 'main', credentialsId: 'gitToken'
            }
            post {
                success {
                    echo 'Success git clone step'
                }
                failure {
                    echo 'Fail git clone step'
                }
            }
        }
        
        stage('gradle Build') {
            steps {
                echo 'gradle Build'
                 sh """
                     cd ./project_DiB
                     pwd
                     chmod +x ./gradlew
                     ./gradlew build -x test
                 """
           }
        }
        
        stage('Docker Image Build') {
            steps {
                echo 'Docker Image build'                
                dir("${env.WORKSPACE}") {
                    sh """
                       docker build -t hyunkyoungkang/busan-tour-on:$BUILD_NUMBER .
                       docker tag hyunkyoungkang/busan-tour-on:$BUILD_NUMBER hyunkyoungkang/busan-tour-on:latest
                    """
                }
            }
        }

        stage('Docker Login') {
            steps {
                // docker hub 로그인
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            }
        }
        stage('Docker Image Push') {
            steps {
                echo 'Docker Image Push'  
                sh "docker push hyunkyoungkang/busan-tour-on:latest"  // docker push
            }
        }
        stage('Cleaning up') { 
              steps { 
              // docker image 제거
              echo 'Cleaning up unused Docker images on Jenkins server'
              sh """
                  docker rmi hyunkyoungkang/busan-tour-on:$BUILD_NUMBER
                  docker rmi hyunkyoungkang/busan-tour-on:latest
              """
            }
        }
    }
}
