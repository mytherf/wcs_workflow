- Q: Client does not support authentication protocol requested by server. plugin type was = 'sha256_password'
  
  A: https://dev.mysql.com/doc/refman/5.6/en/old-client.html

- Q: SELECT command denied to user 'workflow'@'localhost' for table 'user_variables_by_thread'

  A: GRANT SELECT ON performance_schema.user_variables_by_thread TO 'workflow'@'%'