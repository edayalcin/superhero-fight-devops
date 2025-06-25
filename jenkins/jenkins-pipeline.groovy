pipeline {
  agent { label 'built-in' } 

  environment {
    NODE_ENV = 'production'
    HARBOR_REGISTRY = 'harbor.local'
    HARBOR_PROJECT = 'superhero'
    IMAGE_NAME = 'superhero-app'
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', url: 'https://github.com/edayalcin/superhero-fight-app.git'
      }
    }
    
    stage('Set Version Tag from Commit Hash') {
      steps {
        script {
          COMMIT_HASH = sh(script: 'git rev-parse --short=6 HEAD', returnStdout: true).trim()
          echo "${COMMIT_HASH}"
          env.IMAGE_TAG = COMMIT_HASH
          echo "🔖 Commit tag olarak kullanılacak: ${IMAGE_TAG}"
        }
      }
    }
    
    stage('Docker Build') {
        steps {
            sh """
              docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
            """
      }
    }
    
    stage('Docker Tag & Push to Harbor') {
      steps {
          script {
            sh """
              docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/${IMAGE_NAME}:${IMAGE_TAG}
              docker push ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/${IMAGE_NAME}:${IMAGE_TAG}
            """
          }
      }
    }

  }

  post {
    success {
      echo '✅ Build başarılı!'
    }
    failure {
      echo '❌ Build başarısız!'
    }
  }
}