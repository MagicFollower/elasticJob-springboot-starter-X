#include <stdio.h>

// 函数声明
void bubbleSort(int arr[], int n);
void printArray(int arr[], int size);

// 主函数
int main() {
    int arr[] = {64, 34, 25, 12, 22, 11, 90};
    int n = sizeof(arr)/sizeof(arr[0]);
    bubbleSort(arr, n);
    printf("排序后的数组: \n");
    printArray(arr, n);
    return 0;
}

// 冒泡排序函数
void bubbleSort(int arr[], int n) {
    int i, j, temp;
    for (i = 0; i < n-1; i++) {
        // 最后i个已经排好序了，不需要再次比较
        for (j = 0; j < n-i-1; j++) {
            if (arr[j] > arr[j+1]) {
                // 交换 arr[j] 和 arr[j+1]
                temp = arr[j];
                arr[j] = arr[j+1];
                arr[j+1] = temp;
            }
        }
    }
}

// 打印数组的函数
void printArray(int arr[], int size) {
    int i;
    for (i = 0; i < size; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");
}
