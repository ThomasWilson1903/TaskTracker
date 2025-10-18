pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'task-tracker:latest'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/ThomasWilson1903/TaskTracker.git'
            }
        }

        stage('Build and Test') {
            steps {
                sh '''
                    mvn clean compile
                    mvn test
                    mvn package -DskipTests
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    docker stop task-tracker-container || true
                    docker rm task-tracker-container || true
                    docker run -d -p 8080:8080 --name task-tracker-container ${DOCKER_IMAGE}
                '''
            }
        }
    }

    post {
        always {
            echo 'Pipeline завершен'
        }
    }
}