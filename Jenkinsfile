pipeline {
    agent any

    stages {
        stage('Build') {
            agent {
                    docker {
                        image 'maven:3.9.0-eclipse-temurin-11'
                        args '-v /root/.m2:/root/.m2'
                        reuseNode true
                    }
                }
            steps {
                     sh 'mvn -B -DskipTests clean package'
                }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}