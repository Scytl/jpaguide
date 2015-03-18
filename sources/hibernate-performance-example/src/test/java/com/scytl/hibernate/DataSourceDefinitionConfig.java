package com.scytl.hibernate;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;

@DataSourceDefinition(
    name = "movieDatabase",
    className = "org.hsqldb.jdbc.JDBCDriver",
    url = "jdbc:hsqldb:mem:mydb")
@Singleton
public class DataSourceDefinitionConfig {
}