#Running
    
    # Run Dojot backend
    git clone https://github.com/jsiloto/docker-compose
    cd docker-compose
    docker-compose up -d
    cd ..
    
    # Clone this repo
    git clone --recursive https://github.com/jsiloto/iotagent
    cd iotagent
    
    # Build and Run COAP Fileserver
    docker build -f fileserver.Dockerfile -t local/fileserver .
    docker run --rm -it -v $PWD/fw:/usr/src/app/demo-apps/cf-simplefile-server/data -p 5693:5693/udp local/fileserver
    
    # Build and Run IoTAgent
    mvn install
    java -jar target/iotagent-0.1.0-SNAPSHOT-jar-with-dependencies.jar
    
   