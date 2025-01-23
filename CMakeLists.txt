cmake_minimum_required(VERSION 3.0)
project(AntlerSolver)

set (AntlerSolver_VERSION_MAJOR 1)
set (AntlerSolver_VERSION_MINOR 0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake/")
cmake_policy(SET CMP0057 NEW)

if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

include_directories(include)

find_package(Filesystem)

set(HEADER_FILES
        include/Graph.h)

set(SOURCE_FILES
        src/Graph.cpp)

add_executable(AntlerSolver src/AntlerSolver.cpp ${SOURCE_FILES} ${HEADER_FILES})

install (TARGETS AntlerSolver DESTINATION bin)