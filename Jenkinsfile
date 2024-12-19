pipeline {
    agent any

    tools {
        maven 'Maven' // Replace 'Maven' with the actual name of your Maven installation in Jenkins
    }

    environment {
        DOCKER_IMAGE = 'e-bankify-security'
        DOCKER_TAG = "${BUILD_NUMBER}"
        SONAR_TOKEN = credentials('jenkins-sonar')
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=true'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    deleteDir() // Clean workspace
                    echo "Cloning Git repository..."
                    sh '''
                        git clone -b main https://github.com/MedELHattab/E-Bankify-Security.git .
                        echo "Repository cloned successfully."
                    '''
                }
            }
        }

        stage('Environment Check') {
            steps {
                sh '''
                    echo "Git version:"
                    git --version
                    echo "Current Git branch:"
                    git branch --show-current
                    echo "Git status:"
                    git status
                    echo "Java version:"
                    java -version
                    echo "Maven version:"
                    mvn --version
                    echo "Working directory contents:"
                    pwd
                    ls -la
                '''
            }
        }

        stage('Build') {
            steps {
                sh '''
                    mvn clean package -DskipTests --batch-mode --errors
                '''
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test --batch-mode'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml' // Archive JUnit results
                    jacoco(
                        execPattern: '**/target/*.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        // New stage to verify that the test reports are generated
        stage('Verify Test Reports') {
            steps {
                script {
                    echo "Verifying test results..."
                    sh 'ls -la target/surefire-reports'  // Lists the test result files in the target/surefire-reports directory
                }
            }
        }

        stage('Code Quality Analysis') {
            steps {
                sh '''
                    mvn sonar:sonar \
                        -Dsonar.projectKey=E-Bankify-Security \
                        -Dsonar.projectName=E-Bankify-Security \
                        -Dsonar.host.url=http://sonar:9000 \
                        -Dsonar.login=${SONAR_TOKEN}
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        stage('Manual Approval') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    input message: 'Deploy to production?', ok: 'Proceed'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").run('-p 8081:8080')
                }
            }
        }
    }

    post {
        success {
            mail to: 'elhattabmohammedelarbi@gmail.com',
                 subject: "Pipeline Success - eBankify",
                 body: "Le pipeline Jenkins s'est terminé avec succès !"
        }
        failure {
            mail to: 'elhattabmohammedelarbi@gmail.com',
                 subject: "Pipeline Failure - eBankify",
                 body: "Le pipeline Jenkins a échoué. Veuillez vérifier les logs."
        }
        unstable {
            mail to: 'elhattabmohammedelarbi@gmail.com',
                 subject: "Pipeline Unstable - eBankify",
                 body: "Le pipeline Jenkins est dans un état instable. Veuillez vérifier les logs et résoudre les tests échoués."
        }
    }
}
