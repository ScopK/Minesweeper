################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/generator.cpp \
../src/gridsolver.cpp \
../src/solverc.cpp \
../src/tile.cpp \
../src/util.cpp 

OBJS += \
./src/generator.o \
./src/gridsolver.o \
./src/solverc.o \
./src/tile.o \
./src/util.o 

CPP_DEPS += \
./src/generator.d \
./src/gridsolver.d \
./src/solverc.d \
./src/tile.d \
./src/util.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -O2 -g -Wall -c -fmessage-length=0 -pthread -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


