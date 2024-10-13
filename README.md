# Siral Project

## Description
Siral is the backend server for a meal reservation and management application. This project is part of my university internship, where I am gaining practical experience in backend development. The application allows users to manage dining halls, make reservations, and handle user roles efficiently.

## Installation and Usage
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
   
4. Run the application:
    ```sh
    ./gradlew run
    ```

5. The server will start on `http://localhost:8080`.

## Endpoints for Administration Web Interface

### Authentication

#### Admin, Scheduler and Site Manager Login
- **URL**: `POST /siral/admin-login`
- **Description**: Authenticates an admin and returns a JWT token.
- **Request Body**:
    ```json
    {
        "email": "admin@example.com",
        "password": "password123"
    }
    ```
  - **Response**:
      - `200 OK` with JWT token if successful
      - `401 Unauthorized` if credentials are invalid
      - For Admin Login (data is null in this case):
      ```json
      {
          "success": true,
          "data": null,
          "message": "USER_LOGED_SUCCESSFULLY",
          "status": 200,
          "role": "ADMIN",
          "token": "jwt_token_here"
      }
      ```
      - For Scheduler or Site Manager:
      ```json
        {
            "success": true,
            "data": {
                "id": 1,
                "email": "scheduler@gmail.com",
                "dinningHallID": 1,
                "role": "SCHEDULER"
            },
            "message": "USER_LOGED_SUCCESSFULLY",
            "status": 200,
            "role": "SCHEDULER",
            "token": "jwt_token_here"
        }
     ```
#### Insert Dining Hall
- **URL**: `POST /siral/dinninghalls/{dinninghallNAME}`
- **Description**: Inserts a new dining hall.
- **Permissions**: Admin
- **Request Parameters**:
    - `dinninghallNAME` (path parameter)
- **Response**:
    - `403 Forbidden` if user is not Admin
    - `400 Bad Request` if the dining hall already exists or the name is missing
    - `200 OK` if successful
    ```json
    {
        "success": true,
        "data": {
            "id": 1,
            "name": "Dining Hall Name"
        },
        "message": "DINNING_HALL_INSERTED_SUCCESSFULLY",
        "status": 200
    }
    ```

#### Delete Dining Hall
- **URL**: `DELETE /siral/dinninghalls/{dinninghallID}`
- **Description**: Deletes an existing dining hall.
- **Permissions**: Admin
- **Request Parameters**:
    - `dinninghallID` (path parameter)
- **Response**:
    - `403 Forbidden` if user is not Admin
    - `200 OK` if successful
    - `400 Bad Request` if the dining hall ID is missing or not found
    ```json
    {
        "success": true,
        "data": null,
        "message": "DINNING_HALL_DELETED_SUCCESSFULLY",
        "status": 200
    }
    ```

#### Insert New Scheduler or Site Manager
- **URL**: `POST /siral/insert-new-role`
- **Description**: Inserts a new role for a user.
- **Permissions**: Admin
- **Request Body**:
    ```json
    {
        "email": "user@example.com",
        "role": "SCHEDULER",
        "dinninghall": "Dining Hall Name"
    }
    ```
- **Response**:
    - `200 OK` if successful
    - `400 Bad Request` if required fields are missing or the role is invalid
    ```json
    {
        "success": true,
        "data": {
            "id": 1,
            "email": "user@example.com",
            "dinninghallID": 1,
            "role": "SCHEDULER"
        },
        "message": "USER_INSERTED_SUCCESSFULLY",
        "status": 200
    }
    ```

#### Delete Scheduler or Site Manager
- **URL**: `DELETE /siral/delete-role/{email}`
- **Description**: Deletes an existing role for a user.
- **Permissions**: Admin
- **Request Parameters**:
    - `email` (path parameter)
- **Response**:
    - `200 OK` if successful
    - `400 Bad Request` if the email is missing or the user is not found
    ```json
    {
        "success": true,
        "data": null,
        "message": "USER_DELETED_SUCCESSFULLY",
        "status": 200
    }
    ```

#### Insert Schedule Item
- **URL**: `POST /siral/schedule/{schedulerID}`
- **Description**: Inserts a new schedule item.
- **Permissions**: Scheduler
- **Request Parameters**:
    - `schedulerID` (path parameter)
- **Request Body**:
    ```json
    {
        "date": "2024-10-25",
        "breakfast": true,
        "lunch": true,
        "dinner": true
    }
    ```
- **Response**:
    - `200 OK` if successful
    - `400 Bad Request` if required fields are missing or the date is invalid
    ```json
    {
        "success": true,
        "data": null,
        "message": "ALL_DONE",
        "status": 200
    }
    ```

#### Delete Schedule Item
- **URL**: `DELETE /siral/schedule/{schedulerID}`
- **Description**: Deletes an existing schedule item.
- **Permissions**: Scheduler
- **Request Parameters**:
    - `schedulerID` (path parameter)
- **Request Body**:
    ```json
    {
        "date": "2023-10-01",
        "breakfast": true,
        "lunch": true,
        "dinner": true
    }
    ```
