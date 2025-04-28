cmake_minimum_required(VERSION 3.10.2)

project("matrix")

# Changed target name to "matrix"
add_library(
        matrix  # This must match Java's loadLibrary name
        SHARED
        native-lib.cpp
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/eigen-3.4.0)

find_library(
        log-lib
        log
)

# Link to the correct target name
target_link_libraries(
        matrix  # Must match add_library name
        ${log-lib}
)