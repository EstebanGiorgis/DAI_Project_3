# Authentication API

The Authentication API manages the login, logout, adn roles validation.

## Endpoints

### Login

- `POST /auth/login`

Authenticate a user and create cookies with its userId and for its role.

#### Request

The request body must contain a JSON object with the following properties:

- `email` - The user's email address (required).
- `password` - The user's password (required).

#### Response

Creation of those cookies if successful

- `user` - The ID of the authenticated user.
- `role` - The user's role (e.g., `LOGGED_IN`).

The body of the response is empty

#### Status codes

- `204` (No Content) - Login successful.
- `400` (Bad Request) - The request body is invalid.
- `401` (Unauthorized) - Invalid email or password.

---

### Logout

- `POST /auth/logout`

Remove their cookies for logout a user

#### Request

No body needed for the request

#### Response

The response body is empty.

#### Status codes

- `204` (No Content) - Logout successful.

---

## Authorization Middleware

The `AuthRoleController` class is responsible for checking user roles for each endpoints


#### Usage

```java
AuthRoleController.checkRole(Context requestContent, Set<Role> permittedRoles);
```

- `requestContent` - The HTTP context of the request.
  - `permittedRoles` - Every possible roles

#### Behavior

- If the user's role (retrieved from the `role` cookie) matches one of the permitted roles, the action proceeds.
- If not, a `403 Forbidden` response is returned.

#### Status codes

- `403` (Forbidden) - Access is not authorized.

---

## Cookies

### User Cookie

- **Name:** `user`
- **Description:** Stores the ID of the authenticated user.
- **Lifetime:** Session duration.

### Role Cookie

- **Name:** `role`
- **Description:** Stores the role of the authenticated user (e.g., `LOGGED_IN`, `ADMIN`).
- **Lifetime:** Session duration.

