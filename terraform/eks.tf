module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 21.0"

  name               = var.name
  kubernetes_version = var.kubernetes_version

  endpoint_public_access       = true
  endpoint_public_access_cidrs = [var.admin_cidr]

  enable_cluster_creator_admin_permissions = true

  vpc_id     = module.vpc.vpc_id
  subnet_ids = module.vpc.private_subnets

  addons = {
    coredns                = {}
    kube-proxy             = {}
    vpc-cni                = { before_compute = true }
    eks-pod-identity-agent = { before_compute = true }
  }

  eks_managed_node_groups = {
    default = {
      instance_types = ["t3.medium", "m5.large", "c5.large"]
      capacity_type  = "SPOT"

      ami_type = "AL2023_x86_64_STANDARD"

      min_size     = 1
      max_size     = 3
      desired_size = 2

      labels = { workload = "general" }
    }
  }

  tags = var.tags
}
