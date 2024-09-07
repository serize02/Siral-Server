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
    ```sh
    export dbuser=your_db_user
    export dbpassword=your_db_password
    export jwt-secret=your_jwt_secret
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
- **Insert Dining Hall**: `POST /siral/dinninghalls/{dinninghallNAME}`
- **Delete Dining Hall**: `DELETE /siral/dinninghalls/{dinninghallID}`
- **Insert New Role**: `POST /siral/insert-new-role`
- **Delete Role**: `DELETE /siral/delete-role/{email}`


## Technologies
- **Ktor**: A framework for building asynchronous servers and clients in connected systems. It is used to create the web server.
- **Gradle**: A build automation tool used for dependency management and project build tasks.
- **PostgreSQL**: An advanced open-source relational database. It is used to store and manage the application's data.
- **Exposed**: A lightweight SQL library on top of JDBC. It is used for database operations.
- **JWT Authentication**: A method for securely transmitting information between parties as a JSON object. It is used for securing the application endpoints.
