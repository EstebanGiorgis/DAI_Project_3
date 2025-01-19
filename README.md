```text
 █████╗ ███╗   ███╗    ██╗    ███████╗       ██████╗██╗  ██╗██████╗ 
██╔══██╗████╗ ████║    ██║    ██╔════╝▄ ██╗▄██╔════╝██║ ██╔╝██╔══██╗
███████║██╔████╔██║    ██║    █████╗   ████╗██║     █████╔╝ ██║  ██║
██╔══██║██║╚██╔╝██║    ██║    ██╔══╝  ▀╚██╔▀██║     ██╔═██╗ ██║  ██║
██║  ██║██║ ╚═╝ ██║    ██║    ██║       ╚═╝ ╚██████╗██║  ██╗██████╔╝
╚═╝  ╚═╝╚═╝     ╚═╝    ╚═╝    ╚═╝            ╚═════╝╚═╝  ╚═╝╚═════╝ 
```
A Web Application to manage and preview your final grades depending on which grade you do at the exams

### Contributors
@nilsdonatantonio
@EstebanGiorgis
@drinracaj

## Contributing

If you wish to help us with the improvement of this application, start by cloning the project on your device
```
git clone git@github.com:EstebanGiorgis/DAI_Project_3.git

# Create a branch to work on the feature you want
git switch -c <name of your branch>
```

A maven wrapper is already present under ./DAI_Project_3/lab06/, so go from there to build the project
```
# Build the project
./mvwn dependency:go-offline clean compile package

# Run the server (still from ./DAI_Project_3/lab06)
java -jar target/lab06-1.0-SNAPSHOT.jar
```

Once you've modified what you wanted, create a PR that we will be more than happy to review


# DOCUMENTATION

## Build, publish and run the application with Docker

Run `docker compose up` once to generate the images

Connect to GHCR from docker 
```
docker login ghcr.io -u <username>
```

When asked for password give it a Personnal access token


Tag the wanted image
```
docker tag dai_project_3-java-app ghcr.io/<username>/dai_project_3-java-app:latest
```

Push the image to GHCR
```
docker push ghcr.io/<username>/dai_project_3-java-app
```

Change the visibility of the package to public from your github account and link it to your repository


## APIs



### Users API

The users API allows to manage users. It uses the HTTP protocol and the JSON format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new user
- Update a user
- Delete a user


#### Endpoints

##### Create a new user

- `POST /users/`

Create a new user.

###### Example
```
curl -X POST \
-H "Content-Type: application/json" \
-d '{"firstName": "John", "lastName": "Doe", "username": "johndoe", "password": "secret123"}' \
https://amifucked.duckdns.org/users/

# Output
{"id":1,"firstName":"John","lastName":"Doe","username":"johndoe","password":"secret123"}
```

###### Request

The request body must contain a JSON object with the following properties:

- `firstName` - The first name of the user
- `lastName` - The last name of the user
- `username` - Username chosen by user (must be unique)
- `password` - Password of the account

###### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the user
- `firstName` - The first name of the user
- `lastName` - The last name of the user
- `username` - Username chosen by user
- `password` - Password chosen by user

###### Status codes

- `201` (Created) - The user has been successfully created
- `400` (Bad Request) - The request body is invalid
- `409` (Conflict) - Username already exists

##### Modify an existing user

- `POST /users/{id}`

Update user infos

###### Example
```
curl -X PUT \
-H "Content-Type: application/json" \
-d '{"firstName": "John", "lastName": "Smith"}' \
https://amifucked.duckdns.org/users/1

# Output
{"id":1,"firstName":"John","lastName":"Smith","username":"johndoe","password":"secret123"}
```
###### Request

The request contains a JSON object with the following possible properties (depending on what he wants to update) :
- firstName
- lastName
- username
- password

###### Response

The response contains a JSON with the following properties :
- `id` - The unique identifier of the user
- `firstName` - The first name of the user
- `lastName` - The last name of the user
- `username` - Username chosen by user
- `password` - Password chosen by user

###### Status codes

- `201` (Created) - The user informations were correctly updated
- `400` (Bad Request) - The request body is invalid

##### Delete an existing user

- `DELETE /users/{id}`

Delete user

###### Example
```
curl -X DELETE https://amifucked.duckdns.org/users/1

#NO OUTPUT
```
###### Request

The request has an empty body, only giving id in url

###### Response

The response has also an empty body

###### Status codes

- `204` (No content) - Delete was successful, no content is present in the body
- `404` (Not Found) - The specified user does not exist


### Subject API

The subject API allows to manage subjects. It uses the HTTP protocol and the JSON format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new subject
- Update a subject
- Delete a subject


