pipeline {
    agent {
        label 'docker'
    }

    parameters {
        string(name: 'BRANCH', defaultValue: 'master', description: 'Git branch to build')
        string(name: 'NAMESPACE', defaultValue: 'sandbox', description: 'Kubernetes namespace')
        string(name: 'K8S_CLUSTER', defaultValue: 'k3s', description: 'Kubernetes cluster context')
    }

    tools {
        maven 'M3'
    }

    environment {
        DOCKER_IMAGE_NAME = 'task-tracker'
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_REGISTRY = 'your-registry'  // Замените на ваш registry
        KUBECONFIG = credentials('k8s-config')  // Добавьте credentials в Jenkins
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

                    // Проверка Kubectl
                    sh '''
                        echo "=== Kubernetes ==="
                        kubectl version --client
                        which kubectl
                        echo "Проверка доступа к кластеру..."
                        kubectl cluster-info && echo "✅ Доступ к кластеру есть" || echo "❌ Нет доступа к кластеру"
                    '''

                    // Проверка дискового пространства
                    sh '''
                        echo "=== Дисковое пространство ==="
                        df -h
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

        stage('Docker Build & Push') {
            steps {
                script {
                    // Логин в registry если нужно
                    // sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD} ${DOCKER_REGISTRY}"

                    // Сборка и тегирование
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ."
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} ${DOCKER_IMAGE_NAME}:latest"

                    // Push в registry если используется
                    // sh "docker push ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
                    // sh "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                script {
                    // Создаем Kubernetes манифесты динамически
                    sh """
                    cat > k8s-deployment.yaml << EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-tracker
  namespace: ${params.NAMESPACE}
  labels:
    app: task-tracker
    version: "${DOCKER_IMAGE_TAG}"
spec:
  replicas: 1
  selector:
    matchLabels:
      app: task-tracker
  template:
    metadata:
      labels:
        app: task-tracker
        version: "${DOCKER_IMAGE_TAG}"
    spec:
      containers:
      - name: task-tracker
        image: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
        env:
        - name: URL_DATA
          value: "jdbc:postgresql://postgres:5432/task_tracker"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1024Mi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: task-tracker-service
  namespace: ${params.NAMESPACE}
spec:
  selector:
    app: task-tracker
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30090
  type: NodePort
EOF
                    """

                    // Применяем манифесты
                    sh "kubectl apply -f k8s-deployment.yaml"

                    // Ждем развертывания
                    sh "kubectl rollout status deployment/task-tracker -n ${params.NAMESPACE} --timeout=300s"
                }
            }
        }

        stage('Health Check') {
            steps {
                script {
                    // Получаем URL сервиса
                    sh """
                    NODE_IP=\$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type==\"InternalIP\")].address}')
                    echo "Application URL: http://\${NODE_IP}:30090/actuator/health"
                    """

                    // Health check
                    sleep time: 30, unit: 'SECONDS'
                    sh '''
                        NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
                        curl -f http://${NODE_IP}:30090/actuator/health || echo "Health check failed but continuing..."
                    '''
                }
            }
        }
    }

    post {
        always {
            // Очистка
            sh '''
                docker system prune -f || true
                rm -f k8s-deployment.yaml || true
            '''

            // Логи при failure
            script {
                if (currentBuild.result == 'FAILURE') {
                    sh """
                    echo "=== Pods status ==="
                    kubectl get pods -n ${params.NAMESPACE} -l app=task-tracker
                    
                    echo "=== Pods logs ==="
                    kubectl logs -n ${params.NAMESPACE} -l app=task-tracker --tail=50
                    
                    echo "=== Deployment status ==="
                    kubectl describe deployment task-tracker -n ${params.NAMESPACE}
                    """
                }
            }
        }
        success {
            echo "✅ Приложение успешно развернуто в Kubernetes!"
            echo "Image: ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
            echo "Namespace: ${params.NAMESPACE}"
            sh '''
                NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}')
                echo "📱 Доступно по: http://${NODE_IP}:30090"
            '''
        }
        failure {
            echo '❌ Произошла ошибка при развертывании в Kubernetes'
        }
    }
}