## Component
- zookeeper
- kafka
- tcp server -> kafka
- kafka -> tcp server

    docker-compose up -d
    docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic driver.info