- **Response**:
    - `200 OK` if successful
    - `400 Bad Request` if required fields are missing or the item is not found
    ```json
    {
        "success": true,
        "data": null,
        "message": "ALL_DONE",
        "status": 200
    }
    ```

#### Update Schedule Availability
- **URL**: `PUT /siral/schedule/availability/{schedulerID}/{days}`
- **Description**: Updates the number of days before a schedule item becomes available.
- **Permissions**: Scheduler
- **Request Parameters**:
    - `schedulerID` (path parameter)
    - `days` (path parameter)
- **Response**:
    - `200 OK` if successful
    - `400 Bad Request` if required fields are missing or the days are invalid -> valid-days(2,7,15,30)
    ```json
    {
        "success": true,
        "data": null,
        "message": "ALL_DONE",
        "status": 200
    }
    ```

#### Get Logs
- **URL**: `GET /siral/logs`
- **Description**: Retrieves all logs .
- **Permissions**: Admin, Scheduler, Site Manager
- **Response**:
    - `200 OK` with list of logs if successful
    ```json
    {
        "success": true,
        "data": [
            {
                "id": 1,
                "email": "admin@example.com",
                "action": "INSERT_DINNING_HALL",
                "status": "SUCCESSFUL",
                "timestamp": "2023-10-01T12:00:00"
            }
        ],
        "message": "DATA_RETRIEVED_SUCCESSFULLY",
        "status": 200
    }
    ```

#### Get Data for Stats
- **URL**: `GET /siral/stats-data`
- **Description**: Retrieves data for plot.
- **Permissions**: Admin, Scheduler, Site Manager
- **Response**:
    - `200 OK` with list of data if successful
    ```json
    {
        "success": true,
        "data": [
            {
                "date": "2023-10-01",
                "dinningHall": "Dining Hall Name",
                "reservations": 100
            }
        ],
        "message": "DATA_RETRIEVED_SUCCESSFULLY",
        "status": 200
    }
    ```

#### Get All Administration Personal
- **URL**: `GET /siral/administration`
- **Description**: Retrieves all the schedulers, site managers and admin.
- **Permissions**: Admin, Scheduler, Site Manager
- **Response**:
- `200 OK` with list of data if successful
  ```json
  {
      "success": true,
      "data": [
          {
              "id": 4,
              "email": "scheduler-central@uclv.cu",
              "dinninghallID": 9,
              "role": "SCHEDULER"
          },
          {
              "id": 5,
              "email": "scheduler-fajardo@uclv.cu",
              "dinninghallID": 10,
              "role": "SCHEDULER"
          },
          {
              "id": 6,
              "email": "scheduler-camilitos@uclv.cu",
              "dinninghallID": 11,
              "role": "SCHEDULER"
          },
          {
              "id": 7,
              "email": "scheduler-varela@uclv.cu",
              "dinninghallID": 12,
              "role": "SCHEDULER"
          }
      ],
      "message": "DATA_RETRIEVED_SUCCESSFULLY",
      "status": 200
  }
  ```

#### Get All Students
- **URL**: `GET /siral/students`
- **Description**: Retrieves all the students.
- **Permissions**: Admin, Scheduler, Site Manager
- **Response**:
- `200 OK` with list of data if successful
  ```json
  {
      "success": true,
      "data": [
          {
              "id": 1,
              "name": "John Doe",
              "code": 123123,
              "email": "eserize@uclv.cu",
              "resident": true,
              "last": "2024-10-12T21:43:58.950543",
              "active": true
          },
          {
              "id": 2,
              "name": "John Doe",
              "code": 12345,
              "email": "student@example.com",
              "resident": true,
              "last": "2024-10-12T21:45:53.242814",
              "active": true
          }
      ],
      "message": "DATA_RETRIEVED_SUCCESSFULLY",
      "status": 200
  }
  ```

#### Get All Dinning Halls
- **URL**: `GET /siral/dinninghalls`
- **Description**: Retrieves all the dinning halls.
- **Permissions**: Admin, Scheduler, Site Manager
- **Response**:
- `200 OK` with list of data if successful
  ```json
  {
      "success": true,
      "data": [
          {
              "id": 1,
              "name": "Central"
          },
          {
              "id": 2,
              "name": "Camilitos"
          }
      ],
      "message": "DATA_RETRIEVED_SUCCESSFULLY",
      "status": 200
  }
  ```

#### Get All Reservations
- **URL**: `GET /siral/reservations`
- **Description**: Retrieves all the reservations.
- **Permissions**: Admin, Scheduler, Site Manager
- **Response**:
- `200 OK` with list of data if successful
  ```json
  {
      "success": true,
      "data": [
          {
              "id": 6,
              "studentID": 1,
              "scheduleItemID": 30,
              "dateOfReservation": "2024-10-12T16:44:46.147406"
          },
          {
              "id": 7,
              "studentID": 1,
              "scheduleItemID": 31,
              "dateOfReservation": "2024-10-12T16:46:37.475581"
          }
      ],
      "message": "DATA_RETRIEVED_SUCCESSFULLY",
      "status": 200
  }
  ```

## Endpoints for Student Interface

