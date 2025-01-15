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

#### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the user
- `firstName` - The first name of the user
- `lastName` - The last name of the user

#### Status codes

- `201` (Created) - The user has been successfully created
- `400` (Bad Request) - The request body is invalid


### Get one user's overview

- `GET /users/{id}`

Get one user's overview by its ID.

#### Request

The request path must contain the ID of the user.

#### Response

The response body contains a JSON object with the following properties:


#### Status codes

- `200` (OK) - The user has been successfully retrieved
- `404` (Not Found) - The user does not exist

