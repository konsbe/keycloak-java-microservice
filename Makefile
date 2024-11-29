IMAGE=sci-tech-city-microservice-keycloak-microservice
IMGTAR=${IMAGE}.tar
HOST_PORT=8082

# Build the Docker image from the Dockerfile
build-img:
	sudo docker build -t sci-tech-city-keycloak-microservice:17-jdk-alpine .


# Run the Docker container locally
run:
	sudo docker run -it -p 8082:8082 --network host sci-tech-city-keycloak-microservice:17-jdk-alpine

# Stop and remove the Docker container if it's already running
clean:
	docker stop java-rest || true
	docker rm java-rest || true
