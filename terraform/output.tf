output "db_instance_master_user_secret_arn" {
  description = "The ARN of the master user secret (Only available when manage_master_user_password is set to true)"
  value       = module.db.db_instance_master_user_secret_arn
}

output "rds_ssm_parameter_name" {
  description = "The name of the SSM parameter for RDS parameters"
  value       = module.rds_params.ssm_parameter_name
}

output "rds_secrets_read_only_policy_arn" {
  description = "The ARN of the RDS secrets"
  value       = aws_iam_policy.rds_secrets_read_only_policy.arn
}

output "rds_params_read_only_policy_arn" {
  description = "The ARN of the RDS params"
  value       = aws_iam_policy.rds_params_read_only_policy.arn
}
