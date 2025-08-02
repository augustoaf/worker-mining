echo Packaging and running the application
call mvn clean package
call java -jar target/worker-mining-1.0.0.jar
