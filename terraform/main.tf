terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23.0"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

resource "kubernetes_namespace" "workhub" {
  metadata {
    name = "workhub"
  }
}

resource "kubernetes_deployment" "workhub_app" {
  metadata {
    name      = "workhub-app"
    namespace = kubernetes_namespace.workhub.metadata[0].name
    labels = {
      app = "workhub-app"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "workhub-app"
      }
    }

    template {
      metadata {
        labels = {
          app = "workhub-app"
        }
      }

      spec {
        container {
          image = "enterprise_ph3-app:latest"
          name  = "workhub-app"

          port {
            container_port = 8080
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "workhub_service" {
  metadata {
    name      = "workhub-service"
    namespace = kubernetes_namespace.workhub.metadata[0].name
  }

  spec {
    selector = {
      app = "workhub-app"
    }

    port {
      port        = 8080
      target_port = 8080
    }

    type = "NodePort"
  }
}