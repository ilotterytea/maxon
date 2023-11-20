cmake_minimum_required(VERSION 3.14)

include(cmake/prelude.cmake)

project(
    MaxonPettingSimulator
    VERSION 1.0.0
    DESCRIPTION "A clicker game about Maxon the cat"
    HOMEPAGE_URL "https://ilotterytea.itch.io/maxon"
    LANGUAGES CXX
)

include(cmake/project-is-top-level.cmake)
include(cmake/variables.cmake)

# ---- Declare library ----

add_library(
    MaxonPettingSimulator_lib OBJECT
    source/lib.cpp
)

target_include_directories(
    MaxonPettingSimulator_lib ${warning_guard}
    PUBLIC
    "$<BUILD_INTERFACE:${PROJECT_SOURCE_DIR}/source>"
)

target_compile_features(MaxonPettingSimulator_lib PUBLIC cxx_std_17)

find_package(fmt REQUIRED)
target_link_libraries(MaxonPettingSimulator_lib PRIVATE fmt::fmt)

# ---- Declare executable ----

add_executable(MaxonPettingSimulator_exe source/main.cpp)
add_executable(MaxonPettingSimulator::exe ALIAS MaxonPettingSimulator_exe)

set_property(TARGET MaxonPettingSimulator_exe PROPERTY OUTPUT_NAME MaxonPettingSimulator)

target_compile_features(MaxonPettingSimulator_exe PRIVATE cxx_std_17)
target_link_libraries(MaxonPettingSimulator_exe PUBLIC MaxonPettingSimulator_lib sfml-network sfml-audio sfml-graphics sfml-window sfml-system)

# ---- Install rules ----

if(NOT CMAKE_SKIP_INSTALL_RULES)
  include(cmake/install-rules.cmake)
endif()

# ---- Developer mode ----

if(NOT MaxonPettingSimulator_DEVELOPER_MODE)
  return()
elseif(NOT PROJECT_IS_TOP_LEVEL)
  message(
      AUTHOR_WARNING
      "Developer mode is intended for developers of MaxonPettingSimulator"
  )
endif()

include(cmake/dev-mode.cmake)
