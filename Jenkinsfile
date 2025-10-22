pipeline {
    agent any

    parameters {
        string(name: 'BRANCH', defaultValue: 'master', description: 'Git branch to build')
    }

    tools {
        maven 'M3'
    }

    stages {
        stage('Checkout & Build') { 
            steps {
                 git branch: "${params.BRANCH}",
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
                    sh 'docker-compose up -d --build --force-recreate'
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