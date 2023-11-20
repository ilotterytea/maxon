install(
    TARGETS MaxonPettingSimulator_exe
    RUNTIME COMPONENT MaxonPettingSimulator_Runtime
)

if(PROJECT_IS_TOP_LEVEL)
  include(CPack)
endif()
