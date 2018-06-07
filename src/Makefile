libs=-I../../scheduling/lib -I/usr/lib/jvm/java-1.8.0-openjdk-amd64/include -I/usr/lib/jvm/java-1.8.0-openjdk-amd64/include/linux
#caching global memory
#caching=-Xptxas -dlcm=ca
params=-std=c++11 --expt-extended-lambda -lcuda
gen=-gencode arch=compute_20,code=sm_20 -gencode arch=compute_30,code=sm_30 -gencode arch=compute_35,code=sm_35 -gencode arch=compute_37,code=sm_37 -gencode arch=compute_50,code=sm_50 -gencode arch=compute_52,code=sm_52 -gencode arch=compute_60,code=sm_60

#first=MinSortExample
first=MinSortCloudSim

all: minsort 

minsort: $(first).cu
	nvcc $(gen) $(libs) -o lib$(first).so -shared -Xcompiler -fPIC $(first).cu $(params) -DELAPSED_TIME=$(TIME)


cloud1: $(first).cu
	nvcc $(gen) $(libs) -o lib$(first).so -shared -Xcompiler -fPIC $(first).cu $(params) -DELAPSED_TIME=$(TIME)




