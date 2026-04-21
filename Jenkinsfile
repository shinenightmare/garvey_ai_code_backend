pipeline {
    agent any  // 使用宿主机 agent（你的 Jenkins 容器）

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            // 在这个阶段使用 Maven 容器
            agent {
                docker {
                    image 'maven:3-openjdk-17'
                    args '-v /root/.m2:/root/.m2'  // 缓存 Maven 依赖
                }
            }
            steps {
                sh 'mvn clean package -DskipTests'
                // 将构建产物复制到工作空间
                sh 'cp target/*.jar ./app.jar'
            }
        }

        stage('Docker Build') {
            agent any  // 切换回主 agent
            steps {
                sh 'docker build -t garvey_ai_code_backend:latest .'
            }
        }

        stage('Deploy') {
            agent any
            steps {
                sh 'docker stop garvey_ai_code_backend || true'
                sh 'docker rm garvey_ai_code_backend || true'
                sh 'docker run -d --name garvey_ai_code_backend -p 8080:8080 garvey_ai_code_backend:latest'
            }
        }
    }

    post {
        success {
            echo '🎉 构建部署成功！'
        }
        failure {
            echo '❌ 构建失败'
        }
    }
}