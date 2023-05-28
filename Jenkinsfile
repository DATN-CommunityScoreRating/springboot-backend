pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub_user')
    }
    stages {
        stage('Maven build') {
            agent {
                    docker {
                        image 'maven:3.6.3-jdk-11'
                        args '-v /root/.m2:/root/.m2'
                        reuseNode true
                    }
                }
            steps {
                     sh 'mvn -B -DskipTests clean package'
                }
        }
        stage('Docker build') {
            steps {
                sh 'docker build -t anhdai0801/capstone-project-backend .'
            }
        }
        stage('Push Docker Hub') {
            steps {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                sh 'docker push anhdai0801/capstone-project-backend'
            }
        }
        stage ('Deploy with docker'){
            steps{
                sh 'docker pull anhdai0801/capstone-project-backend'
                sh 'docker stop capstone-backend-container || true && docker rm capstone-backend-container || true'
                sh 'docker run -dp 9090:8080 --env DB_HOST --env DB_PORT --env DB_NAME --env DB_USER --env DB_PASS --env REDIS_HOST --env REDIS_PORT --name capstone-backend-container anhdai0801/capstone-project-backend'
            }
        }
    }
}