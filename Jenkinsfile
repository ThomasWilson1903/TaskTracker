pipeline {
    agent any

    tools {
        maven 'M3'
    }

    stages {
        stage('Checkout & Build') {
            steps {
                git 'https://github.com/ThomasWilson1903/TaskTracker'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build & Run') {
            steps {
                script {
                    // Собираем образ
                    sh 'docker build -t my-app .'

                    // Останавливаем старый контейнер
                    sh 'docker stop my-app || true'
                    sh 'docker rm my-app || true'

                    // Запускаем новый
                    sh 'docker run -d -p 9090:8080 --name my-app my-app'
                }
            }
        }
    }
}