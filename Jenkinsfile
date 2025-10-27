pipeline {
    agent {
        label 'docker'
    }

    parameters {
        string(name: 'BRANCH', defaultValue: 'master', description: 'Git branch to build')
    }

    tools {
        maven 'M3'
    }

    environment {
        DOCKER_IMAGE_NAME = 'task-tracker'
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}"
    }


    stages {
        stage('Tools Verification') {
            steps {
                script {
                    echo "🔧 Проверка доступности инструментов на агенте: ${env.NODE_NAME}"

                    // Проверка Java
                    sh '''
                        echo "=== Java ==="
                        java -version
                        echo "JAVA_HOME: ${JAVA_HOME}"
                    '''

                    // Проверка Maven
                    sh '''
                        echo "=== Maven ==="
                        mvn --version
                        which mvn
                    '''

                    // Проверка Git
                    sh '''
                        echo "=== Git ==="
                        git --version
                        which git
                    '''

                    // Проверка Docker
                    sh '''
                        echo "=== Docker ==="
                        docker --version
                        which docker
                        echo "Проверка Docker daemon..."
                        docker ps > /dev/null && echo "✅ Docker daemon доступен" || echo "❌ Docker daemon недоступен"
                    '''

                    // Проверка Docker Compose
                    sh '''
                        echo "=== Docker Compose ==="
                        docker-compose --version
                        which docker-compose
                    '''

                    // Проверка дискового пространства
                    sh '''
                        echo "=== Дисковое пространство ==="
                        df -h
                    '''

                    // Проверка текущего пользователя и прав
                    sh '''
                        echo "=== Пользователь и права ==="
                        whoami
                        id
                        pwd
                        echo "Права на Docker socket:"
                        ls -la /var/run/docker.sock 2>/dev/null || echo "Docker socket не найден"
                    '''
                }
            }
        }
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
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        stage('Docker Compose Deploy') {
            steps {
                script {
                    sh '''
                        docker-compose down || true
                        docker-compose up -d --build --force-recreate
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    sleep time: 120, unit: 'SECONDS'
                    sh 'curl -f http://twilson.ru:8080/actuator/health || echo "Application health check failed but continuing..."'
                }
            }
        }
    }

    post {
        always {
            sh '''
                docker system prune -f || true
                docker volume prune -f || true
            '''
        }
        success {
            echo "✅ Приложение успешно развернуто! Image: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
        }
        failure {
            echo '❌ Произошла ошибка при развертывании'
            sh '''
                docker ps -a
                docker-compose logs || true
            '''
        }
    }
}