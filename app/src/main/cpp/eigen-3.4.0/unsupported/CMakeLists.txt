add_subdirectory(Eigen)
if(EIGEN_BUILD_DOC)
  add_subdirectory(doc EXCLUDE_FROM_ALL)
endif()
if(BUILD_TESTING)
  if(EIGEN_LEAVE_TEST_IN_ALL_TARGET)
    add_subdirectory(test) # can't do EXCLUDE_FROM_ALL here, breaks CTest
  else()
    add_subdirectory(test EXCLUDE_FROM_ALL)
  endif()
endif()

target_include_directories(${PROJECT_NAME}
        PRIVATE
        "${CMAKE_SOURCE_DIR}/eigen-3.4.0"
)
