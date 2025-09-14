pipeline {
  agent any

  environment {
    // Maven settings.xml 路径（如有私服）
    MAVEN_OPTS = '-Dmaven.repo.local=/home/jenkins/agent/.m2/repository'
    IMAGE_NAME = "helpme:1.0-SNAPSHOT"
  }

  tools {
    maven 'maven-3.9.3'  // Jenkins 中定义的 Maven 名称
//     jdk 'JDK 17'     // 对应 JDK，或改成你用的版本
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Env Check') {
      steps {
        sh '''
          echo "Java version:"
          java -version
          echo "Maven version:"
          mvn -version
        '''
      }
    }

    stage('Remove Last Running') {
      steps {
        sh """
          docker stop helpme || true
        """
      }
    }

    stage('Make Docker Image') {
      steps {
        configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
           sh 'mvn clean package -s $MAVEN_SETTINGS -DskipTests=true'
        }
      }
    }

    stage('Run Docker Image') {
      steps {
        sh """
          docker run -p 32768:8080 --rm -d --name helpme ${IMAGE_NAME}
        """
      }
    }

  }

  post {
    success {
      echo "✅ 部署完成：${IMAGE_NAME}:${env.BUILD_NUMBER}"
    }
    failure {
      echo "❌ 构建或部署失败"
    }
  }
}
