# Users API

The users API allows to manage users. It uses the HTTP protocol and the JSON
format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new user
- Get one user's overview by its ID
- Update a user
- Delete a user


## Endpoints

### Create a new user

- `POST /users`

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

- `POST /user/{id}`

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

- `DELETE /user/{id}`

Delete user

#### Request

The request has an empty body, only giving id in url

#### Response

The response has also an empty body

#### Status codes

- `204` (No content) - Delete was successful, no content is present in the body
- `404` (Not Found) - The specified user does not exist





