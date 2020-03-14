/**
* Sclera - Tests
* Copyright 2012 - 2020 Sclera, Inc.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.scleradb.plugin.analytics.sequence.matcher.test

import org.scalatest.CancelAfterFailure
import org.scalatest.funspec.AnyFunSpec

import java.util.Properties
import java.sql.{Connection, DriverManager, Statement, ResultSet, SQLException}

import com.scleradb.sqltests.runner.SqlTestRunner

class SeqMatcherTestSuite
extends AnyFunSpec with CancelAfterFailure with SqlTestRunner {
    val jdbcUrl: String = "jdbc:scleradb"

    val props: Properties = new Properties()
    props.setProperty("schemaDbms", "H2MEM")
    props.setProperty("schemaDb", "scleratests")
    props.setProperty("tempDb", "scleratests")

    var conn: java.sql.Connection = null
    var stmt: java.sql.Statement = null

    describe("JDBC driver") {
        it("should return a valid connection") {
            conn = DriverManager.getConnection(jdbcUrl, props)
        }

        it("should create a valid statement") {
            stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                                        java.sql.ResultSet.CONCUR_READ_ONLY)
            stmt.executeUpdate("create schema")
        }

        it("should execute SEQ script") {
            runScript(stmt, "/scripts/seq.out")
        }

        it("should close the statement") {
            stmt.close()

            val e: Throwable = intercept[java.sql.SQLException] {
                stmt.executeQuery("select 1::int as foo;")
            }

            assert(e.getMessage() === "Statement closed")
        }

        it("should close the connection") {
            conn.close()

            val e: Throwable = intercept[java.sql.SQLException] {
                conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                                     java.sql.ResultSet.CONCUR_READ_ONLY)
            }

            assert(e.getMessage() === "Connection closed")
        }
    }
}
