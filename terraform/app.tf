provider "kubernetes" {
  host                   = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "aws"
    args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name, "--region", var.region]
  }
}

provider "helm" {
  kubernetes {
    host                   = module.eks.cluster_endpoint
    cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
    exec {
      api_version = "client.authentication.k8s.io/v1beta1"
      command     = "aws"
      args        = ["eks", "get-token", "--cluster-name", module.eks.cluster_name, "--region", var.region]
    }
  }
}

resource "kubernetes_namespace" "sdsb" {
  metadata {
    name = var.name
  }
}

resource "kubernetes_secret" "gitlab_registry" {
  metadata {
    name      = "gitlab-registry"
    namespace = kubernetes_namespace.sdsb.metadata[0].name
  }
  type = "kubernetes.io/dockerconfigjson"
  data = {
    ".dockerconfigjson" = jsonencode({
      auths = {
        "registry.gitlab.com" = {
          username = var.gitlab_username
          password = var.gitlab_token
          auth     = base64encode("${var.gitlab_username}:${var.gitlab_token}")
        }
      }
    })
  }
}

resource "helm_release" "sdsb" {
  name      = var.name
  namespace = kubernetes_namespace.sdsb.metadata[0].name
  chart     = "${path.module}/../charts/simpledatascraperbot"

  values = [yamlencode({
    mysql = { enabled = false }
    externalDatabase = {
      host = aws_db_instance.sdsb.address # hostname only; the chart appends :3306
      port = 3306
    }
    app = {
      actuator = { enabled = true }
      bot      = { name = var.bot_name, token = var.bot_token }
      db = {
        username = aws_db_instance.sdsb.username
        password = random_password.db.result
        database = aws_db_instance.sdsb.db_name
      }
    }
    service          = { enabled = true, type = "ClusterIP", port = 80 }
    imagePullSecrets = [{ name = kubernetes_secret.gitlab_registry.metadata[0].name }]
  })]

  depends_on = [module.eks]
}
