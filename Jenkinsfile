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
                container('docker') {
                git url: 'https://github.com/Hyunkyoungkang/Project_DiB.git',
                branch: 'main', credentialsId: 'gitToken'
                }
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
                container('docker') {
                 sh """
                     cd ./project_DiB
                     pwd
                     chmod +x ./gradlew
                     ./gradlew build -x test
                 """
                }
           }
        }
        
        stage('Docker Image Build') {
            steps {
                echo 'Docker Image build'
                container('docker') {
                dir("${env.WORKSPACE}") {
                    sh """
                       docker build -t hyunkyoungkang/Project_DiB:$BUILD_NUMBER .
                       docker tag hyunkyoungkang/Project_DiB:$BUILD_NUMBER hyunkyoungkang/Project_DiB:latest
                    """
                }
            }
            }
        }

        stage('Docker Login') {
            steps {
                // docker hub 로그인
                container('docker') {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                }
            }
        }
        stage('Docker Image Push') {
            steps {
                echo 'Docker Image Push'  
                container('docker') {
                sh "docker push hyunkyoungkang/Project_DiB:latest"  // docker push
                }
            }
        }
        stage('Cleaning up') { 
              steps { 
              // docker image 제거
              echo 'Cleaning up unused Docker images on Jenkins server'
              container('docker') {
              sh """
                  docker rmi hyunkyoungkang/Project_DiB:$BUILD_NUMBER
                  docker rmi hyunkyoungkang/Project_DiB:latest
              """
            }
           }
        }
    }
}
