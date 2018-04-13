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
    
    # Run Database Fixture for this example
    cd client
    python3 db_fixture.py


    # On Terminal #3
    cd net-tools
    ./loop-socat.sh
    
    # On Terminal #4
    cd net-tools
    ./loop-slip-tap.sh
    
    # On Terminal #5
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
