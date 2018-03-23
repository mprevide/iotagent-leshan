#Running
    
Be sure to have the necessary environment with docker, docker-compose, zephyr, zephyr_sdk, and net-tools
https://docs.docker.com/install/

https://docs.docker.com/compose/install/

http://docs.zephyrproject.org/getting_started/installation_linux.html

https://github.com/zephyrproject-rtos/net-tools
    
    
    # On Terminal #1 
    # Clone and bring up the dojot infrastructure with docker compose
    # Be aware this may take a few minutes (or hours depending on your connection)
    git clone git@github.com:jsiloto/docker-compose.git
    cd docker-compose
    docker-compose up -d
    
    # On Terminal #2
    git clone --recursive https://github.com/jsiloto/iotagent
    cd iotagent
    git checkout 0.1.0
    
    # Run Database Fixture for this example
    python3 client.py
    
    # Build and Run COAP Fileserver
    mkdir fw
    docker build -f fileserver.Dockerfile -t local/fileserver .
    docker run --rm -it -v $PWD/fw:/usr/src/app/demo-apps/cf-simplefile-server/data -p 5693:5693/udp local/fileserver
    
    # On Terminal #3
    # Build and Run IoTAgent
    cd iotagent
    mvn install
    java -jar target/iotagent-0.1.0-SNAPSHOT-jar-with-dependencies.jar
    
    # On Terminal #4
    cd net-tools
    ./loop-socat.sh
    
    # On Terminal #5
    cd net-tools
    ./loop-slip-tap.sh
    
    # On Terminal #6
    # Get repo version with working example
    cd zephyr
    git remote add jsiloto git@github.com:jsiloto/zephyr.git
    git fetch jsiloto
    git checkout jsiloto/master
    cd samples/net/lwm2m_client/
    mkdir -p build
    cd build
    make pristine
    cmake -DBOARD=qemu_cortex_m3 ..
    make run -j4
    
    # Open your browser on http://localhost:8000
    # Login with admin/admin

   