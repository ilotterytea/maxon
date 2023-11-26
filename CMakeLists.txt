cmake_minimum_required(VERSION 3.14)

include(cmake/prelude.cmake)

project(
  MaxonPettingSimulator
  VERSION 1.0.0
  DESCRIPTION "A clicker game about Maxon the cat"
  HOMEPAGE_URL "https://ilotterytea.itch.io/maxon"
  LANGUAGES CXX)

include(cmake/project-is-top-level.cmake)
include(cmake/variables.cmake)

# ---- Declare executable ----
set(SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/source)

file(GLOB_RECURSE CPP_SOURCES "${SOURCE_DIR}/*.cpp")
file(GLOB_RECURSE HPP_HEADERS "${SOURCE_DIR}/*.hpp")

add_executable(MaxonPettingSimulator_exe ${CPP_SOURCES} ${HPP_HEADERS})
add_executable(MaxonPettingSimulator::exe ALIAS MaxonPettingSimulator_exe)

set_property(TARGET MaxonPettingSimulator_exe PROPERTY OUTPUT_NAME
                                                       MaxonPettingSimulator)

target_compile_features(MaxonPettingSimulator_exe PRIVATE cxx_std_17)
target_link_libraries(
  MaxonPettingSimulator_exe PUBLIC sfml-network sfml-audio sfml-graphics
                                   sfml-window sfml-system)

# ---- Install rules ----

if(NOT CMAKE_SKIP_INSTALL_RULES)
  include(cmake/install-rules.cmake)
endif()

if(CMAKE_COMPILER_IS_GNUCXX)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra")
endif()

# ---- Developer mode ----

if(NOT MaxonPettingSimulator_DEVELOPER_MODE)
  return()
elseif(NOT PROJECT_IS_TOP_LEVEL)
  message(
    AUTHOR_WARNING
      "Developer mode is intended for developers of MaxonPettingSimulator")
endif()

include(cmake/dev-mode.cmake)
