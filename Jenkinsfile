pipeline {
    agent any

    tools {
        maven 'M3'
    }

    stages {
        stage('Checkout & Build') { 
            steps {
                git branch: "jenkins",
                    url: 'https://github.com/ThomasWilson1903/TaskTracker'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    // Собираем образ
                    sh 'docker build -t my-app .'
                }
            }
        }

        stage('Docker Compose Deploy') {
            steps {
                script {
                    // Останавливаем и удаляем старые контейнеры
                    sh 'docker-compose down || true'
                    sh 'ls -a'
                    // Запускаем через docker-compose
                    sh 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
            // Очистка после выполнения
            sh 'docker system prune -f || true'
        }
        success {
            echo 'Приложение успешно развернуто через Docker Compose!'
        }
        failure {
            echo 'Произошла ошибка при развертывании'
        }
    }
}