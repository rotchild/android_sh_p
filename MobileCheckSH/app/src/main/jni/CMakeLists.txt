cmake_minimum_required(VERSION 3.4.1)
include_directories (. )
add_library(
             netencoder
             SHARED
             H264Android.c utils.c adpcm.c netencoder.cpp AudioEncoder.cpp AudioDecoder.cpp
           )
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wall -v -Wl,--no-warn-shared-textrel")
set (CMAKE_C_FLAGS  "${CMAKE_CXX_FLAGS} -DHAVE_AV_CONFIG_H")
target_link_libraries(netencoder ${CMAKE_SOURCE_DIR}/libx264.a log)