#### Endpoints

##### Create a new subject

- `POST /subjects/`

Create a new subject.

###### Example
```
curl -X POST \
-H "Content-Type: application/json" \
-d '{"shortName": "DAI", "fullName": "Développement d'\''applications internet"}' \
https://amifucked.duckdns.org/subjects/

#Output
{"id":1,"shortName":"DAI","fullName":"Développement d'applications internet"}
```

###### Request

The request body must contain a JSON object with the following properties:

- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject

###### Response

The response body contains a JSON object with the following properties:

- `id` - The unique identifier of the subject
- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject

The `Last-Modified` header is updated with a new timestamp

###### Status codes

- `201` (Created) - The subject has been successfully created
- `400` (Bad Request) - The request body is invalid
- `409` (Conflict) - Subject already exists

##### Modify an existing user

- `POST /subjects/{id}`

Update subject infos

###### Example
```
curl -X PUT \
-H "Content-Type: application/json" \
-d '{"shortName": "DAI2", "fullName": "Développement Applications Internet"}' \
https://amifucked.duckdns.org/subjects/1

# Output
{"id":1,"shortName":"DAI2","fullName":"Développement Applications Internet"}
```
###### Request

The request contains a JSON object with the following possible properties (depending on what he wants to update) :

- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject


###### Response

The response contains a JSON with the following properties :
- `id` - The unique identifier of the subject
- `shortName` - The short form of the subject's name
- `fullName` - The full name of the subject

The `Last-Modified` header is updated with a new timestamp

###### Status codes

- `201` (Created) - The subject's informations were correctly updated
- `404` (Not Found) - The subject was not found
- `412` (Precondition Failed) - Ressource was modified in the meantime

##### Delete an existing subject

- `DELETE /subjects/{id}`

Delete subject

###### Example
```
curl -X DELETE https://amifucked.duckdns.org/subjects/1

# NO OUTPUT
```

###### Request

The request has an empty body, only giving id in url

###### Response

The response has also an empty body

###### Status codes

- `204` (No content) - Delete was successful, no content is present in the body
- `404` (Not Found) - The specified user does not exist

### Enrollment API

The enrollments API allows to manage enrollments (link between a user and a subject, containing their respective grades). It uses the HTTP protocol and the JSON format.

The API is based on the CRUD pattern. It has the following operations:

- Create a new enrollment
- Add a grade to an enrollment
- Delete an enrollment
- Get an overview of all enrollments for a user

#### Endpoints

##### Create a new enrollment

- `POST /enrollment/users/{userId}/subjects/{subjectId}`

Create a new enrollment.

###### Example
```
curl -X POST https://amifucked.duckdns.org/enrollment/users/1/subjects/1

# Output
{"userId":1,"subjectId":1,"courseGrades":[],"labGrades":[]}
```

###### Request

Body is empty, only userId and subjectId are specified in url

###### Response

The response body contains a JSON object with the following properties:

- `userId` - The unique identifier of the user
- `subjectId` - The unique identifier of the subject
- `courseGrades` - List of course grades
- `labGrades` - List of lab grades

In addition the `Last-Modified` header is updated with a new timestamp

###### Status codes

- `201` (Created) - The user has been successfully created
- `404` (Not Found) - Subject or User not found
- `409` (Conflict) - Username already exists

##### Add a grade to an enrollment

- `POST /enrollment/users/{userId}/subjects/{subjectId}/addGrade`

Add grade (lab or course) to an existing enrollment

###### Examples

For a lab grade:
```
curl -X POST \
-H "Content-Type: application/json" \
-d '{"gradeType": "labGrade", "grade": 5.5}' \
https://amifucked.duckdns.org/enrollment/users/1/subjects/1/addGrade

# NO OUTPUT
```

For a course grade:
```
curl -X POST \
-H "Content-Type: application/json" \
-d '{"gradeType": "courseGrade", "grade": 5.5}' \
https://amifucked.duckdns.org/enrollment/users/1/subjects/1/addGrade

# NO OUTPUT
```

###### Request

The request contains a JSON object with either one the following property: 
- labGrade - Grade given for a lab
- courseGrade - Grade given for a midterm

###### Response

The response's body is empty

The header `Last-Modified` is updated with a new timestamp

###### Status codes

- `200` (OK) - The user informations were correctly updated
- `400` (Bad Request) - The request body is invalid
- `404` (Not Found) - Enrollment does not exist with this keys association

##### Delete an existing enrollment

- `DELETE /enrollment/users/{userId}/subjects/{subjectId}`

Delete enrollment

###### Example
```
curl -X DELETE https://amifucked.duckdns.org/enrollment/users/1/subjects/1

# NO OUTPUT
```
###### Request

