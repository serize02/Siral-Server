package com.siral.data.services

import com.siral.data.database.tables.Students
import com.siral.data.models.Student
import com.siral.data.interfaces.StudentDataSource
import com.siral.responses.StudentData
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class StudentService(private val db: Database): StudentDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getStudentByEmail(email: String): Student? = dbQuery {
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

    override suspend fun insertStudent(student: StudentData): Long = dbQuery {
        Students
            .insert {
                it[name] = student.name
                it[code] = student.code
                it[email] = student.email
                it[resident] = student.resident
            } get Students.id
    }

    override suspend fun getStudentById(studentId: Long): Student? = dbQuery {
        Students
            .select { Students.id eq studentId }
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

    override suspend fun updateStudentLastAndActive(studentId: Long): Unit = dbQuery {
        Students
            .update({ Students.id eq studentId }) {
                it[last] = LocalDateTime.now()
                it[active] = true
            }
    }
}