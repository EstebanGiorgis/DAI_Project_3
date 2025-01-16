
# Enrollment API

The enrollments API allows to manage enrollments (link between a user and a subject, containing their respective grades). It uses the HTTP protocol and the JSON format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new enrollment
- Add a grade to an enrollment
- Delete an enrollment
- Get an overview of all enrollments for a user

## Endpoints

### Create a new enrollment

- `POST /enrollment/users/{userId}/subjects/{subjectId}`

Create a new enrollment.

#### Request

Body is empty, only userId and subjectId are specified in url

#### Response

The response body contains a JSON object with the following properties:

- `userId` - The unique identifier of the user
- `subjectId` - The unique identifier of the subject
- `courseGrades` - List of course grades
- `labGrades` - List of lab grades

In addition the `Last-Modified` header is updated with a new timestamp

#### Status codes

- `201` (Created) - The user has been successfully created
- `404` (Not Found) - Subject or User not found
- `409` (Conflict) - Username already exists

### Add a grade to an enrollment

- `POST /enrollment/users/{userId}/subjects/{subjectId}`

Add grade (lab or course) to an existing enrollment

#### Request

The request contains a JSON object with either one the following property: 
- labGrade - Grade given for a lab
- courseGrade - Grade given for a midterm

#### Response

The response's body is empty

The header `Last-Modified` is updated with a new timestamp

#### Status codes

- `200` (OK) - The user informations were correctly updated
- `400` (Bad Request) - The request body is invalid
- `404` (Not Found) - Enrollment does not exist with this keys association

### Delete an existing enrollment

- `DELETE /enrollment/users/{userId}/subjects/{subjectId}`

Delete enrollment

#### Request

The request has an empty body, only giving IDs in url

#### Response

The response has also an empty body, but `Last-Modified` header is updated with a new timestamp

#### Status codes

- `204` (No content) - Delete was successful, no content is present in the body
- `404` (Not Found) - The specified user does not exist
- `412` (Precondition Failed) - Ressource was updated in the meantime

### Get an overview of all enrollments for a user

- `GET /enrollment/users/{userId}/overview`

Get the overview of all the enrollments of the user, with their actual avg grades per subject and previsionnal average 

#### Request

Body is empty, only `userId` is taken from the url

#### Response

The response body contains a JSON object withe the following properties:

- `subject` - Subject name and other attributes
- `avgBeforeExam` - Average grade before taking the exam
- `prevAvg` - Expected average grade depending on the one that one could have in the exam


In addition the `Last-Modified` header is updated with a new timestamp

#### Status codes

- `200` (OK) - The request has been served
- `404` (Not Found) - No user was found with this ID
- `412` (Precondition Failed) - Ressource was changed in the meantime



