## Endpoints

### Authentication

#### Student Login
- **URL:** `POST /siral/student-login`
- **Request Body:**
    - `email`: User's email.
    - `password`: User's password.
- **Response:**
    - `200 OK`: Returns user details and token.
    - `401 Unauthorized`: Invalid credentials or student not found.

#### Admin Login
- **URL:** `POST /siral/admin-login`
- **Request Body:**
    - `email`: Admin's email.
    - `password`: Admin's password.
- **Response:**
    - `200 OK`: Returns a token.
    - `401 Unauthorized`: Invalid credentials.

#### Site Manager/Scheduler Login
- **URL:** `POST /siral/site-manager-scheduler-login`
- **Request Body:**
    - `email`: User's email.
    - `password`: User's password.
- **Response:**
    - `200 OK`: Returns a token.
    - `401 Unauthorized`: Invalid credentials or user not found.

#### Auth Check
- **URL:** `GET /siral/auth`
- **Response:**
    - `200 OK`: Authentication successful.

### Dining Halls

#### Insert Dining Hall
- **URL:** `POST /siral/dinninghalls/{dinninghallNAME}`
- **Request Parameters:**
    - `dinninghallNAME`: Name of the dining hall to be inserted.
- **Response:**
    - `201 Created`: Dining hall inserted successfully.
    - `400 Bad Request`: Missing dining hall name or dining hall already exists.

#### Delete Dining Hall
- **URL:** `DELETE /siral/dinninghalls/{dinninghallID}`
- **Request Parameters:**
    - `dinninghallID`: ID of the dining hall to be deleted.
- **Response:**
    - `200 OK`: Dining hall deleted successfully.
    - `400 Bad Request`: Missing dining hall ID or dining hall not found.

### User Roles

#### Insert New Role
- **URL:** `POST /siral/insert-new-role`
- **Request Body:**
    - `email`: User's email.
    - `dinninghall`: Dining hall associated with the role.
    - `role`: Role to be assigned.
- **Response:**
    - `200 OK`: User role inserted successfully.
    - `400 Bad Request`: Missing required fields, dining hall not found, invalid role, or user already has a role.

#### Delete Role
- **URL:** `DELETE /siral/delete-role/{email}`
- **Request Parameters:**
    - `email`: Email of the user whose role is to be deleted.
- **Response:**
    - `200 OK`: User role deleted successfully.
    - `400 Bad Request`: Missing email or user not found.

### Schedule

#### Get Schedule
- **URL:** `GET /siral/schedule/{dinninghallID}`
- **Request Parameters:**
    - `dinninghallID`: ID of the dining hall.
- **Response:**
    - `200 OK`: Returns the schedule items.
    - `400 Bad Request`: Missing dining hall ID.
    - `404 Not Found`: Dining hall not found.

#### Insert Schedule Item
- **URL:** `POST /siral/schedule/{schedulerID}`
- **Request Parameters:**
    - `schedulerID`: ID of the scheduler.
- **Request Body:**
    - `date`: Date of the schedule item.
    - `breakfast`: Boolean indicating if breakfast is available.
    - `lunch`: Boolean indicating if lunch is available.
    - `dinner`: Boolean indicating if dinner is available.
- **Response:**
    - `200 OK`: Schedule item inserted successfully.
    - `400 Bad Request`: Missing required fields or invalid date.
    - `404 Not Found`: Dining hall or user not found.

#### Delete Schedule Item
- **URL:** `DELETE /siral/schedule/{schedulerID}`
- **Request Parameters:**
    - `schedulerID`: ID of the scheduler.
- **Request Body:**
    - `date`: Date of the schedule item.
    - `breakfast`: Boolean indicating if breakfast is available.
    - `lunch`: Boolean indicating if lunch is available.
    - `dinner`: Boolean indicating if dinner is available.
- **Response:**
    - `200 OK`: Schedule item deleted successfully.
    - `400 Bad Request`: Missing required fields.
    - `404 Not Found`: Dining hall or user not found.

### Reservations

#### Get Reservations
- **URL:** `GET /siral/reservations/{studentID}`
- **Request Parameters:**
    - `studentID`: ID of the student.
- **Response:**
    - `200 OK`: Returns the reservations.
    - `400 Bad Request`: Missing student ID.

## Security
- **Role-Based Access Control:** Endpoints are protected using roles such as `ADMIN`, `SCHEDULER`, and `SITE_MANAGER`.
- **Token-Based Authentication:** JWT tokens are used for authenticating users.

## Response Messages
- `MISSING_DINNING_HALL_NAME`
- `DINNING_HALL_ALREADY_EXISTS`
- `DINNING_HALL_INSERTED_SUCCESSFULLY`
- `DINNING_HALL_NOT_FOUND`
- `DINNING_HALL_DELETED_SUCCESSFULLY`
- `MISSING_REQUIRED_FIELDS`
- `INVALID_ROLE`
- `USER_ALREADY_HAS_ROLE`
- `USER_INSERTED_SUCCESSFULLY`
- `MISSING_EMAIL`
- `USER_NOT_FOUND`
- `USER_DELETED_SUCCESSFULLY`
- `INVALID_CREDENTIALS`
- `INVALID_STUDENT`
- `INVALID_DATE`
- `ALL_DONE`
