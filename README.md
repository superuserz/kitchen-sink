Techstack - 

Backend - Spring Boot + JDK-21
Frontent - Angular + Node.js
Database - MongoDB (NoSQL)
Deployment - Azure App Services
CI-CD - Github Actions


Build Notes -
  From the Root of Project, run below commands
1. mvn clean package
2. docker build --build-arg JAR_FILE=target/kitchen-sink-user-0.0.1-SNAPSHOT.jar -t kitchensink-backend .
3. docker tag kitchensink-backend:latest superuserz/kitchensink-backend:latest
4. docker push superuserz/kitchensink-backend:latest

Running the App
1. The app can simply be run locally by using Docker Locally
2. Use command "docker-compose up -d kitchensink"
3. Access the Swagger Endpoints at http://localhost:8080/swagger-ui/index.html

Cloud Deployment
1. The app is connected to Github Actions for CI-CD via Azure App-Service Configuration
2. https://kitchensink-backend-b9ame2g6bddnaee7.centralindia-01.azurewebsites.net/swagger-ui/index.html

Frontend Details
Git Repo For Angular App - https://github.com/superuserz/kitchen-sink-front

Commands to Running Locally
1. npm install
2. ng serve (or npx ng server)
3. Access it Locally at http://localhost:4200

Cloud Deployment

1. The App has a User Interface deployed as a Azure App Service
2. Accessible at https://kitchensink-frontend-e8bmcddyf0gvbuac.centralindia-01.azurewebsites.net

   
