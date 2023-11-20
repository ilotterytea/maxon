# Parent project does not export its library target, so this CML implicitly
# depends on being added from it, i.e. the testing is done only from the build
# tree and is not feasible from an install location

project(MaxonPettingSimulatorTests LANGUAGES CXX)

# ---- Dependencies ----

find_package(Catch2 REQUIRED)
include(Catch)

# ---- Tests ----

add_executable(MaxonPettingSimulator_test source/MaxonPettingSimulator_test.cpp)
target_link_libraries(
    MaxonPettingSimulator_test PRIVATE
    MaxonPettingSimulator_lib
    Catch2::Catch2WithMain
)
target_compile_features(MaxonPettingSimulator_test PRIVATE cxx_std_17)

catch_discover_tests(MaxonPettingSimulator_test)

# ---- End-of-file commands ----

add_folders(Test)
