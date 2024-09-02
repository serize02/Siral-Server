# Siral Server Application - API Endpoints

## Overview
This document provides a detailed overview of the API endpoints available in the Siral Server Application. The application is built using Kotlin and Ktor and manages student reservations for dining halls.

## Endpoints

### Authentication
- **POST** `/login`
    - **Description**: Authenticates a user and returns a JWT token.
    - **Request Body**:
      ```json
      {
        "username": "string",
        "password": "string"
      }
      ```
    - **Response**:
      ```json
      {
        "token": "string"
      }
      ```

### Reservations
- **POST** `/siral/reservations/{scheduleItemID}`
    - **Description**: Creates a reservation for the authenticated student.
    - **Path Parameters**:
        - `scheduleItemID`: ID of the schedule item to reserve.
    - **Response**:
        - `200 OK`: Reservation made.
        - `400 Bad Request`: Missing or invalid schedule item ID, or reservation already exists.
        - `401 Unauthorized`: Access denied.
        - `500 Internal Server Error`: User ID or role not found in token.

- **DELETE** `/siral/reservations/{reservationID}`
    - **Description**: Deletes a reservation for the authenticated student.
    - **Path Parameters**:
        - `reservationID`: ID of the reservation to delete.
    - **Response**:
        - `200 OK`: Reservation deleted.
        - `400 Bad Request`: Missing or invalid reservation ID.
        - `401 Unauthorized`: Access denied.
        - `500 Internal Server Error`: User ID or role not found in token.

- **GET** `/siral/reservations`
    - **Description**: Retrieves all reservations for the authenticated student.
    - **Response**:
        - `200 OK`: List of reservations.
        - `401 Unauthorized`: Access denied.
        - `500 Internal Server Error`: User ID or role not found in token.

### Schedule
- **GET** `/siral/schedule`
    - **Description**: Retrieves the schedule of available dining hall slots.
    - **Response**:
        - `200 OK`: List of schedule items.

### Dining Halls
- **POST** `/siral/dinninghalls`
    - **Description**: Inserts a new dining hall.
    - **Request Body**:
      ```json
      {
        "name": "string"
      }
      ```
    - **Response**:
        - `200 OK`: Dining hall inserted.
        - `400 Bad Request`: Invalid request body.

- **DELETE** `/siral/dinninghalls/{dinninghallID}`
    - **Description**: Deletes a dining hall.
    - **Path Parameters**:
        - `dinninghallID`: ID of the dining hall to delete.
    - **Response**:
        - `200 OK`: Dining hall deleted.
        - `400 Bad Request`: Missing or invalid dining hall ID.

### Roles
- **POST** `/siral/roles`
    - **Description**: Inserts a new role.
    - **Request Body**:
      ```json
      {
        "role": "string"
      }
      ```
    - **Response**:
        - `200 OK`: Role inserted.
        - `400 Bad Request`: Invalid request body.

- **DELETE** `/siral/roles/{roleID}`
    - **Description**: Deletes a role.
    - **Path Parameters**:
        - `roleID`: ID of the role to delete.
    - **Response**:
        - `200 OK`: Role deleted.
        - `400 Bad Request`: Missing or invalid role ID.

