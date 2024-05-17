locals {
  name     = "ordersdb"
  username = "master"
  port     = 5432
}

data "terraform_remote_state" "tech-challenge" {
  backend = "s3"

  config = {
    bucket = "fiap-3soat-g15-iac-tech-challenge"
    key    = "live/terraform.tfstate"
    region = var.region
  }
}

module "db" {
  source  = "terraform-aws-modules/rds/aws"
  version = "6.5.2"

  identifier = local.name

  engine               = "postgres"
  engine_version       = "15"
  family               = "postgres15"
  major_engine_version = "15"
  instance_class       = "db.t3.micro"

  allocated_storage     = 10
  max_allocated_storage = 20

  storage_encrypted = false

  db_name  = local.name
  username = local.username
  port     = local.port

  manage_master_user_password = true

  multi_az               = false
  db_subnet_group_name   = data.terraform_remote_state.tech-challenge.outputs.database_subnet_group_name
  vpc_security_group_ids = [module.security_group.security_group_id]

  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false
}

module "security_group" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name   = local.name
  vpc_id = data.terraform_remote_state.tech-challenge.outputs.vpc_id

  ingress_with_cidr_blocks = [
    {
      from_port   = local.port
      to_port     = local.port
      protocol    = "tcp"
      cidr_blocks = data.terraform_remote_state.tech-challenge.outputs.vpc_cidr_block
    },
  ]
}

module "rds_params" {
  source = "terraform-aws-modules/ssm-parameter/aws"
  name   = "/live/orders/db"
  type   = "String"

  value = jsonencode({
    name : local.name,
    endpoint : module.db.db_instance_endpoint,
    port : local.port
  })
}

resource "aws_iam_policy" "rds_secrets_read_only_policy" {
  name = "OrdersRDSSecretsReadOnlyPolicy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:DescribeSecret",
          "secretsmanager:GetSecretValue"
        ],
        Resource = module.db.db_instance_master_user_secret_arn
      }
    ]
  })
}

resource "aws_iam_policy" "rds_params_read_only_policy" {
  name = "OrdersRDSParamsReadOnlyPolicy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ssm:GetParameter",
          "ssm:GetParameters"
        ],
        Resource = module.rds_params.ssm_parameter_arn
      }
    ]
  })
}
