pipeline {
    agent any

    environment {
        JAVA_HOME = tool 'JDK21'
        MAVEN_HOME = tool 'Maven3'
        DOCKER_IMAGE = 'springboot-cicd-api'
        DOCKER_REGISTRY = credentials('docker-registry-credentials')
        APP_PORT = '8080'
    }

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    stages {
        stage('🔄 Checkout') {
            steps {
                echo '=== Checking out source code ==='
                checkout scm
                sh 'git log --oneline -5'
            }
        }

        stage('📦 Build') {
            steps {
                echo '=== Building with Maven ==='
                sh 'mvn clean compile -B'
            }
        }

        stage('🧪 Test') {
            steps {
                echo '=== Running Tests ==='
                sh 'mvn test -B'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: true
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage Report'
                    ])
                }
            }
        }

        stage('📦 Package') {
            steps {
                echo '=== Packaging Application ==='
                sh 'mvn package -DskipTests -B'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('🐳 Docker Build') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo '=== Building Docker Image ==='
                script {
                    def imageTag = "${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                    def latestTag = "${DOCKER_IMAGE}:latest"

                    sh "docker build -t ${imageTag} -t ${latestTag} ."
                    echo "Docker image built: ${imageTag}"
                }
            }
        }

        stage('🚀 Push to Registry') {
            when {
                branch 'main'
            }
            steps {
                echo '=== Pushing Docker Image to Registry ==='
                script {
                    withDockerRegistry(credentialsId: 'docker-registry-credentials') {
                        sh "docker push ${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }

        stage('🌍 Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo '=== Deploying to Server ==='
                sshagent(credentials: ['deploy-server-ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no $DEPLOY_USER@$DEPLOY_HOST \
                        "docker pull ${DOCKER_IMAGE}:latest && \
                         docker stop springboot-api || true && \
                         docker rm springboot-api || true && \
                         docker run -d --name springboot-api \
                           --restart unless-stopped \
                           -p 8080:8080 \
                           ${DOCKER_IMAGE}:latest"
                    '''
                }
            }
        }

        stage('✅ Health Check') {
            when {
                branch 'main'
            }
            steps {
                echo '=== Verifying Deployment ==='
                sh '''
                    sleep 30
                    curl -f http://$DEPLOY_HOST:8080/api/v1/health || exit 1
                    echo "Health check passed!"
                '''
            }
        }
    }

    post {
        always {
            echo '=== Pipeline Complete ==='
            cleanWs()
        }
        success {
            echo '✅ Pipeline succeeded!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
