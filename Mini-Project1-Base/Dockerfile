# Use OpenJDK 25 Early Access on Oracle Linux 9
FROM openjdk:25-ea-4-jdk-oraclelinux9

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/*.jar app.jar

VOLUME /data

# Copy the data folder from src/main/java/com/example/ into the container
COPY src/main/java/com/example/data/users.json /data/users.json
COPY src/main/java/com/example/data/carts.json /data/carts.json
COPY src/main/java/com/example/data/orders.json /data/orders.json
COPY src/main/java/com/example/data/products.json /data/products.json

# Set environment variable for JSON file location
ENV spring.application.userDataPath=/data/users.json
ENV spring.application.productDataPath=/data/products.json
ENV spring.application.orderDataPath=/data/orders.json
ENV spring.application.cartDataPath=/data/carts.json

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