The request has an empty body, only giving IDs in url

###### Response

The response has also an empty body, but `Last-Modified` header is updated with a new timestamp

###### Status codes

- `204` (No content) - Delete was successful, no content is present in the body
- `404` (Not Found) - The specified user does not exist
- `412` (Precondition Failed) - Ressource was updated in the meantime

##### Get an overview of all enrollments for a user

- `GET /enrollment/users/{userId}/overview`

Get the overview of all the enrollments of the user, with their actual avg grades per subject and previsionnal average 

###### Example
```
curl -X GET \
https://amifucked.duckdns.org/enrollment/users/1/overview

# Output
[{"subject":{"id":1,"shortName":"DAI","fullName":"Développement d'applications internet"},"avgBeforeExam":5.5,"prevAvg":{"1.0":3.25,"2.0":3.75,"4.0":4.75,"4.5":5.0,"2.5":4.0,"5.0":5.25,"5.5":5.5,"1.5":3.5,"3.0":4.25,"6.0":5.75,"3.5":4.5}}]
```

###### Request

Body is empty, only `userId` is taken from the url

###### Response

The response body contains a JSON object withe the following properties:

- `subject` - Subject name and other attributes
- `avgBeforeExam` - Average grade before taking the exam
- `prevAvg` - Expected average grade depending on the one that one could have in the exam

In addition the `Last-Modified` header is updated with a new timestamp

###### Status codes

- `200` (OK) - The request has been served
- `404` (Not Found) - No user was found with this ID
- `412` (Precondition Failed) - Ressource was changed in the meantime

## Setup Web Infrastructure

### Generate SSH Key

For authentication we will need a ssh key, so generate it now to use it later
```
ssh-keygen
```
<p align="center">
  <img src="./images/1-Generate_SSH-Key.png" alt="Create ressource">
</p>

### Create virtual machine

Create new ressource on azure
<p align="left">
  <img src="./images/1-Create_Ressource_1.png" alt="Create ressource">
</p>

Select the `Virtual Machine` ressource

<p align="left">
  <img src="./images/1-Create_Ressource_2.png" alt="Create ressource">
</p>

Setup ressource  

 On azure, fill the form with these informations to create the VM:
  - Project details
      - **Subscription**: Azure for Students
      - **Resource group**: Create new with the name heig-vd-dai-course
  - Instance details
      - **Virtual machine name**: practical-work-3
      - **Region**: (Europe) West Europe
      - **Availability options**: No infrastructure redundancy required
      - **Security type**: Trusted launch virtual machines (the default)
      - **Image**: Ubuntu Server 24.04 LTS - x64 Gen2 (the default)
      - **VM architecture**: x64
      - **Size**: Standard_B1s - you might need to click "See all sizes" to see this option
  - Administrator account
      - **Authentication type**: SSH public key
      - **Username**: ubuntu
      - **SSH public key source**: Use existing public key
      - **SSH public key**: Paste public key previously generated here    
    - Inbound port rules
      - **Public inbound ports**: Allow selected ports
      - **Select inbound ports**: HTTP (80), HTTPS (443), SSH (22)

  Click on `Review + create`

  Wait for the VM to be ready

### Setup virtual machine

Connect to the virtual machine with its public ip address from your terminal :
```
ssh ubuntu@40.115.3.193
```

If the key was created in an other folder than .ssh, add -i parameter to the command to specify the path where your key is stored
```
# In our case
ssh -i /home/enigma/kDrive/HEIG-VD/DAI/labo/3-WebApp/practical_work ubuntu@40.115.3.193
```

Update packages on the VM and reboot to apply
```
sudo apt update
sudo apt upgrade
sudo reboot
```

### Acquire domain name

Go to duckdns.org and get a domain name

Set the IP address of the A record (current IP field) with the public address of the server

<p align="left">
  <img src="./images/2-Setup_DNS_entry.png" alt="Create ressource">
</p>

Verify that resolution works
```
nslookup amifucked.duckdns.org

# Should return something like this
Server: 127.0.0.53
Address: 127.0.0.53#53

Non-authoritative answer:
Name: amifucked.duckdns.org
Address: 40.115.3.193
```


## DNS configuration
![image](https://github.com/user-attachments/assets/71c3d69e-6628-48dd-9a33-a8f38f53005f)

## Deploy, run and access the web applications with Docker Compose

1. After connecting to the server via SSH, go to the /webapp directory using the command:
   ``` cd webapp/ ```

2. Ensure that all containers are stopped and removed by running the command:
   ``` sudo docker compose down ```

3. Start and create all containers with Docker Compose by running the following command: ``` sudo docker compose up ```






