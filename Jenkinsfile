pipeline {
    agent any
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub_user')
    }
    stages {
        stage('Build') {
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
    }
}