pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                echo '开始拉取代码...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '开始构建...'
                // 如果是 Maven 项目
                sh 'mvn clean package -DskipTests'
                // 如果是 Node.js
                // sh 'npm install && npm run build'
            }
        }

        stage('Deploy') {
            steps {
                echo '开始部署...'
                sh 'docker build -t ai-code-backend:latest .'
                sh 'docker run -d --name ai-code-backend -p 8080:8080 ai-code-backend:latest'
            }
        }
    }
}