#### Student Login
- **URL**: `POST /siral/student-login`
- **Description**: Authenticates a student and returns a JWT token.
- **Request Body**:
    ```json
    {
        "email": "student@example.com",
        "password": "password123"
    }
    ```
- **Response**:
  - `200 OK` with `StudentLoginData` if successful
  - `401 Unauthorized` if credentials are invalid
    ```json
    {
        "success": true,
        "data": {
            "id": 1,
            "name": "John Doe",
            "code": 1234,
            "email": "student@example.com",
            "resident": true,
            "last": "2024-10-07 22:30:33.005274",
            "active": true
        },
        "message": "USER_LOGED_SUCCESSFULLY",
        "status": 200,
        "role": "STUDENT",
        "token": "jwt_token_here"
    }
    ```

### Reservations

#### Make Reservation
- **URL**: `POST /siral/reservations/{studentID}/{scheduleItemID}`
- **Description**: Makes a reservation for a student.
- **Permissions**: Student
- **Request Parameters**:
    - `studentID` (path parameter)
    - `scheduleItemID` (path parameter)
- **Response**:
    - `200 OK` if successful
    - `400 Bad Request` if the reservation already exists or required fields are missing
    ```json
    {
        "success": true,
        "data": {
            "id": 1,
            "studentID": 1,
            "scheduleItemID": 1
        },
        "message": "RESERVATION_MADE",
        "status": 200
    }
    ```

#### Delete Reservation
- **URL**: `DELETE /siral/reservations/{studentID}/{reservationID}`
- **Description**: Deletes an existing reservation.
- **Permissions**: Student
- **Request Parameters**:
    - `studentID` (path parameter)
    - `reservationID` (path parameter)
- **Response**:
    - `200 OK` if successful
    - `400 Bad Request` if the reservation ID is missing or not found
    ```json
    {
        "success": true,
        "data": null,
        "message": "RESERVATION_DELETED",
        "status": 200
    }
    ```

#### Get Student Reservations
- **URL**: `GET /siral/reservations/{studentID}`
- **Description**: Retrieves all reservations for a student.
- **Permissions**: Student
- **Request Parameters**:
    - `studentID` (path parameter)
- **Response**:
    - `200 OK` with list of reservations if successful
    - `400 Bad Request` if required fields are missing
    ```json
    {
        "success": true,
        "data": [
            {
              "id": 6,
              "studentID": 1,
              "scheduleItemID": 30,
              "dateOfReservation": "2024-10-12T16:44:46.147406"
            },
            {
              "id": 7,
              "studentID": 1,
              "scheduleItemID": 31,
              "dateOfReservation": "2024-10-12T16:44:46.147406"
            }
        ],
        "message": "DATA_RETREIVED_SUCCESSFULLY",
        "status": 200
    }
    ```
#### Get Schedule
- **URL**: `GET /siral/schedule/{dinninghallID}`
- **Description**: Retrieves the schedule for a dining hall (all the items, included the ones that are not available).
- **Request Parameters**:
    - `dinninghallID` (path parameter)
- **Response**:
    - `200 OK` with schedule items if successful
    - `400 Bad Request` if required fields are missing
    - `404 Not Found` if the dining hall is not found
    ```json
    {
        "success": true,
        "data": [
            {
                "id": 1,
                "date": "2023-10-01",
                "meal": "breakfast",
                "dinninghallID": 1
            }
        ],
        "message": "DATA_RETREIVED_SUCCESSFULLY",
        "status": 200
    }
    ```

#### Get All Meals Available For a Date
- **URL**: `GET /siral/schedule/{dinninghallID}/{date}`
- **Description**: Retrieves all the meals for a date in a dinning hall that are available.
- **Request Parameters**:
  - `dinninghallID` (path parameter)
  - `date` (path parameter)
- **Response**:
  - `200 OK` with schedule items if successful
  - `400 Bad Request` if required fields are missing or date format is invalid
    - `404 Not Found` if the dining hall is not found
  ```json
  {
      "success": true,
      "data": [
          {
              "id": 1,
              "date": "2023-10-01",
              "time": "breakfast",
              "dinninghallID": 1,
              "available": true 
          },
          {
              "id": 2,
              "date": "2023-10-01",
              "time": "lunch",
              "dinninghallID": 1,
              "available": true 
          } 
      ],
      "message": "DATA_RETREIVED_SUCCESSFULLY",
      "status": 200
  }
  ```


## Server Efficiency

The server is designed to handle multiple requests concurrently. It uses a thread pool to manage the requests efficiently. The server is also designed to handle errors gracefully and return appropriate status codes.

![Alt text](https://github.com/serize02/Siral-Server/blob/main/postman-tests/siral-server-eficiency.gif)

## Technologies
- **Ktor**: A framework for building asynchronous servers and clients in connected systems. It is used to create the web server.
- **Gradle**: A build automation tool used for dependency management and project build tasks.
- **PostgreSQL**: An advanced open-source relational database. It is used to store and manage the application's data.
- **Exposed**: A lightweight SQL library on top of JDBC. It is used for database operations.
- **JWT Authentication**: A method for securely transmitting information between parties as a JSON object. It is used for securing the application endpoints.