pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Irfan622/Spring-Batch.git'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image
                    sh 'docker build -t my-spring-boot-app .'
                }
            }
        }
        stage('Run Docker Container') {
            steps {
                script {
                    // Run Docker container
                    sh 'docker run -d -p 8080:8080 --name my-spring-boot-app my-spring-boot-app'
                }
            }
        }
    }
    post {
        always {
            // Clean up Docker container
            sh 'docker stop my-spring-boot-app || true'
            sh 'docker rm my-spring-boot-app || true'
        }
    }
}
