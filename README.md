# Users API

The users API allows to manage users. It uses the HTTP protocol and the JSON format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new user
- Update a user
- Delete a user

## Endpoints

### Create a new user

- `POST /users/`

Create a new user.

#### Request

The request body must contain a JSON object with the following properties:

- `firstName` - The first name of the user
- `lastName` - The last name of the user
- `username` - Username chosen by user (must be unique)
- `password` - Password of the account

#### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the user
- `firstName` - The first name of the user
- `lastName` - The last name of the user
- `username` - Username chosen by user
- `password` - Password chosen by user

#### Status codes

- `201` (Created) - The user has been successfully created
- `400` (Bad Request) - The request body is invalid
- `409` (Conflict) - Username already exists

### Modify an existing user

- `POST /users/{id}`

Update user infos

#### Request

The request contains a JSON object with the following possible properties (depending on what he wants to update) :
- firstName
- lastName
- username
- password

#### Response

The response contains a JSON with the following properties :
- `id` - The unique identifier of the user
- `firstName` - The first name of the user
- `lastName` - The last name of the user
- `username` - Username chosen by user
- `password` - Password chosen by user

#### Status codes

- `201` (Created) - The user informations were correctly updated
- `400` (Bad Request) - The request body is invalid

### Delete an existing user

- `DELETE /users/{id}`

Delete user

#### Request

The request has an empty body, only giving id in url

#### Response

The response has also an empty body

#### Status codes

- `204` (No content) - Delete was successful, no content is present in the body
- `404` (Not Found) - The specified user does not exist


# Subject API

The subject API allows to manage subjects. It uses the HTTP protocol and the JSON format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new subject
- Update a subject
- Delete a subject


## Endpoints

### Create a new subject

- `POST /subjects/`

Create a new subject.

#### Request

The request body must contain a JSON object with the following properties:

- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject

#### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the subject
- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject

The `Last-Modified` header is updated with a new timestamp

#### Status codes

- `201` (Created) - The subject has been successfully created
- `400` (Bad Request) - The request body is invalid
- `409` (Conflict) - Subject already exists

### Modify an existing user

- `POST /subjects/{id}`

Update subject infos

#### Request

The request contains a JSON object with the following possible properties (depending on what he wants to update) :

- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject


#### Response

The response contains a JSON with the following properties :
- `id` - The unique identifier of the subject
- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject

The `Last-Modified` header is updated with a new timestamp

#### Status codes

- `201` (Created) - The subject's informations were correctly updated
- `404` (Not Found) - The subject was not found
- `412` (Precondition Failed) - Ressource was modified in the meantime

### Delete an existing subject

- `DELETE /subjects/{id}`

Delete subject

#### Request

The request has an empty body, only giving id in url

#### Response

The response has also an empty body

#### Status codes

- `204` (No content) - Delete was successful, no content is present in the body
- `404` (Not Found) - The specified user does not exist

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

- `POST /enrollment/users/{userId}/subjects/{subjectId}/addGrade`

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


