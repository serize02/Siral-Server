package com.siral.data.services

import com.siral.data.database.tables.Students
import com.siral.data.models.Student
import com.siral.data.interfaces.StudentDataSource
import com.siral.responses.StudentData
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

class StudentService(private val db: Database): StudentDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<Student> = dbQuery {
        Students
            .selectAll()
            .map{
                Student(
                    id = it[Students.id],
                    name = it[Students.name],
                    code = it[Students.code],
                    email = it[Students.email],
                    resident = it[Students.resident],
                    last = it[Students.last],
                    active = it[Students.active]
                )
            }
    }

    override suspend fun getById(id: Long): Student? = dbQuery {
        Students
            .select { Students.id eq id }
            .map{
                Student(
                    id = it[Students.id],
                    name = it[Students.name],
                    code = it[Students.code],
                    email = it[Students.email],
                    resident = it[Students.resident],
                    last = it[Students.last],
                    active = it[Students.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun getByEmail(email: String): Student? = dbQuery {
        Students
            .select { Students.email eq email}
            .map{
                Student(
                    id = it[Students.id],
                    name = it[Students.name],
                    code = it[Students.code],
                    email = it[Students.email],
                    resident = it[Students.resident],
                    last = it[Students.last],
                    active = it[Students.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun create(student: StudentData): Long = dbQuery {
        Students
            .insert {
                it[name] = student.name
                it[code] = student.code
                it[email] = student.email
                it[resident] = student.resident
            } get Students.id
    }

    override suspend fun updateStudentLastAndActive(id: Long): Unit = dbQuery {
        Students
            .update({ Students.id eq id }) {
                it[last] = LocalDateTime.now()
                it[active] = true
            }
    }

    override suspend fun delete(id: Long): Unit = dbQuery {
        Students.deleteWhere { Students.id eq id}
    }

}