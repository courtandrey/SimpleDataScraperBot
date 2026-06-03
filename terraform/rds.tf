resource "random_password" "db" {
  length  = 24
  special = false
}

resource "aws_db_subnet_group" "sdsb" {
  name       = "${var.name}-db"
  subnet_ids = module.vpc.private_subnets
}

resource "aws_security_group" "rds" {
  name        = "${var.name}-rds"
  description = "Allow MySQL from EKS nodes only"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description     = "MySQL from EKS nodes"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "sdsb" {
  identifier     = "${var.name}-mysql"
  engine         = "mysql"
  engine_version = "8.0"
  instance_class = "db.t4g.micro"

  allocated_storage = 20
  storage_type      = "gp3"
  storage_encrypted = true

  db_name  = "simple_data_storage"
  username = "sdsb"
  password = random_password.db.result

  db_subnet_group_name   = aws_db_subnet_group.sdsb.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false

  multi_az            = false
  skip_final_snapshot = true
  deletion_protection = false
  backup_retention_period = 1
}

resource "aws_secretsmanager_secret" "db" {
  name = "${var.name}/db"
}

resource "aws_secretsmanager_secret_version" "db" {
  secret_id = aws_secretsmanager_secret.db.id
  secret_string = jsonencode({
    username = aws_db_instance.sdsb.username
    password = random_password.db.result
    host     = aws_db_instance.sdsb.address
    port     = 3306
    dbname   = aws_db_instance.sdsb.db_name
  })
}
