pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t garvey_ai_code_backend:latest .'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker stop garvey_ai_code_backend || true'
                sh 'docker rm garvey_ai_code_backend || true'
                sh 'docker run -d --name garvey_ai_code_backend -p 8080:8080 garvey_ai_code_backend:latest'
            }
        }
    }
}