# --- Layer 1: VPC ---
output "vpc_id" {
  description = "VPC ID."
  value       = module.vpc.vpc_id
}

output "private_subnet_ids" {
  description = "Private subnets - EKS nodes and RDS."
  value       = module.vpc.private_subnets
}

output "public_subnet_ids" {
  description = "Public subnets - for internet-facing load balancers."
  value       = module.vpc.public_subnets
}

output "region" {
  value = var.region
}

output "cluster_name" {
  description = "EKS cluster name."
  value       = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "EKS API server endpoint."
  value       = module.eks.cluster_endpoint
}

output "kubeconfig_command" {
  description = "Command to configure kubectl for this cluster."
  value       = "aws eks update-kubeconfig --name ${module.eks.cluster_name} --region ${var.region}"
}

output "rds_endpoint" {
  description = "RDS hostname (no port). Fed into the chart's externalDatabase.host."
  value       = aws_db_instance.sdsb.address
}

output "db_secret_arn" {
  description = "Secrets Manager ARN holding the DB credentials (for the future ESO wiring)."
  value       = aws_secretsmanager_secret.db.arn
}
