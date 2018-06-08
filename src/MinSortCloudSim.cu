#include <jni.h>
//#include <iostream>
#include "MinSortCloudSim.h"
#include <cuda.h>
#include <moderngpu/kernel_segsort.hxx>

JNIEXPORT void JNICALL Java_MinSortCloudSim_segmented_1sort (JNIEnv * env, jobject obj,
		jfloatArray ma, jintArray ta, jintArray seg, jint t, jint m) {

    float *machines = env->GetFloatArrayElements(ma, 0);
    int *task_index  = env->GetIntArrayElements(ta, 0);
    int *segments = env->GetIntArrayElements(seg, 0);

	int *d_task_index;
	int *d_segments;
	float *d_machines;

	int mem_size_seg = sizeof(int) * (m);
	int mem_size_machines = sizeof(float) * (m * t);
	int mem_size_task_index = sizeof(uint) * (m * t);

	cudaMalloc((void **) &d_segments, mem_size_seg);
	cudaMalloc((void **) &d_machines, mem_size_machines);
	cudaMalloc((void **) &d_task_index, mem_size_task_index);

	//cudaEventRecord(start);
	// copy host memory to device
	cudaMemcpy(d_segments, segments, mem_size_seg, cudaMemcpyHostToDevice);
	cudaMemcpy(d_machines, machines, mem_size_machines, cudaMemcpyHostToDevice);
	cudaMemcpy(d_task_index, task_index, mem_size_task_index, cudaMemcpyHostToDevice);

	mgpu::standard_context_t context;
	mgpu::segmented_sort(d_machines, d_task_index, m * t, d_segments, m, mgpu::less_t<float>(), context);

	cudaMemcpy(task_index, d_task_index, mem_size_task_index, cudaMemcpyDeviceToHost);
	cudaMemcpy(machines, d_machines, mem_size_machines, cudaMemcpyDeviceToHost);

	env->ReleaseFloatArrayElements(ma, machines, NULL);
	env->ReleaseIntArrayElements(ta, task_index, NULL);


}

JNIEXPORT void JNICALL Java_MinSortCloudSim_segmented_1sort_1desc (JNIEnv * env, jobject obj,
		jfloatArray ma, jintArray ta, jintArray seg, jint t, jint m) {

    float *machines = env->GetFloatArrayElements(ma, 0);
    int *task_index  = env->GetIntArrayElements(ta, 0);
    int *segments = env->GetIntArrayElements(seg, 0);

	int *d_task_index;
	int *d_segments;
	float *d_machines;

	int mem_size_seg = sizeof(int) * (m);
	int mem_size_machines = sizeof(float) * (m * t);
	int mem_size_task_index = sizeof(uint) * (m * t);

	cudaMalloc((void **) &d_segments, mem_size_seg);
	cudaMalloc((void **) &d_machines, mem_size_machines);
	cudaMalloc((void **) &d_task_index, mem_size_task_index);

	//cudaEventRecord(start);
	// copy host memory to device
	cudaMemcpy(d_segments, segments, mem_size_seg, cudaMemcpyHostToDevice);
	cudaMemcpy(d_machines, machines, mem_size_machines, cudaMemcpyHostToDevice);
	cudaMemcpy(d_task_index, task_index, mem_size_task_index, cudaMemcpyHostToDevice);

	mgpu::standard_context_t context;
	mgpu::segmented_sort(d_machines, d_task_index, m * t, d_segments, m, mgpu::greater_t<float>(), context);

	cudaMemcpy(task_index, d_task_index, mem_size_task_index, cudaMemcpyDeviceToHost);
	cudaMemcpy(machines, d_machines, mem_size_machines, cudaMemcpyDeviceToHost);

	env->ReleaseFloatArrayElements(ma, machines, NULL);
	env->ReleaseIntArrayElements(ta, task_index, NULL);


}
