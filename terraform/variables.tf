variable "region" {
  description = "AWS region."
  type        = string
  default     = "eu-north-1"
}

variable "name" {
  description = "Common name prefix for all resources."
  type        = string
  default     = "sdsb"
}

variable "vpc_cidr" {
  description = "CIDR for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "az_count" {
  description = "Number of AZs to spread subnets across (EKS wants >=2)."
  type        = number
  default     = 3
}

variable "kubernetes_version" {
  description = "EKS control plane version. Keep current to avoid the +$0.60/hr extended-support fee."
  type        = string
  default     = "1.33"
}

variable "admin_cidr" {
  description = "CIDR allowed to reach the PUBLIC EKS API endpoint. SET THIS to <your-ip>/32. 0.0.0.0/0 exposes the API server to the world (still auth-gated, but bad practice)."
  type        = string
  default     = "0.0.0.0/0"
}

variable "bot_token" {
  description = "Telegram BOT_TOKEN."
  type        = string
  sensitive   = true
}

variable "bot_name" {
  description = "Telegram BOT_NAME."
  type        = string
}

variable "gitlab_username" {
  description = "GitLab user (or deploy-token name) for pulling the private image."
  type        = string
}

variable "gitlab_token" {
  description = "GitLab token with read_registry scope."
  type        = string
  sensitive   = true
}

variable "tags" {
  description = "Tags applied to everything (cost allocation)."
  type        = map(string)
  default = {
    Project   = "sdsb"
    ManagedBy = "terraform"
  }
}
