pipeline {
    agent {
        label 'docker'
    }

    environment {
        // Docker
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_IMAGE_NAME = 'thomaswilson1903/tasktracker'
        DOCKER_TAG = "${env.BUILD_NUMBER}"

        // Kubernetes
        K8S_NAMESPACE = 'default'
        K8S_DEPLOYMENT = 'my-app-deployment'
    }

    tools {
        maven 'M3'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    checkout scm
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    echo 'Building project with Maven...'
                    sh 'mvn clean compile -B'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo 'Running tests...'
                    sh 'mvn test -B'
                }
            }

            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    echo 'Packaging application...'
                    sh 'mvn package -DskipTests -B'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Building Docker image...'
                    sh """
                        docker build -t ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG} .
                        docker tag ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG} ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest
                    """
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(
                            credentialsId: 'docker-hub-creds',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASSWORD'
                    )]) {
                        sh """
                    docker login -u $DOCKER_USER -p $DOCKER_PASSWORD
                    docker push ${DOCKER_IMAGE_NAME}:${DOCKER_TAG}
                    docker push ${DOCKER_IMAGE_NAME}:latest
                    docker logout
                """
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
            // Можно добавить уведомления
        }
        failure {
            echo 'Pipeline failed!'
            // Можно добавить уведомления об ошибке
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}