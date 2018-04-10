#Running
    
Be sure to have the necessary environment with docker, docker-compose, zephyr, zephyr_sdk, and net-tools
https://docs.docker.com/install/

https://docs.docker.com/compose/install/

http://docs.zephyrproject.org/getting_started/installation_linux.html

https://github.com/zephyrproject-rtos/net-tools
    
    
    # On Terminal #1 
    # Clone and bring up the dojot infrastructure with docker compose
    # Be aware this may take a few minutes (or hours depending on your connection)
    git clone git@github.com:dojot/docker-compose.git
    cd docker-compose
    git checkout 0.2.0
    docker-compose up -d
    
    # On Terminal #2
    git clone --recursive https://github.com/jsiloto/iotagent-leshan
    cd iotagent
    git checkout 0.2.0
    
    # Run Database Fixture for this example
    cd client
    python3 db_fixture.py
    
    # Build and Run COAP Fileserver
    cd iotagent-leshan
    mkdir fw
    docker build -f fileserver.Dockerfile -t local/fileserver .
    docker run --rm -it -v $PWD/fw:/usr/src/app/demo-apps/cf-simplefile-server/data -p 5693:5693/udp local/fileserver
    
    # On Terminal #3
    # Build and Run IoTAgent
    cd iotagent-leshan
    docker build -f Dockerfile -t local/iotagent-leshan .
    docker run --rm -it --network dockercompose_default -p 5683:5683/udp local/iotagent-leshan
    
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

   