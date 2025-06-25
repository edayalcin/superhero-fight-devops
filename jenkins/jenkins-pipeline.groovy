pipeline {
  agent { label 'built-in' } 

  environment {
    NODE_ENV = 'production'
    HARBOR_REGISTRY = 'harbor.local'
    HARBOR_PROJECT = 'superhero'
    IMAGE_NAME = 'superhero-app'
    
  }

  stages {
    stage('Checkout Source Code') {
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
    
    stage('Checkout Kubernetes Manifest') {
      steps {
        git branch: 'main', url: 'https://github.com/edayalcin/superhero-fight-devops.git'
      }
    }
    
    stage('Update Deployment YAML') {
      steps {
        script {
          sh """
            cd ./k8s/
            sed -i '' 's|image: ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/${IMAGE_NAME}:.*|image: ${HARBOR_REGISTRY}/${HARBOR_PROJECT}/${IMAGE_NAME}:${IMAGE_TAG}|' superhero-deployment.yaml
          """
        }
      }
    }
    
    stage('Commit & Push YAML Update') {
  steps {
    withCredentials([usernamePassword(credentialsId: 'git-credential', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
      script {
        sh """
          git config user.name "${GIT_USER}"
          git config user.email "edaanurrylcn@gmail.com"
          
          git add . || true
          git commit -m "🔄 Update image tag to ${IMAGE_TAG} by Jenkins" || true

          git remote set-url origin https://${GIT_USER}:${GIT_PASS}@github.com/edayalcin/superhero-fight-devops.git
          git push origin main
        """
      }
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