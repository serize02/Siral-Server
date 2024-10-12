# Siral Project

## Description
Siral is the backend server for a meal reservation and management application. This project is part of my university internship, where I am gaining practical experience in backend development. The application allows users to manage dining halls, make reservations, and handle user roles efficiently.

## Installation
1. Clone the repository:
    ```sh
    git clone https://github.com/serize02/siral.git
    cd siral
    ```

2. Set up environment variables:
   Create a file named `.env` in the root of your project and add the following variables:
    ```plaintext
    DB_USER=your_db_user
    DB_PASSWORD=your_db_password
    DB_HOST="jdbc:postgresql://localhost:5432/DB_NAME"
    ADMIN_EMAIL=your_admin_email
    ADMIN_PASSWORD=your_admin_password
    JWT_SECRET=your_jwt_secret
    ```

3. Build the project using Gradle:
    ```sh
    ./gradlew build
    ```

## Usage
1. Run the application:
    ```sh
    ./gradlew run
    ```

2. The server will start on `http://localhost:8080`.

## Endpoints
### Admin-Scheduler-SiteManger
- **Insert Dining Hall**: `POST /siral/dinninghalls/{dinninghallNAME}`
   - **Description**: Inserts a new dining hall.
   - **Parameters**: `dinninghallNAME` (path parameter)
   - **Response**:
        - `403 Forbidden` if user is not Admin
            ```json
                "ACCESS_DENIED"
            ```
        - `400 Bad Request` if the dining hall already exists
             ```json
                "DINNING_HALL_ALREADY_EXISTS"
             ```  
          or the name is missing
             ```json
                "MISSING_DINNING_HALL_NAME"
             ```
            
        - `201 Created` if successful
            ```json
                "DINNING_HALL_INSERTED_SUCCESSFULLY"
            ```

- **Delete Dining Hall**: `DELETE /siral/dinninghalls/{dinninghallID}`
   - **Description**: Deletes an existing dining hall.
   - **Parameters**: `dinninghallID` (path parameter)
   - **Response**:
        - `403 Forbidden` if user is not Admin
          ```json
              "ACCESS_DENIED"
          ```
        - `200 OK` if successful
        - `400 Bad Request` if the dining hall ID is missing or not found

### User Roles
- **Insert New Role**: `POST /siral/insert-new-role`
   - **Description**: Inserts a new role for a user.
   - **Request Body**: `NewRoleCredentials` (JSON)
   - **Response**:
      - `200 OK` if successful
      - `400 Bad Request` if required fields are missing or the role is invalid

- **Delete Role**: `DELETE /siral/delete-role/{email}`
   - **Description**: Deletes an existing role for a user.
   - **Parameters**: `email` (path parameter)
   - **Response**:
      - `200 OK` if successful
      - `400 Bad Request` if the email is missing or the user is not found

### Authentication
- **Student Login**: `POST /siral/student-login`
   - **Description**: Authenticates a student and returns a JWT token.
   - **Request Body**: `AuthCredentials` (JSON)
   - **Response**:
      - `200 OK` with `StudentLoginData` if successful
      - `401 Unauthorized` if credentials are invalid

- **Admin Login**: `POST /siral/admin-login`
   - **Description**: Authenticates an admin and returns a JWT token.
   - **Request Body**: `AuthCredentials` (JSON)
   - **Response**:
      - `200 OK` with JWT token if successful
      - `401 Unauthorized` if credentials are invalid

- **Site Manager/Scheduler Login**: `POST /siral/site-manager-scheduler-login`
   - **Description**: Authenticates a site manager or scheduler and returns a JWT token.
   - **Request Body**: `AuthCredentials` (JSON)
   - **Response**:
      - `200 OK` with JWT token if successful
      - `401 Unauthorized` if credentials are invalid

### Reservations
- **Make Reservation**: `POST /siral/reservations/{studentID}/{scheduleItemID}`
   - **Description**: Makes a reservation for a student.
   - **Parameters**: `studentID` and `scheduleItemID` (path parameters)
   - **Response**:
      - `200 OK` if successful
      - `400 Bad Request` if the reservation already exists or required fields are missing

- **Delete Reservation**: `DELETE /siral/reservations/{studentID}/{reservationID}`
   - **Description**: Deletes an existing reservation.
   - **Parameters**: `studentID` and `reservationID` (path parameters)
   - **Response**:
      - `200 OK` if successful
      - `400 Bad Request` if the reservation ID is missing or not found

- **Get Student Reservations**: `GET /siral/reservations/{studentID}`
   - **Description**: Retrieves all reservations for a student.
   - **Parameters**: `studentID` (path parameter)
   - **Response**:
      - `200 OK` with list of reservations if successful
      - `400 Bad Request` if required fields are missing

### Schedule
- **Get Schedule**: `GET /siral/schedule/{dinninghallID}`
   - **Description**: Retrieves the schedule for a dining hall.
   - **Parameters**: `dinninghallID` (path parameter)
   - **Response**:
      - `200 OK` with schedule items if successful
      - `400 Bad Request` if required fields are missing
      - `404 Not Found` if the dining hall is not found

- **Insert Schedule Item**: `POST /siral/schedule/{schedulerID}`
   - **Description**: Inserts a new schedule item.
   - **Parameters**: `schedulerID` (path parameter)
   - **Request Body**: `ScheduleItemRequest` (JSON)
   - **Response**:
      - `200 OK` if successful
      - `400 Bad Request` if required fields are missing or the date is invalid

- **Delete Schedule Item**: `DELETE /siral/schedule/{schedulerID}`
   - **Description**: Deletes an existing schedule item.
   - **Parameters**: `schedulerID` (path parameter)
   - **Request Body**: `ScheduleItemRequest` (JSON)
   - **Response**:
      - `200 OK` if successful
      - `400 Bad Request` if required fields are missing or the item is not found

- **Update Schedule Availability**: `PUT /siral/schedule/availability/{schedulerID}/{days}`
   - **Description**: Updates the number of days before a schedule item becomes available.
   - **Parameters**: `schedulerID` and `days` (path parameters)
   - **Response**:
      - `200 OK` if successful
      - `400 Bad Request` if required fields are missing or the days are invalid

### Logs
- **Get Logs**: `GET /siral/logs`
    - **Description**: Retrieves all logs.
    ```kotlin
        data class Log(
            val id: Long,
            val email: String,
            val action: String,
            val status: String,
            val timestamp: LocalDateTime
        )
    ```
    - **Response**:
        - `200 OK` with list of logs if successful

### Data
- **Get Data**: `GET /siral/data`
    - **Description**: Retrieves data for plot.
    ```kotlin
        data class Data(
            val date: LocalDate,
            val dinningHall: String,
            val reservation: Long
        )
    ```
    - **Response**:
        - `200 OK` with list of data if successful

## Server Eficiency

The server is designed to handle multiple requests concurrently. It uses a thread pool to manage the requests efficiently. The server is also designed to handle errors gracefully and return appropriate status codes.

![Alt text](https://github.com/serize02/Siral-Server/blob/main/postman-tests/siral-server-eficiency.gif)

## Technologies
- **Ktor**: A framework for building asynchronous servers and clients in connected systems. It is used to create the web server.
- **Gradle**: A build automation tool used for dependency management and project build tasks.
- **PostgreSQL**: An advanced open-source relational database. It is used to store and manage the application's data.
- **Exposed**: A lightweight SQL library on top of JDBC. It is used for database operations.
- **JWT Authentication**: A method for securely transmitting information between parties as a JSON object. It is used for securing the application endpoints.